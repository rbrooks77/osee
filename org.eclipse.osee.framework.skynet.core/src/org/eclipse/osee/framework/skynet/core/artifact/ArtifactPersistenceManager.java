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

import static org.eclipse.osee.framework.database.sql.SkynetDatabase.ARTIFACT_TABLE;
import static org.eclipse.osee.framework.database.sql.SkynetDatabase.ARTIFACT_VERSION_TABLE;
import static org.eclipse.osee.framework.database.sql.SkynetDatabase.ATTRIBUTE_VERSION_TABLE;
import static org.eclipse.osee.framework.database.sql.SkynetDatabase.RELATION_LINK_VERSION_TABLE;
import static org.eclipse.osee.framework.database.sql.SkynetDatabase.TRANSACTIONS_TABLE;
import static org.eclipse.osee.framework.database.sql.SkynetDatabase.TRANSACTION_DETAIL_TABLE;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.osee.framework.core.enums.TransactionDetailsType;
import org.eclipse.osee.framework.core.enums.TxChange;
import org.eclipse.osee.framework.core.exception.BranchDoesNotExist;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.database.core.ConnectionHandlerStatement;
import org.eclipse.osee.framework.database.core.OseeConnection;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.artifact.search.ISearchPrimitive;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;
import org.eclipse.osee.framework.skynet.core.relation.RelationManager;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionIdManager;

/**
 * @author Ryan D. Brooks
 * @author Robert A. Fisher
 */
public class ArtifactPersistenceManager {
   private static final String PURGE_BASELINE_ATTRIBUTE_TRANS =
         "DELETE from " + TRANSACTIONS_TABLE + " T2 WHERE EXISTS (SELECT 'x' from " + TRANSACTION_DETAIL_TABLE + " T1, " + ATTRIBUTE_VERSION_TABLE + " T3 WHERE T1.transaction_id = T2.transaction_id and T3.gamma_id = T2.gamma_id and T1.tx_type = " + TransactionDetailsType.Baselined.getId() + " and T1.branch_id = ? and T3.art_id = ?)";
   private static final String PURGE_BASELINE_RELATION_TRANS =
         "DELETE from " + TRANSACTIONS_TABLE + " T2 WHERE EXISTS (SELECT 'x' from " + TRANSACTION_DETAIL_TABLE + " T1, " + RELATION_LINK_VERSION_TABLE + " T3 WHERE T1.transaction_id = T2.transaction_id and T3.gamma_id = T2.gamma_id and T1.tx_type = " + TransactionDetailsType.Baselined.getId() + " and T1.branch_id = ? and (T3.a_art_id = ? or T3.b_art_id = ?))";
   private static final String PURGE_BASELINE_ARTIFACT_TRANS =
         "DELETE from " + TRANSACTIONS_TABLE + " T2 WHERE EXISTS (SELECT 'x' from " + TRANSACTION_DETAIL_TABLE + " T1, " + ARTIFACT_VERSION_TABLE + " T3 WHERE T1.transaction_id = T2.transaction_id and T3.gamma_id = T2.gamma_id and T1.tx_type = " + TransactionDetailsType.Baselined.getId() + " and T1.branch_id = ? and T3.art_id = ?)";

   private static final String GET_GAMMAS_ARTIFACT_REVERT =
         "SELECT txs1.gamma_id, txd1.tx_type, txs1.transaction_id  FROM osee_tx_details txd1, osee_txs  txs1, osee_attribute atr1 where txd1.transaction_id = txs1.transaction_id and txs1.gamma_id = atr1.gamma_id and txd1.branch_id = ? and atr1.art_id = ? UNION ALL SELECT txs2.gamma_id, txd2.tx_type, txs2.transaction_id FROM osee_tx_details txd2, osee_txs txs2, osee_relation_link rel2 where txd2.transaction_id = txs2.transaction_id and txs2.gamma_id = rel2.gamma_id and txd2.branch_id = ? and (rel2.a_art_id = ? or rel2.b_art_id = ?) UNION ALL SELECT txs3.gamma_id, txd3.tx_type, txs3.transaction_id FROM osee_tx_details txd3, osee_txs txs3, osee_artifact_version art3 where txd3.transaction_id = txs3.transaction_id and txs3.gamma_id = art3.gamma_id and txd3.branch_id = ? and art3.art_id = ?";

