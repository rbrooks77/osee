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
package org.eclipse.osee.ats;

import org.eclipse.osee.ats.editor.stateItem.AtsDecisionReviewDecisionStateItemTest;
import org.eclipse.osee.ats.editor.stateItem.AtsDecisionReviewPrepareStateItemTest;
import org.eclipse.osee.ats.editor.stateItem.AtsForceAssigneesToTeamLeadsStateItemTest;
import org.eclipse.osee.ats.editor.stateItem.AtsPeerToPeerReviewPrepareStateItemTest;
import org.eclipse.osee.ats.editor.stateItem.AtsPeerToPeerReviewReviewStateItemTest;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
   AtsForceAssigneesToTeamLeadsStateItemTest.class,
   AtsPeerToPeerReviewReviewStateItemTest.class,
   AtsPeerToPeerReviewPrepareStateItemTest.class,
   AtsDecisionReviewDecisionStateItemTest.class,
   AtsDecisionReviewPrepareStateItemTest.class})
/**
 * This test suite contains tests that must be run against demo database
 * 
 * @author Donald G. Dunne
 */
public class AtsTest_Demo_StateItem_Suite {
   @BeforeClass
   public static void setUp() throws Exception {
      OseeProperties.setIsInTest(true);
      System.out.println("\n\nBegin " + AtsTest_Demo_StateItem_Suite.class.getSimpleName());
   }

   @AfterClass
   public static void tearDown() throws Exception {
      System.out.println("End " + AtsTest_Demo_StateItem_Suite.class.getSimpleName());
   }
}
