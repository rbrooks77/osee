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
package org.eclipse.osee.client.integration.tests.suite;

import org.eclipse.osee.ats.config.demo.PopulateActionsTest;
import org.eclipse.osee.ats.config.demo.config.DemoDatabaseConfigTest;
import org.eclipse.osee.ats.config.demo.config.DemoDbGroupsTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
   DemoDbInitTest.class,
   PopulateDemoDatabaseTest.class,
   DemoDatabaseConfigTest.class,
   DemoDbGroupsTest.class,
   PopulateActionsTest.class})
/**
 * DbInit, DbPopulate and Tests to ensure that all went as expected.
 * @author Donald G. Dunne
 */
public class MasterTestSuite_DemoDbInit {
   // tests listed above

}
