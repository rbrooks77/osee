/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.db.internal.search.engines;

import static org.eclipse.osee.jdbc.JdbcConstants.JDBC__MAX_FETCH_SIZE;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcStatement;
import org.eclipse.osee.jdbc.OseePreparedStatement;
import org.eclipse.osee.orcs.data.ArtifactTypes;
import org.eclipse.osee.orcs.db.internal.sql.join.IdJoinQuery;
import org.eclipse.osee.orcs.search.DeepQuery;

/**
 * @author Ryan D. Brooks
 */
public class DeepQueryImpl implements DeepQuery {
   private static final String SELECT_ATTRIBUTE_VALUES =
      "select att.art_id, value from osee_attribute att, osee_artifact art, osee_join_id jid where attr_type_id = ? and value is not null and att.art_id = art.art_id and art_type_id = jid.id and jid.query_id = ?";
   private final JdbcClient jdbcClient;
   private final ArtifactTypes artifactTypeService;
   private final IdJoinQuery arifactTypeJoin;

   public DeepQueryImpl(JdbcClient jdbcClient, ArtifactTypes artifactTypeService, IdJoinQuery arifactTypeJoin) {
      this.jdbcClient = jdbcClient;
      this.artifactTypeService = artifactTypeService;
      this.arifactTypeJoin = arifactTypeJoin;
   }

   @Override
   public DeepQuery andIsOfType(ArtifactTypeId... artifactTypes) {
      for (ArtifactTypeId artifactType : artifactTypes) {
         for (ArtifactTypeId type : artifactTypeService.getAllDescendantTypes(artifactType)) {
            arifactTypeJoin.add(type);
         }
      }
      return this;
   }

   @Override
   public List<Pair<ArtifactId, String>> collect(AttributeTypeId attributeType) {
      List<Pair<ArtifactId, String>> attributeValues = new ArrayList<>(10000);
      arifactTypeJoin.store();

      try (JdbcStatement chStmt = jdbcClient.getStatement()) {
         chStmt.runPreparedQuery(JDBC__MAX_FETCH_SIZE, SELECT_ATTRIBUTE_VALUES, attributeType,
            arifactTypeJoin.getQueryId());
         while (chStmt.next()) {
            attributeValues.add(new Pair<>(ArtifactId.valueOf(chStmt.getLong("art_id")), chStmt.getString("value")));
         }
      } finally {
         arifactTypeJoin.delete();
      }
      return attributeValues;
   }

   @Override
   public int fixApplicOnDelete() {
      int counter = 0;
      try (JdbcStatement chStmt = jdbcClient.getStatement()) {
         String SELECT_LOST_APPLIC =
            "select txs1.app_id, txs2.branch_id, txs2.transaction_id, txs2.gamma_id from osee_txs txs1, osee_txs txs2 where txs1.branch_id = txs2.branch_id and txs1.gamma_id = txs2.gamma_id and txs1.app_id <> txs2.app_id and txs2.mod_type = 3 and txs2.app_id = 1 and txs2.tx_current = 2";
         chStmt.runPreparedQuery(JDBC__MAX_FETCH_SIZE, SELECT_LOST_APPLIC);
         String UPDATE_APPLIC =
            "UPDATE osee_txs SET app_id = ? WHERE branch_id = ? AND transaction_id = ? AND gamma_id = ?";
         OseePreparedStatement addressing = jdbcClient.getBatchStatement(UPDATE_APPLIC);
         while (chStmt.next()) {
            addressing.addToBatch(chStmt.getLong("app_id"), chStmt.getLong("branch_id"),
               chStmt.getLong("transaction_id"), chStmt.getLong("gamma_id"));
            counter++;
         }
         addressing.execute();

      }
      return counter;
   }
}