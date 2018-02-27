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
package org.eclipse.osee.orcs.core.ds.criteria;

import java.util.Collection;
import java.util.Collections;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.orcs.core.ds.Criteria;
import org.eclipse.osee.orcs.core.ds.Options;

/**
 * @author Ryan D. Brooks
 */
public class CriteriaAttributeHash extends Criteria {
   private final AttributeTypeId attributeType;
   private final Collection<String> values;
   private final QueryOption option;
   private final boolean caseSensitive;

   public CriteriaAttributeHash(AttributeTypeId attributeType, Collection<String> values, QueryOption option, boolean caseSensitive) {
      this.attributeType = attributeType;
      this.values = values;
      this.option = option;
      this.caseSensitive = caseSensitive;
   }

   public CriteriaAttributeHash(AttributeTypeId attributeType, String value, QueryOption option, boolean caseSensitive) {
      this(attributeType, Collections.singletonList(value), option, caseSensitive);
   }

   public CriteriaAttributeHash(String value, QueryOption option, boolean caseSensitive) {
      this(AttributeTypeId.SENTINEL, value, option, caseSensitive);
   }

   public AttributeTypeId getAttributeType() {
      return attributeType;
   }

   public Collection<String> getValues() {
      return values;
   }

   public QueryOption getOption() {
      return option;
   }

   public boolean isCaseSensitive() {
      return caseSensitive;
   }

   @Override
   public void checkValid(Options options) {
      if (values.isEmpty()) {
         throw new OseeArgumentException("CriteriaAttributeHash requires at least one value to match");
      }
   }
}