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
package org.eclipse.osee.orcs.core.ds;

import java.util.ArrayList;
import org.eclipse.osee.framework.jdk.core.util.Conditions;

/**
 * @author Roberto E. Escobar
 */
public class DynamicObject extends DynamicData {

   private final ArrayList<DynamicData> children = new ArrayList<>();

   public DynamicObject(String uid, String alias) {
      super(uid, alias);
   }

   public boolean hasChildren() {
      return !children.isEmpty();
   }

   public Iterable<DynamicData> getChildren() {
      return children;
   }

   public void addChild(DynamicData child) {
      Conditions.checkExpressionFailOnTrue(this == child, "Cannot assign self as child - parent [%s] child [%s]", this,
         child);
      children.add(child);
      child.setParent(this);
   }

   public void addChild(int index, DynamicData child) {
      Conditions.checkExpressionFailOnTrue(this == child, "Cannot assign self as child - parent [%s] child [%s]", this,
         child);
      children.add(index, child);
      child.setParent(this);
   }

   public void removeChild(DynamicData child) {
      children.remove(child);
      child.setParent(null);
   }

}