   private static final String GET_GAMMAS_RELATION_REVERT =
         "SELECT txs2.gamma_id, txd2.tx_type, txs2.transaction_id FROM osee_tx_details txd2, osee_txs txs2, osee_relation_link rel2 where txd2.transaction_id = txs2.transaction_id and txs2.gamma_id = rel2.gamma_id and txd2.branch_id = ? and rel2.rel_link_id = ?";

   private static final String GET_GAMMAS_ATTRIBUTE_REVERT =
         "SELECT txs2.gamma_id, txd2.tx_type, txs2.transaction_id FROM osee_tx_details txd2, osee_txs txs2, osee_attribute atr2 where txd2.transaction_id = txs2.transaction_id and txs2.gamma_id = atr2.gamma_id and txd2.branch_id = ? and atr2.attr_id = ?";

   private static final String ARTIFACT_SELECT =
         "SELECT osee_artifact.art_id, txd1.branch_id FROM osee_artifact, osee_artifact_version arv1, osee_txs txs1, osee_tx_details txd1 WHERE " + ARTIFACT_TABLE.column("art_id") + "=arv1.art_id AND arv1.gamma_id=txs1.gamma_id AND txs1.tx_current=" + TxChange.CURRENT.getValue() + " AND txs1.transaction_id = txd1.transaction_id AND txd1.branch_id=? AND ";

   private static final String ARTIFACT_ID_SELECT =
         "SELECT " + ARTIFACT_TABLE.columns("art_id") + " FROM " + ARTIFACT_TABLE + " WHERE ";

   private static final String ARTIFACT_NEW_ON_BRANCH =
         "Select det.tx_type from osee_tx_details det, osee_txs txs, osee_artifact_version art WHERE det.branch_id = ? AND det.tx_type = 1 AND det.transaction_id = txs.transaction_id AND txs.gamma_id = art.gamma_id AND art.art_id = ?";
   private static final String RELATION_NEW_ON_BRANCH =
         "Select det.tx_type from osee_tx_details det, osee_txs txs, osee_relation_link rel WHERE det.branch_id = ? AND det.tx_type = 1 AND det.transaction_id = txs.transaction_id AND txs.gamma_id = rel.gamma_id AND rel.rel_link_id = ?";

   public static CharSequence getSelectArtIdSql(ISearchPrimitive searchCriteria, List<Object> dataList, Branch branch) {
      return getSelectArtIdSql(searchCriteria, dataList, null, branch);
   }

   private static CharSequence getSelectArtIdSql(ISearchPrimitive searchCriteria, List<Object> dataList, String alias, Branch branch) {
      StringBuilder sql = new StringBuilder();

      sql.append("SELECT ");
      sql.append(searchCriteria.getArtIdColName());

      if (alias != null) {
         sql.append(" AS " + alias);
      }

      sql.append(" FROM ");
      sql.append(searchCriteria.getTableSql(dataList, branch));

      String criteriaSql = searchCriteria.getCriteriaSql(dataList, branch);
      if (criteriaSql.trim().length() != 0) {
         sql.append(" WHERE (");
         sql.append(criteriaSql);
         sql.append(")");
      }

      return sql;
   }

   public static String getIdSql(List<ISearchPrimitive> searchCriteria, boolean all, List<Object> dataList, Branch branch) {
      return getSql(searchCriteria, all, ARTIFACT_ID_SELECT, dataList, branch);
   }

   private static String getSql(List<ISearchPrimitive> searchCriteria, boolean all, String header, List<Object> dataList, Branch branch) {
      StringBuilder sql = new StringBuilder(header);

      if (all) {
         ISearchPrimitive primitive = null;
         Iterator<ISearchPrimitive> iter = searchCriteria.iterator();

         while (iter.hasNext()) {
            primitive = iter.next();
            sql.append(ARTIFACT_TABLE.column("art_id") + " in (");
            sql.append(getSelectArtIdSql(primitive, dataList, branch));

            if (iter.hasNext()) {
               sql.append(") AND ");
            }
         }
         sql.append(")");
      } else {
         ISearchPrimitive primitive = null;
         Iterator<ISearchPrimitive> iter = searchCriteria.iterator();

         sql.append(ARTIFACT_TABLE.column("art_id") + " IN(SELECT art_id FROM " + ARTIFACT_TABLE + ", (");

         while (iter.hasNext()) {
            primitive = iter.next();
            sql.append(getSelectArtIdSql(primitive, dataList, "desired_art_id", branch));
            if (iter.hasNext()) sql.append(" UNION ALL ");
         }
         sql.append(") ORD_ARTS");
         sql.append(" WHERE art_id = ORD_ARTS.desired_art_id");

         sql.append(")");
      }

      return sql.toString();
   }

