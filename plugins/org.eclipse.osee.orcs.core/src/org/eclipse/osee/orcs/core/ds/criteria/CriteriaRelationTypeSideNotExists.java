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
package org.eclipse.osee.orcs.core.ds.criteria;

import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.orcs.core.ds.Criteria;
import org.eclipse.osee.orcs.core.ds.Options;

/**
 * @author John Misinco
 */
public class CriteriaRelationTypeSideNotExists extends Criteria {
   private final RelationTypeSide relationTypeSide;

   public CriteriaRelationTypeSideNotExists(RelationTypeSide relationTypeSide) {
      this.relationTypeSide = relationTypeSide;
   }

   public RelationTypeSide getType() {
      return relationTypeSide;
   }

   @Override
   public boolean checkValid(Options options) {
      Conditions.checkNotNull(getType(), "relation type");
      return true;
   }

   @Override
   public String toString() {
      return "CriteriaRelationTypeNotExists [relationTypeSide=" + relationTypeSide + "]";
   }
}