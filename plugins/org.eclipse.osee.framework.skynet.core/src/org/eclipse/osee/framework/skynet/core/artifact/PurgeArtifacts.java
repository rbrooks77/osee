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

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.database.core.AbstractDbTxOperation;
import org.eclipse.osee.framework.database.core.ArtifactJoinQuery;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.database.core.JoinUtility;
import org.eclipse.osee.framework.database.core.OseeConnection;
import org.eclipse.osee.framework.database.core.TransactionJoinQuery;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.model.ArtifactEvent;
import org.eclipse.osee.framework.skynet.core.event.model.EventBasicGuidArtifact;
import org.eclipse.osee.framework.skynet.core.event.model.EventModType;
import org.eclipse.osee.framework.skynet.core.internal.Activator;
import org.eclipse.osee.framework.skynet.core.internal.ServiceUtil;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;

/**
 * @author Ryan D. Brooks
 */
public class PurgeArtifacts extends AbstractDbTxOperation {

   private static final String SELECT_ITEM_GAMMAS =
      "SELECT /*+ ordered */ txs.gamma_id, txs.transaction_id, aj.branch_id FROM osee_join_artifact aj, %s item, osee_txs txs WHERE aj.query_id = ? AND %s AND item.gamma_id = txs.gamma_id AND aj.branch_id = txs.branch_id";

   private static final String COUNT_ARTIFACT_VIOLATIONS =
      "SELECT art.art_id, txs.branch_id FROM osee_join_artifact aj, osee_artifact art, osee_txs txs WHERE aj.query_id = ? AND aj.art_id = art.art_id AND art.gamma_id = txs.gamma_id AND txs.branch_id = aj.branch_id";

   private static final String DELETE_FROM_TXS_USING_JOIN_TRANSACTION =
      "DELETE FROM osee_txs txs WHERE EXISTS (select 1 from osee_join_transaction jt WHERE jt.query_id = ? AND jt.branch_id = txs.branch_id AND jt.gamma_id = txs.gamma_id AND jt.transaction_id = txs.transaction_id)";

   private static final String DELETE_FROM_TX_DETAILS_USING_JOIN_TRANSACTION =
      "DELETE FROM osee_tx_details txd WHERE EXISTS (select 1 from osee_join_transaction jt WHERE jt.query_id = ? AND jt.branch_id = txd.branch_id AND jt.transaction_id = txd.transaction_id AND not exists (select * from osee_txs txs where jt.branch_id = txs.branch_id and jt.transaction_id = txs.transaction_id))";

   private final List<Artifact> artifactsToPurge;
   private boolean success;
   private final boolean recurseChildrenBranches;

   public PurgeArtifacts(Collection<? extends Artifact> artifactsToPurge) throws OseeCoreException {
      this(artifactsToPurge, false);
   }

   public PurgeArtifacts(Collection<? extends Artifact> artifactsToPurge, boolean recurseChildrenBranches) throws OseeCoreException {
      super(ServiceUtil.getOseeDatabaseService(), "Purge Artifact", Activator.PLUGIN_ID);
      this.artifactsToPurge = new LinkedList<Artifact>(artifactsToPurge);
      this.success = false;
      this.recurseChildrenBranches = recurseChildrenBranches;
   }

