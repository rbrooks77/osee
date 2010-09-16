/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.coverage.store;

import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.NamedIdentity;

/**
 * @author Ryan D. Brooks
 */
public class CoverageAttributeTypes extends NamedIdentity implements IAttributeType {
   // @formatter:off
   public static final IAttributeType Assignees = new CoverageAttributeTypes("AARCA+XjSyKjnh3sweQA", "coverage.Assignees");
   public static final IAttributeType Item = new CoverageAttributeTypes("AARr8BmsQHKLOHNzOcQA", "coverage.Coverage Item");
   public static final IAttributeType Options = new CoverageAttributeTypes("AAF+8+sqyELZ2mdVV_AA", "coverage.Coverage Options");
   public static final IAttributeType FileContents = new CoverageAttributeTypes("AARDJK8YAT3SDnghjQgA", "coverage.File Contents");
   public static final IAttributeType Location = new CoverageAttributeTypes("AARA2XwhNRddgQrd0iwA", "coverage.Location");
   public static final IAttributeType Namespace = new CoverageAttributeTypes("AAQ_v6uUrh0j39+4D5gA", "coverage.Namespace");
   public static final IAttributeType WorkProductGuid = new CoverageAttributeTypes("A+m7Y2sV2z83QUlkzIAA", "coverage.WorkProductGuid");
   public static final IAttributeType Notes = new CoverageAttributeTypes("AARERmIjazD1udUwfLgA", "coverage.Notes");
   public static final IAttributeType Order = new CoverageAttributeTypes("AD72opMBR1pFxB0hVpQA", "coverage.Order");
   // @formatter:on

   private CoverageAttributeTypes(String guid, String name) {
      super(guid, name);
   }
}