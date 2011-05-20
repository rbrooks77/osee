/*
 * Created on Nov 10, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.column;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import org.eclipse.osee.ats.core.AtsTestUtil;
import org.eclipse.osee.ats.core.team.TeamState;
import org.eclipse.osee.ats.core.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.workflow.transition.TransitionHelper;
import org.eclipse.osee.ats.core.workflow.transition.TransitionManager;
import org.eclipse.osee.ats.core.workflow.transition.TransitionOption;
import org.eclipse.osee.ats.core.workflow.transition.TransitionResults;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.ats.util.DemoTestUtil;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.support.test.util.TestUtil;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;

/**
 * @tests CancelledDateColumn
 * @author Donald G. Dunne
 */
public class CancelledDateColumnTest {

   @AfterClass
   @BeforeClass
   public static void cleanup() throws Exception {
      AtsTestUtil.cleanupSimpleTest(CancelledDateColumnTest.class.getSimpleName());
   }

   @org.junit.Test
   public void testGetDateAndStrAndColumnText() throws Exception {
      SevereLoggingMonitor loggingMonitor = TestUtil.severeLoggingStart();

      SkynetTransaction transaction =
         new SkynetTransaction(AtsUtil.getAtsBranch(), CancelledDateColumnTest.class.getSimpleName());
      TeamWorkFlowArtifact teamArt =
         DemoTestUtil.createSimpleAction(CancelledDateColumnTest.class.getSimpleName(), transaction);
      transaction.execute();

      Assert.assertEquals("", CancelledDateColumn.getInstance().getColumnText(teamArt, AssigneeColumn.getInstance(), 0));
      Date date = CancelledDateColumn.getDate(teamArt);
      Assert.assertNull(date);
      Assert.assertEquals("", CancelledDateColumn.getDateStr(teamArt));

      TransitionHelper helper =
         new TransitionHelper("Transition to Cancelled", Arrays.asList(teamArt), TeamState.Cancelled.getPageName(),
            null, "reason", TransitionOption.OverrideTransitionValidityCheck, TransitionOption.OverrideAssigneeCheck);
      TransitionManager transitionMgr = new TransitionManager(helper);
      TransitionResults results = transitionMgr.handleAll();
      Assert.assertTrue(results.toString(), results.isEmpty());
      transitionMgr.getTransaction().execute();

      date = CancelledDateColumn.getDate(teamArt);
      Assert.assertNotNull(date);
      Assert.assertEquals(DateUtil.getMMDDYYHHMM(date), CancelledDateColumn.getDateStr(teamArt));
      Assert.assertEquals(DateUtil.getMMDDYYHHMM(date),
         CancelledDateColumn.getInstance().getColumnText(teamArt, AssigneeColumn.getInstance(), 0));

      helper =
         new TransitionHelper("Transition to Endorse", Arrays.asList(teamArt), TeamState.Endorse.getPageName(),
            Collections.singleton(UserManager.getUser()), null, TransitionOption.OverrideTransitionValidityCheck,
            TransitionOption.OverrideAssigneeCheck);
      transitionMgr = new TransitionManager(helper);
      results = transitionMgr.handleAll();
      Assert.assertTrue(results.toString(), results.isEmpty());
      transitionMgr.getTransaction().execute();

      Assert.assertEquals("Cancelled date should be blank again", "",
         CancelledDateColumn.getInstance().getColumnText(teamArt, AssigneeColumn.getInstance(), 0));
      date = CancelledDateColumn.getDate(teamArt);
      Assert.assertNull(date);

      TestUtil.severeLoggingEnd(loggingMonitor);
   }
}