   @Deprecated
   public static Collection<Artifact> getArtifacts(List<ISearchPrimitive> searchCriteria, boolean all, Branch branch, ISearchConfirmer confirmer) throws OseeCoreException {
      LinkedList<Object> queryParameters = new LinkedList<Object>();
      queryParameters.add(branch.getBranchId());
      return ArtifactLoader.getArtifacts(getSql(searchCriteria, all, ARTIFACT_SELECT, queryParameters, branch),
            queryParameters.toArray(), 100, ArtifactLoad.FULL, false, confirmer, null, false);
   }

   /**
    * @param transaction if the transaction is null then persist is not called otherwise
    * @param overrideDeleteCheck if <b>true</b> deletes without checking preconditions
    * @param artifacts The artifacts to delete.
    */
   public static void deleteArtifact(SkynetTransaction transaction, boolean overrideDeleteCheck, final Artifact... artifacts) throws OseeCoreException {
      if (artifacts.length == 0) return;

      if (!overrideDeleteCheck) {
         // Confirm artifacts are fit to delete
         for (IArtifactCheck check : ArtifactChecks.getArtifactChecks()) {
            IStatus result = check.isDeleteable(Arrays.asList(artifacts));
            if (!result.isOK()) {
               throw new OseeStateException(result.getMessage());
            }
         }
      }
      //Bulk Load Artifacts
      Collection<Integer> artIds = new LinkedList<Integer>();
      for (Artifact artifact : artifacts) {
         for (RelationLink link : artifact.getRelationsAll(false)) {
            if (link.getRelationType().isOrdered()) {
               artIds.add(artifact.getArtId() == link.getAArtifactId() ? link.getBArtifactId() : link.getAArtifactId());
            }
         }
      }
      Branch branch = artifacts[0].getBranch();
      ArtifactQuery.getArtifactListFromIds(artIds, branch, true);

      for (Artifact artifact : artifacts) {
         deleteTrace(artifact, transaction, true);
      }
   }

   /**
    * @param artifact
    * @param builder
    * @param reorderReloations
    * @throws Exception
    */
   private static void deleteTrace(Artifact artifact, SkynetTransaction transaction, boolean reorderRelations) throws OseeCoreException {
      if (!artifact.isDeleted()) {
         // This must be done first since the the actual deletion of an artifact clears out the link manager
         for (Artifact childArtifact : artifact.getChildren()) {
            deleteTrace(childArtifact, transaction, false);
         }
         try {
            artifact.internalSetDeleted();
            RelationManager.deleteRelationsAll(artifact, reorderRelations);
            if (transaction != null) {
               artifact.persistAttributesAndRelations(transaction);
            }
         } catch (OseeCoreException ex) {
            artifact.resetToPreviousModType();
            throw ex;
         }
      }
   }

   /**
    * Removes an artifact, it's attributes and any relations that have become invalid from the removal of this artifact
    * from the database. It also removes all history associated with this artifact (i.e. all transactions and gamma ids
    * will also be removed from the database only for the branch it is on).
    * 
    * @param artifact
    */
   public static void purgeArtifactFromBranch(OseeConnection connection, int branchId, int artId) throws OseeCoreException {
      revertArtifact(connection, branchId, artId);

      //Remove from Baseline
      ConnectionHandler.runPreparedUpdate(connection, PURGE_BASELINE_ATTRIBUTE_TRANS, branchId, artId);
      ConnectionHandler.runPreparedUpdate(connection, PURGE_BASELINE_RELATION_TRANS, branchId, artId, artId);
      ConnectionHandler.runPreparedUpdate(connection, PURGE_BASELINE_ARTIFACT_TRANS, branchId, artId);
   }

