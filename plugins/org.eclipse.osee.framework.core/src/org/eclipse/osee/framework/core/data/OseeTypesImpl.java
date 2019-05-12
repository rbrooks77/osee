/*******************************************************************************
 * Copyright (c) 2019 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.data;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class OseeTypesImpl implements OseeTypes {
   private final Map<Long, AttributeTypeToken> attributeTypes = new ConcurrentHashMap<>();

   @Override
   public AttributeTypeToken getAttributeType(Long id) {
      return attributeTypes.get(id);
   }

   @Override
   public void registerAttributeTokens(Iterable<AttributeTypeToken> tokens) {
      for (AttributeTypeToken token : tokens) {
         attributeTypes.put(token.getId(), token);
      }
   }

   public void start() {

   }

   public void stop() {

   }
}