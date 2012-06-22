/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.artifact;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.IRelationType;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.enums.TxChange;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.framework.core.model.type.ArtifactType;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.database.core.IdJoinQuery;
import org.eclipse.osee.framework.database.core.JoinUtility;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.model.ArtifactEvent;
import org.eclipse.osee.framework.skynet.core.event.model.EventBasicGuidArtifact;
import org.eclipse.osee.framework.skynet.core.event.model.EventChangeTypeBasicGuidArtifact;
import org.eclipse.osee.framework.skynet.core.internal.Activator;
import org.eclipse.osee.framework.skynet.core.internal.ServiceUtil;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;
import org.eclipse.osee.framework.skynet.core.relation.RelationManager;
import org.eclipse.osee.framework.skynet.core.relation.RelationTypeManager;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;

/**
 * Changes the descriptor type of an artifact to the provided descriptor.
 * 
 * @author Jeff C. Phillips
 */
public class ChangeArtifactType {
   private final HashSet<IAttributeType> attributeTypes = new HashSet<IAttributeType>();
   private final HashSet<IRelationType> relationTypes = new HashSet<IRelationType>();
   private final HashMap<IOseeBranch, SkynetTransaction> txMap = new HashMap<IOseeBranch, SkynetTransaction>();
   private final Set<EventBasicGuidArtifact> artifactChanges = new HashSet<EventBasicGuidArtifact>();
   private final List<Artifact> modifiedArtifacts = new ArrayList<Artifact>();
   private static final IStatus promptStatus = new Status(IStatus.WARNING, Activator.PLUGIN_ID, 257, "", null);

   public static void changeArtifactType(Collection<? extends Artifact> inputArtifacts, IArtifactType newArtifactTypeToken) throws OseeCoreException {

      ChangeArtifactType app = new ChangeArtifactType();
      if (inputArtifacts.isEmpty()) {
         throw new OseeArgumentException("The artifact list can not be empty");
      }

      if (newArtifactTypeToken == null) {
         throw new OseeArgumentException("The new artifact type can not be empty");
      }

      ArtifactType newArtifactType = ArtifactTypeManager.getType(newArtifactTypeToken);

      try {
         app.internalChangeArtifactType(inputArtifacts, newArtifactType);
      } catch (Exception ex) {
         ArtifactQuery.reloadArtifacts(app.modifiedArtifacts);
         OseeExceptions.wrapAndThrow(ex);
      }

   }

   /**
    * the order of operations for this BLAM is important. Since the representations for the Attribute Type and the
    * associated attributes and relations are both in memory and in the database, it is important complete both
    * memory/database changes in such a manner that they stay in sync therefore, if any part of this blam fails, then
    * the type should not be changed
    */
   private void internalChangeArtifactType(Collection<? extends Artifact> inputArtifacts, ArtifactType newArtifactType) throws OseeDataStoreException, OseeCoreException {

      createAttributeRelationTransactions(inputArtifacts, newArtifactType);
      boolean changeOk = doesUserAcceptArtifactChange(newArtifactType);

      if (!changeOk) {
         ArtifactQuery.reloadArtifacts(modifiedArtifacts);
         return;
      }

      for (SkynetTransaction transaction : txMap.values()) {
         transaction.execute();
      }

      changeArtifactTypeOutsideofHistory(inputArtifacts, newArtifactType);

      sendLocalAndRemoteEvents(modifiedArtifacts);
   }

   private void sendLocalAndRemoteEvents(Collection<? extends Artifact> artifacts) throws OseeCoreException {
      ArtifactEvent artifactEvent = new ArtifactEvent(artifacts.iterator().next().getBranch());
      for (EventBasicGuidArtifact guidArt : artifactChanges) {
         artifactEvent.getArtifacts().add(guidArt);
      }

      OseeEventManager.kickPersistEvent(ChangeArtifactType.class, artifactEvent);
   }

   private void createAttributeRelationTransactions(Collection<? extends Artifact> inputArtifacts, ArtifactType newArtifactType) throws OseeDataStoreException, OseeCoreException {
      IdJoinQuery artifactJoin = populateJoinIdTable(inputArtifacts);
      IOseeDatabaseService database = ServiceUtil.getOseeDatabaseService();
      IOseeStatement chStmt = database.getStatement();

      try {
         chStmt.runPreparedQuery(
            "select art_id, branch_id from osee_join_id jid, osee_artifact art, osee_txs txs where jid.query_id = ? and jid.id = art.art_id and art.gamma_id = txs.gamma_id and txs.tx_current = ?",
            artifactJoin.getQueryId(), TxChange.CURRENT.getValue());

         while (chStmt.next()) {
            int artId = chStmt.getInt("art_id");
            int branchId = chStmt.getInt("branch_id");
            IOseeBranch branch = BranchManager.getBranch(branchId);
            Artifact artifact = ArtifactQuery.getArtifactFromId(artId, branch);
            deleteInvalidAttributes(artifact, newArtifactType);
            deleteInvalidRelations(artifact, newArtifactType);
            addTransaction(artifact, txMap);
            artifactChanges.add(new EventChangeTypeBasicGuidArtifact(artifact.getBranch().getGuid(),
               newArtifactType.getGuid(), newArtifactType.getGuid(), artifact.getGuid()));
         }
      } finally {
         chStmt.close();
         artifactJoin.delete();
      }
   }

