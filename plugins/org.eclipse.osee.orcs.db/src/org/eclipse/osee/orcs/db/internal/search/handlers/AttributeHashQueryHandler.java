/*******************************************************************************
 * Copyright (c) 2018 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.db.internal.search.handlers;

import java.util.Iterator;
import java.util.List;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaAttributeHash;
import org.eclipse.osee.orcs.db.internal.search.SearchTermHash;
import org.eclipse.osee.orcs.db.internal.sql.AbstractSqlWriter;
import org.eclipse.osee.orcs.db.internal.sql.SqlHandler;
import org.eclipse.osee.orcs.db.internal.sql.TableEnum;

/**
 * @author Ryan D. Brooks
 */
public class AttributeHashQueryHandler extends SqlHandler<CriteriaAttributeHash> {
   private String attTxsAlias;
   private String attAlias;
   private String artAlias;
   private CriteriaAttributeHash criteria;
   private final SearchTermHash hasher = new SearchTermHash();
   private List<List<Long>> hashes;

   @Override
   public void setData(CriteriaAttributeHash criteria) {
      this.criteria = criteria;
   }

   @Override
   public void addTables(AbstractSqlWriter writer) {
      hashes = hasher.getTermHashes(criteria.getValues(), criteria.getOption());
      int hashTableCount = countNeededHashTables();

      for (int i = 0; i < hashTableCount; i++) {
         writer.addTable(TableEnum.SEARCH_HASH_TABLE);
      }
      attAlias = writer.addTable(TableEnum.ATTRIBUTE_TABLE);
      artAlias = writer.getMainTableAlias(TableEnum.ARTIFACT_TABLE);
      attTxsAlias = writer.addTable(TableEnum.TXS_TABLE);
   }

   private int countNeededHashTables() {
      int hashTableCount = 0;
      Iterator<List<Long>> iterator = hashes.iterator();
      List<Long> singleTokens = iterator.next();
      if (!singleTokens.isEmpty()) {
         hashTableCount++;
      }
      while (iterator.hasNext()) {
         List<Long> list = iterator.next();
         hashTableCount += list.size();
      }
      return hashTableCount;
   }

   /**
    * Combine all single tokens together using a simple in clause
    */
   private void writeHashTableForSingleTokens(AbstractSqlWriter writer, String hshAlias, List<Long> singleTokens) {
      writer.write("(");
      writer.writeEqualsParameterAnd(hshAlias, "app_id", 1);
      if (singleTokens.size() == 1) {
         writer.writeEqualsParameter(hshAlias, "hash", singleTokens.get(0));
      } else {
         writer.write(hshAlias);
         writer.write(".hash IN (");
         boolean notFirst = false;
         for (Long hash : singleTokens) {
            if (notFirst) {
               writer.write(", ?");
            } else {
               writer.write("?");
               notFirst = true;
            }
            writer.addParameter(hash);
         }
         writer.write(")");
      }
      writer.write(" AND ");
      writer.writeEquals(hshAlias, attAlias, "gamma_id");
      writer.write(")");
   }

   private void addHashPredicates(AbstractSqlWriter writer) {
      Iterator<String> aliases = writer.getAliases(TableEnum.SEARCH_HASH_TABLE).iterator();
      Iterator<List<Long>> hashIterator = hashes.iterator();
      String prevHshAlias = aliases.next();
      List<Long> singleTokens = hashIterator.next();

      writer.write("(");
      boolean hasSingleTokens = !singleTokens.isEmpty();
      if (hasSingleTokens) {
         writeHashTableForSingleTokens(writer, prevHshAlias, singleTokens);
      }

      boolean useOr = hasSingleTokens;
      while (hashIterator.hasNext()) {
         if (useOr) {
            writer.write(" OR\n");
         } else {
            useOr = true;
         }
         writer.write(" (");

         boolean joinGammaIds = false;
         for (Long hash : hashIterator.next()) {
            String hshAlias = aliases.next();
            if (joinGammaIds) {
               writer.writeEquals(prevHshAlias, hshAlias, "gamma_id");
               writer.write(" AND ");
            } else {
               joinGammaIds = true;
            }
            writer.write(hshAlias);
            writer.write(".app_id = ? AND ");
            writer.addParameter(1);
            writer.write(hshAlias);
            writer.write(".hash = ? AND ");
            writer.addParameter(hash);
            prevHshAlias = hshAlias;
         }
         writer.writeEquals(prevHshAlias, attAlias, "gamma_id");
         writer.write(")");
      }
      writer.write(") AND\n ");
   }

   @Override
   public void addPredicates(AbstractSqlWriter writer) {
      addHashPredicates(writer);
      if (criteria.getAttributeType().isValid()) {
         writer.writeEqualsParameterAnd(attAlias, "attr_type_id", criteria.getAttributeType());
      }
      writer.writeEquals(attAlias, artAlias, "art_id");
      writer.write(" AND ");
      writer.writeEquals(attAlias, attTxsAlias, "gamma_id");
      writer.write(" AND ");
      writer.write(writer.getTxBranchFilter(attTxsAlias));
   }

   @Override
   public int getPriority() {
      return SqlHandlerPriority.ATTRIBUTE_TOKENIZED_VALUE.ordinal();
   }
}