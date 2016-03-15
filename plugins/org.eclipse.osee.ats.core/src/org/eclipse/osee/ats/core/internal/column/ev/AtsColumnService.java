/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.internal.column.ev;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsServices;
import org.eclipse.osee.ats.api.config.AtsAttributeValueColumn;
import org.eclipse.osee.ats.core.column.AssigneeColumn;
import org.eclipse.osee.ats.core.column.AtsAttributeValueColumnHandler;
import org.eclipse.osee.ats.core.column.AtsColumnId;
import org.eclipse.osee.ats.core.column.AtsIdColumn;
import org.eclipse.osee.ats.core.column.IAtsColumn;
import org.eclipse.osee.ats.core.column.IAtsColumnService;
import org.eclipse.osee.ats.core.internal.column.TeamColumn;

/**
 * @author Donald G. Dunne
 */
public class AtsColumnService implements IAtsColumnService {

   public static final String CELL_ERROR_PREFIX = "!Error";
   private Map<String, IAtsColumn> columnIdToAtsColumn;
   private final IAtsServices services;

   public AtsColumnService(IAtsServices services) {
      this.services = services;
   }

   @Override
   public String getColumnText(AtsColumnId column, IAtsObject atsObject) {
      return getColumnText(column.getId(), atsObject);
   }

   @Override
   public String getColumnText(String id, IAtsObject atsObject) {
      String result = "";
      IAtsColumn column = getColumn(id);
      if (column == null) {
         result = "column not supported";
      } else {
         result = column.getColumnText(atsObject);
      }
      return result;
   }

   @Override
   public IAtsColumn getColumn(String id) {
      if (columnIdToAtsColumn == null) {
         columnIdToAtsColumn = new HashMap<String, IAtsColumn>();
      }
      IAtsColumn column = columnIdToAtsColumn.get(id);
      if (column == null) {
         for (AtsAttributeValueColumn attrCol : services.getConfigurations().getViews().getAttrColumns()) {
            if (id.equals(attrCol.getId())) {
               column = new AtsAttributeValueColumnHandler(attrCol, services);
               add(id, column);
               break;
            }
         }
      }
      if (column == null) {
         if (id.equals(AtsColumnId.Team.getId())) {
            column = new TeamColumn(services.getReviewService());
         } else if (id.equals(AtsColumnId.Assignees.getId())) {
            column = AssigneeColumn.instance;
         } else if (id.equals(AtsColumnId.AtsId.getId())) {
            column = AtsIdColumn.instance;
         } else if (id.equals(AtsColumnId.ActivityId.getId())) {
            column = new ActivityIdColumn(services.getEarnedValueServiceProvider());
         } else if (id.equals(AtsColumnId.WorkPackageName.getId())) {
            column = new WorkPackageNameColumn(services.getEarnedValueServiceProvider());
         } else if (id.equals(AtsColumnId.WorkPackageId.getId())) {
            column = new WorkPackageIdColumn(services.getEarnedValueServiceProvider());
         } else if (id.equals(AtsColumnId.WorkPackageType.getId())) {
            column = new WorkPackageTypeColumn(services.getEarnedValueServiceProvider());
         } else if (id.equals(AtsColumnId.WorkPackageProgram.getId())) {
            column = new WorkPackageProgramColumn(services.getEarnedValueServiceProvider());
         } else if (id.equals(AtsColumnId.WorkPackageGuid.getId())) {
            column = new WorkPackageGuidColumn(services.getEarnedValueServiceProvider());
         }
         // Add to cache even if not found so don't need to look again
         add(id, column);
      }
      return column;
   }

   @Override
   public void add(String id, IAtsColumn column) {
      columnIdToAtsColumn.put(id, column);
   }

   @Override
   public IAtsColumn getColumn(AtsColumnId columnId) {
      return getColumn(columnId.getId());
   }

}