   private void addTransaction(Artifact artifact, HashMap<IOseeBranch, SkynetTransaction> txMap) throws OseeCoreException {
      IOseeBranch branch = artifact.getBranch();
      SkynetTransaction transaction = txMap.get(branch);
      if (transaction == null) {
         transaction = TransactionManager.createTransaction(branch, "Change Artifact Type");
         txMap.put(branch, transaction);
      }
      transaction.addArtifact(artifact);
      modifiedArtifacts.add(artifact);
   }

   public static void handleRemoteChangeType(EventChangeTypeBasicGuidArtifact guidArt) {
      try {
         Artifact artifact = ArtifactCache.getActive(guidArt);
         if (artifact == null) {
            return;
         }
         ArtifactCache.deCache(artifact);
         RelationManager.deCache(artifact);
         artifact.setArtifactType(ArtifactTypeManager.getType(guidArt));
         artifact.clearEditState();
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, "Error handling remote change type", ex);
      }
   }

   private void getConflictString(StringBuilder message, IArtifactType artifactType) {
      message.append("The following types are not supported on the artifact type " + artifactType.getName() + ":\n");
      message.append("\n");
      message.append("Attribute Types:\n" + attributeTypes + "\n");
      message.append("\n");
      message.append("Relation Types:\n" + relationTypes + "\n");
   }

   private void deleteInvalidAttributes(Artifact artifact, IArtifactType artifactType) throws OseeCoreException {

      for (IAttributeType attributeType : artifact.getAttributeTypes()) {
         ArtifactType aType = ArtifactTypeManager.getType(artifactType);
         if (!aType.isValidAttributeType(attributeType, artifact.getFullBranch())) {
            artifact.deleteAttributes(attributeType);
            attributeTypes.add(attributeType);
         }
      }
   }

   private void deleteInvalidRelations(Artifact artifact, IArtifactType artifactType) throws OseeCoreException {

      for (RelationLink link : artifact.getRelationsAll(DeletionFlag.EXCLUDE_DELETED)) {
         if (RelationTypeManager.getRelationSideMax(link.getRelationType(), artifactType, link.getSide(artifact)) == 0) {
            link.delete(false);
            relationTypes.add(link.getRelationType());
         }
      }
   }

   /**
    * @return true if the user accepts the deletion of the attributes and relations that are not compatible for the new
    * artifact type else false.
    */
   private boolean doesUserAcceptArtifactChange(IArtifactType artifactType) throws OseeCoreException {
      if (!relationTypes.isEmpty() || !attributeTypes.isEmpty()) {

         StringBuilder sb = new StringBuilder(1024);
         getConflictString(sb, artifactType);

         Object result;
         try {
            result = DebugPlugin.getDefault().getStatusHandler(promptStatus).handleStatus(promptStatus, sb.toString());
         } catch (CoreException ex) {
            OseeExceptions.wrapAndThrow(ex);
            // the following will never execute - above line always exceptions
            return false;
         }

         return (Boolean) result;

      } else {
         return true;
      }
   }

   private void changeArtifactTypeOutsideofHistory(Collection<? extends Artifact> inputArtifacts, ArtifactType newArtifactType) throws OseeCoreException {
      List<Object[]> insertData = new ArrayList<Object[]>();

      String UPDATE = "UPDATE osee_artifact SET art_type_id = ? WHERE art_id = ?";

      for (Artifact artifact : inputArtifacts) {
         insertData.add(toUpdate(newArtifactType.getId(), artifact.getArtId()));
      }

      ServiceUtil.getOseeDatabaseService().runBatchUpdate(UPDATE, insertData);

      for (Artifact artifact : modifiedArtifacts) {
         artifact.setArtifactType(newArtifactType);
         artifact.clearEditState();
      }
   }

   private Object[] toUpdate(int art_type_id, int art_id) {
      return new Object[] {art_type_id, art_id};
   }

   private IdJoinQuery populateJoinIdTable(Collection<? extends Artifact> inputArtifacts) throws OseeDataStoreException, OseeCoreException {
      IdJoinQuery artifactJoin = JoinUtility.createIdJoinQuery();

      for (Artifact artifact : inputArtifacts) {
         artifactJoin.add(artifact.getArtId());
      }

      artifactJoin.store();

      return artifactJoin;
   }
}