   public static void revertAttribute(OseeConnection connection, Attribute<?> attribute) throws OseeCoreException {
      if (attribute == null) return;
      revertAttribute(connection, attribute.getArtifact().getBranch().getBranchId(),
            attribute.getArtifact().getArtId(), attribute.getAttrId());
   }

   public static void revertAttribute(OseeConnection connection, int branchId, int artId, int attributeId) throws OseeCoreException {
      TransactionId transId =
            TransactionIdManager.createNextTransactionId(BranchManager.getBranch(branchId), UserManager.getUser(), "");
      long totalTime = System.currentTimeMillis();
      //Get attribute Gammas
      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement(connection);
      RevertAction revertAction = null;
      try {
         chStmt.runPreparedQuery(GET_GAMMAS_ATTRIBUTE_REVERT, branchId, attributeId);
         revertAction = new RevertAction(connection, chStmt, transId);
         revertAction.revertObject(totalTime, artId, "Attribute");
      } finally {
         chStmt.close();
      }
      revertAction.fixArtifactVersionForAttributeRevert(branchId, artId);
   }

   /**
    * Should NOT be used for relation types that maintain order. Not handled yet
    */
   public static void revertRelationLink(OseeConnection connection, RelationLink link) throws OseeCoreException {
      //Only reverts relation links that don't span multiple branches.  Need to revisit if additional functionality is needed.
      if (!link.getArtifactA().getBranch().equals(link.getArtifactB().getBranch())) {
         throw new OseeArgumentException(String.format("Can not revert Relation %d. Relation spans multiple branches",
               link.getRelationId()));
      }
      revertRelationLink(connection, link.getArtifactA().getBranch().getBranchId(), link.getRelationId(),
            link.getArtifactA().getArtId(), link.getArtifactB().getArtId());
   }

   private static void revertRelationLink(OseeConnection connection, int branchId, int relLinkId, int aArtId, int bArtId) throws BranchDoesNotExist, OseeCoreException {
      long time = System.currentTimeMillis();
      long totalTime = time;
      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement(connection);

      TransactionId transId =
            TransactionIdManager.createNextTransactionId(BranchManager.getBranch(branchId), UserManager.getUser(), "");

      try {
         chStmt.runPreparedQuery(GET_GAMMAS_RELATION_REVERT, branchId, relLinkId);
         new RevertAction(connection, chStmt, transId).revertObject(totalTime, relLinkId, "Relation Link");
      } finally {
         chStmt.close();
      }
   }

   public static void revertArtifact(OseeConnection connection, Artifact artifact) throws OseeCoreException {
      if (artifact == null) return;
      revertArtifact(connection, artifact.getBranch().getBranchId(), artifact.getArtId());
   }

   public static void revertArtifact(OseeConnection connection, int branchId, int artId) throws OseeCoreException {
      TransactionId transId =
            TransactionIdManager.createNextTransactionId(BranchManager.getBranch(branchId), UserManager.getUser(), "");
      long totalTime = System.currentTimeMillis();
      //Get attribute Gammas
      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement(connection);
      try {
         chStmt.runPreparedQuery(GET_GAMMAS_ARTIFACT_REVERT, branchId, artId, branchId, artId, artId, branchId, artId);
         new RevertAction(connection, chStmt, transId).revertObject(totalTime, artId, "Artifact");
      } finally {
         chStmt.close();
      }
   }

   public static void purgeArtifacts(Collection<? extends Artifact> artifactsToPurge) throws OseeCoreException {
      new PurgeDbTransaction(artifactsToPurge).execute();
   }

   public static boolean isArtifactNewOnBranch(Artifact artifact) throws OseeDataStoreException {
      return (ConnectionHandler.runPreparedQueryFetchInt(-1, ARTIFACT_NEW_ON_BRANCH,
            artifact.getBranch().getBranchId(), artifact.getArtId()) == -1);
   }

   public static boolean isRelationNewOnBranch(RelationLink relation, Branch branch) throws OseeDataStoreException {
      return (ConnectionHandler.runPreparedQueryFetchInt(-1, RELATION_NEW_ON_BRANCH, branch.getBranchId(),
            relation.getRelationId()) == -1);
   }
}