   @Override
   protected void doTxWork(IProgressMonitor monitor, OseeConnection connection) throws OseeCoreException {
      if (artifactsToPurge == null || artifactsToPurge.isEmpty()) {
         return;
      }

      checkPurgeValid(connection);

      // now load the artifacts to be purged
      Set<Artifact> childreArtifactsToPurge = new HashSet<Artifact>();
      for (Artifact art : artifactsToPurge) {
         childreArtifactsToPurge.addAll(art.getDescendants(DeletionFlag.INCLUDE_DELETED));
      }
      artifactsToPurge.addAll(childreArtifactsToPurge);

      ArtifactJoinQuery artJoin2 = JoinUtility.createArtifactJoinQuery(getDatabaseService());
      try {
         for (Artifact art : artifactsToPurge) {
            artJoin2.add(art.getArtId(), art.getFullBranch().getUuid());
         }
         artJoin2.store(connection);

         int queryId = artJoin2.getQueryId();

         TransactionJoinQuery txJoin = JoinUtility.createTransactionJoinQuery(getDatabaseService());

         insertSelectItems(txJoin, connection, "osee_relation_link",
            "(aj.art_id = item.a_art_id OR aj.art_id = item.b_art_id)", queryId);
         insertSelectItems(txJoin, connection, "osee_attribute", "aj.art_id = item.art_id", queryId);
         insertSelectItems(txJoin, connection, "osee_artifact", "aj.art_id = item.art_id", queryId);

         try {
            txJoin.store(connection);
            getDatabaseService().runPreparedUpdate(connection, DELETE_FROM_TXS_USING_JOIN_TRANSACTION,
               txJoin.getQueryId());
            getDatabaseService().runPreparedUpdate(connection, DELETE_FROM_TX_DETAILS_USING_JOIN_TRANSACTION,
               txJoin.getQueryId());
         } finally {
            txJoin.delete(connection);
         }

         for (Artifact artifact : artifactsToPurge) {
            ArtifactCache.deCache(artifact);
            artifact.internalSetDeleted();
            for (RelationLink rel : artifact.getRelationsAll(DeletionFlag.EXCLUDE_DELETED)) {
               rel.markAsPurged();
            }
            for (Attribute<?> attr : artifact.internalGetAttributes()) {
               attr.markAsPurged();
            }
         }
         success = true;
      } finally {
         artJoin2.delete(connection);
      }
   }

   @Override
   protected void handleTxFinally(IProgressMonitor monitor) throws OseeCoreException {
      if (success) {
         Set<EventBasicGuidArtifact> artifactChanges = new HashSet<EventBasicGuidArtifact>();
         for (Artifact artifact : artifactsToPurge) {
            artifactChanges.add(new EventBasicGuidArtifact(EventModType.Purged, artifact));
         }
         // Kick Local and Remote Events
         ArtifactEvent artifactEvent = new ArtifactEvent(artifactsToPurge.iterator().next().getBranch());
         for (EventBasicGuidArtifact guidArt : artifactChanges) {
            artifactEvent.getArtifacts().add(guidArt);
         }
         OseeEventManager.kickPersistEvent(PurgeArtifacts.class, artifactEvent);
      }
   }

   public void insertSelectItems(TransactionJoinQuery txJoin, OseeConnection connection, String tableName, String artifactJoinSql, int queryId) throws OseeCoreException {
      String query = String.format(SELECT_ITEM_GAMMAS, tableName, artifactJoinSql);
      IOseeStatement chStmt = getDatabaseService().getStatement(connection);
      try {
         chStmt.runPreparedQuery(query, queryId);
         while (chStmt.next()) {
            txJoin.add(chStmt.getLong("gamma_id"), chStmt.getInt("transaction_id"), chStmt.getLong("branch_id"));
         }
      } finally {
         chStmt.close();
      }
   }

   private void checkPurgeValid(OseeConnection connection) {
      ArtifactJoinQuery artJoin = JoinUtility.createArtifactJoinQuery(getDatabaseService());
      for (Artifact art : artifactsToPurge) {
         for (Branch branch : art.getFullBranch().getChildBranches(true)) {
            artJoin.add(art.getArtId(), branch.getUuid());
         }
      }
      if (!artJoin.isEmpty()) {
         try {
            artJoin.store(connection);
            IOseeStatement chStmt = getDatabaseService().getStatement(connection);
            try {
               chStmt.runPreparedQuery(COUNT_ARTIFACT_VIOLATIONS, artJoin.getQueryId());
               boolean failed = false;
               StringBuilder sb = new StringBuilder();
               while (chStmt.next()) {
                  int artId = chStmt.getInt("art_id");
                  long branchUuid = chStmt.getLong("branch_id");
                  if (recurseChildrenBranches) {
                     Branch branch = BranchManager.getBranch(branchUuid);
                     Artifact artifactFromId = ArtifactQuery.getArtifactFromId(artId, branch);
                     artifactsToPurge.add(artifactFromId);
                  } else {
                     failed = true;
                     sb.append("ArtifactId[");
                     sb.append(artId);
                     sb.append("] BranchId[");
                     sb.append(branchUuid);
                     sb.append("]\n");
                  }
               }
               if (failed) {
                  throw new OseeCoreException(
                     "Unable to purge because the following artifacts exist on child branches.\n%s", sb.toString());
               }
            } finally {
               chStmt.close();
            }
         } finally {
            artJoin.delete(connection);
         }
      }
   }

}