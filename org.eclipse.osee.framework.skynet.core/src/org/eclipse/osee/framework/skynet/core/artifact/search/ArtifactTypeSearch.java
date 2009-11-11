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
package org.eclipse.osee.framework.skynet.core.artifact.search;

import static org.eclipse.osee.framework.database.sql.SkynetDatabase.ARTIFACT_TABLE;
import static org.eclipse.osee.framework.database.sql.SkynetDatabase.ARTIFACT_TYPE_TABLE;
import static org.eclipse.osee.framework.skynet.core.artifact.search.DeprecatedOperator.EQUAL;
import java.util.List;
import org.eclipse.osee.framework.core.data.Branch;

/**
 * @author Robert A. Fisher
 */
public class ArtifactTypeSearch implements ISearchPrimitive {
   private String typeName;
   private DeprecatedOperator operation;
   private static final String tables = ARTIFACT_TABLE + "," + ARTIFACT_TYPE_TABLE;
   private final static String TOKEN = ";";

   /**
    * @param typeName
    */
   public ArtifactTypeSearch(String typeName, DeprecatedOperator operation) {
      super();
      this.typeName = typeName;
      this.operation = operation;
   }

   public String getArtIdColName() {
      return "art_id";
   }

   public String getCriteriaSql(List<Object> dataList, Branch branch) {
      String sql =
            ARTIFACT_TYPE_TABLE.column("name") + operation + " ? AND " + ARTIFACT_TYPE_TABLE.column("art_type_id") + EQUAL + ARTIFACT_TABLE.column("art_type_id");
      dataList.add(typeName);

      return sql;
   }

   public String getTableSql(List<Object> dataList, Branch branch) {
      return tables;
   }

   @Override
   public String toString() {
      return "Artifact type: " + typeName;
   }

   public String getStorageString() {
      return typeName + TOKEN + operation.name();
   }

   public static ArtifactTypeSearch getPrimitive(String storageString) {
      String[] values = storageString.split(TOKEN);
      if (values.length != 2) throw new IllegalStateException(
            "Value for " + ArtifactTypeSearch.class.getSimpleName() + " not parsable");

      return new ArtifactTypeSearch(values[0], DeprecatedOperator.valueOf(values[1]));
   }

}
