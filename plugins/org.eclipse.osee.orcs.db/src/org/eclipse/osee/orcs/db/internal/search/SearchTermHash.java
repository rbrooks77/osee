/*******************************************************************************
 * Copyright (c) 218 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.db.internal.search;

import static org.eclipse.osee.jdbc.JdbcConstants.JDBC__MAX_FETCH_SIZE;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcStatement;
import org.eclipse.osee.jdbc.OseePreparedStatement;
import org.eclipse.osee.orcs.db.internal.sql.join.IdJoinQuery;
import org.eclipse.osee.orcs.db.internal.sql.join.SqlJoinFactory;

/**
 * Instances of this class should not be shared among threads. Use multiple term normalization rules and then store the
 * unique set of resulting hashes. Each normalization starts with making all letters lower-case.</br>
 * <li>whole match = no tokenize; keep only alpha-numeric characters
 * <li>original = tokenize on characters that are not digits or letters
 * <li>partial with fuzzy punc = tokenize on whitespace; strip all punctuation
 * <li>maybe not = tokenize on whitespace; strip leading and trailing punctuation; replace remaining punctuation with
 * spaces
 * <ul>
 * <li>{Requirement_Name}
 * <li>use new tablespace called osee_search Replace TagProcessor and use XmlTextInputStream
 *
 * @author Ryan D. Brooks
 */
public class SearchTermHash {
   private static final String SELECT_MISSING_ATTRIBUTES =
      "SELECT gamma_id, value FROM osee_attribute att WHERE attr_type_id = ? AND value is not null AND not exists (SELECT 1 FROM osee_search_hash has WHERE has.gamma_id = att.gamma_id)";

   private static final String SELECT_ATTRIBUTES_BY_TYPE =
      "SELECT gamma_id, value FROM osee_attribute WHERE attr_type_id = ? AND value is not null";

   private static final String SELECT_ATTRIBUTES_BY_GAMMA =
      "SELECT att.gamma_id, value FROM osee_attribute att, osee_join_id WHERE query_id = ? AND id = gamma_id AND value is not null";

   private final SqlJoinFactory joinFactory;
   private final JdbcClient jdbcClient;
   private OseePreparedStatement insertStatement;
   private final HashSet<Long> hashes = new HashSet<>();
   private final List<List<Long>> resultHashes = new ArrayList<>();
   private final List<Long> singleTokens = new ArrayList<>();

   public SearchTermHash() {
      this(null, null);
   }

   public SearchTermHash(JdbcClient jdbcClient, SqlJoinFactory joinFactory) {
      this.jdbcClient = jdbcClient;
      this.joinFactory = joinFactory;
   }

   public int createTermHashes(AttributeTypeId attributeType) {
      return createTermHashes(SELECT_ATTRIBUTES_BY_TYPE, attributeType);
   }

   public int createTermHashes(Iterable<GammaId> gammaIds) {
      try (IdJoinQuery joinQuery = joinFactory.createIdJoinQuery()) {
         joinQuery.addAndStore(gammaIds);
         return createTermHashes(SELECT_ATTRIBUTES_BY_GAMMA, joinQuery.getQueryId());
      }
   }

   private int createTermHashes(String sql, Object... data) {
      insertStatement =
         jdbcClient.getBatchStatement("INSERT INTO osee_search_hash (app_id, hash, gamma_id) VALUES (?, ?, ?)");
      int loaded = jdbcClient.runQuery(this::createTermHashes, JDBC__MAX_FETCH_SIZE, sql, data);
      insertStatement.execute();
      return loaded;
   }

   public List<List<Long>> getTermHashes(Collection<String> values, QueryOption queryOption) {
      resultHashes.clear();
      singleTokens.clear();
      resultHashes.add(singleTokens);
      for (String value : values) {
         switch (queryOption) {
            case TOKENIZE_NON_ALPHANUMERIC:
               addOriginalMatchHash(value);
               break;
            case TOKENIZE_WHITESPACE:
               addPartialFuzzyPuncHash(value);
               break;
            case WHOLE_MATCH:
               getWholeMatchHash(value);
               break;
            default:
               throw new OseeArgumentException("Unexpected query option %s", queryOption);
         }
         copyHashes();
      }
      return resultHashes;
   }

   private void copyHashes() {
      if (hashes.size() == 1) {
         singleTokens.add(hashes.iterator().next());
      } else {
         resultHashes.add(new ArrayList<>(hashes));
      }
      hashes.clear();
   }

   private void createTermHashes(JdbcStatement stmt) {
      String value = stmt.getString("value");
      addOriginalMatchHash(value);
      addPartialFuzzyPuncHash(value);
      getWholeMatchHash(value);

      for (Long hash : hashes) {
         insertStatement.addToBatch(1, hash, stmt.getLong("gamma_id"));
      }
      hashes.clear();
   }

   /**
    * don't tokenize and include only alpha-numeric characters in hash. If the value contains only non alpha-numeric
    * characters, then the hash is computed using all of its characters.
    */
   public void getWholeMatchHash(String value) {
      long hash = 0;
      for (char c : value.toCharArray()) {
         c = toLower(c);
         if (isLetterOrDigit(c)) {
            hash = 31 * hash + c;
         }
      }

      hashes.add(handleNoAlphaNumeric(value, hash));
   }

   private static long handleNoAlphaNumeric(String value, long hash) {
      if (hash == 0) {
         for (char c : value.toCharArray()) {
            hash = 31 * hash + toLower(c);
         }
      }
      return hash;
   }

   /**
    * tokenize on everything except alpha-numeric characters
    */
   private void addOriginalMatchHash(String value) {
      long hash = 0;
      for (char c : value.toCharArray()) {
         c = toLower(c);
         if (isLetterOrDigit(c)) {
            hash = 31 * hash + c;
         } else {
            addHash(hash);
            hash = 0;
         }
      }
      addHash(hash);
   }

   /**
    * tokenize on whitespace and include only alpha-numeric characters in hash
    */
   private void addPartialFuzzyPuncHash(String value) {
      long hash = 0;
      for (char c : value.toCharArray()) {
         if (c == ' ' || c == '\n' || c == '\r' || c == '\t') {
            addHash(hash);
            hash = 0;
         } else {
            c = toLower(c);
            if (isLetterOrDigit(c)) {
               hash = 31 * hash + c;
            }
         }
      }
      addHash(hash);
   }

   private void addHash(Long hash) {
      if (hash != 0) {
         hashes.add(hash);
      }
   }

   private static boolean isLetterOrDigit(char c) {
      return (c >= '0' && c <= '9') || (c >= 'a' && c <= 'z');
   }

   private static char toLower(char c) {
      if (c >= 'A' && c <= 'Z') {
         return (char) (c + 32);
      }
      return c;
   }

   public static void main(String[] args) {
      SearchTermHash app = new SearchTermHash();
      System.out.println(app.getTermHashes(Arrays.asList("{Artifact_Name}", "Artifact Name"), QueryOption.WHOLE_MATCH));
      System.out.println(
         app.getTermHashes(Arrays.asList("{Artifact_Name}", "Artifact Name"), QueryOption.TOKENIZE_WHITESPACE));
   }
}