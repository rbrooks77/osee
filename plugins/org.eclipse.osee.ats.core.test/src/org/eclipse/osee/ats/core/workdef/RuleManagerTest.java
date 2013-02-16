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
package org.eclipse.osee.ats.core.workdef;

import org.eclipse.osee.ats.api.workdef.RuleDefinitionOption;
import org.eclipse.osee.ats.core.config.RuleManager;
import org.junit.Test;

/**
 * Test case for {@link RuleManager}
 * 
 * @author Donald G. Dunne
 */
public class RuleManagerTest {

   @Test
   public void testGetOrCreateRule() {
      RuleManager mgr = new RuleManager();
      mgr.addRule(RuleDefinitionOption.AddDecisionValidateBlockingReview.name());
      mgr.addRule(RuleDefinitionOption.AddDecisionValidateBlockingReview.name());
      mgr.addRule("test");
   }

}
