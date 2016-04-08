/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.config;

import org.codehaus.jackson.annotate.JsonProperty;
import org.eclipse.osee.ats.api.column.AtsValueColumn;
import org.eclipse.osee.ats.api.util.ColumnType;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Donald G. Dunne
 */
public class AtsAttributeValueColumn extends AtsValueColumn {
   private AttributeTypeToken attributeType;

   public AtsAttributeValueColumn() {
      // For JaxRs Instantiation
   }

   public AtsAttributeValueColumn(AttributeTypeToken attributeType, String id, String name, int width, String align, boolean show, ColumnType sortDataType, boolean multiColumnEditable, String description, Boolean actionRollup, Boolean inheritParent) {
      super(id, name, width, align, show, sortDataType, multiColumnEditable, description, actionRollup, inheritParent);
      this.attributeType = attributeType;
   }

   public AtsAttributeValueColumn(@JsonProperty("id") AttributeTypeToken attributeType) {
      // For JaxRs Instantiation
      this.attributeType = attributeType;
   }

   public AttributeTypeToken getAttributeType() {
      return attributeType;
   }

   public void setAttributeType(AttributeTypeToken attributeType) {
      this.attributeType = attributeType;
   }

   @Override
   public String toString() {
      return "AtsAttributeValueColumn [name=" + getName() + ", namespace=" + getNamespace() + ", attrType=" + attributeType + "]";
   }

   @Override
   public String getId() {
      String result = null;
      if (Strings.isValid(super.getId())) {
         result = super.getId();
      } else if (attributeType != null) {
         result = attributeType.getName();
      }
      return result;
   }
}