/*
 * Created on Jan 24, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.editor.stateItem;

import static org.junit.Assert.assertFalse;
import java.util.Arrays;
import org.eclipse.osee.ats.core.AtsTestUtil;
import org.eclipse.osee.ats.core.review.DecisionReviewArtifact;
import org.eclipse.osee.ats.core.review.DecisionReviewManager;
import org.eclipse.osee.ats.core.review.DecisionReviewState;
import org.eclipse.osee.ats.core.type.AtsArtifactTypes;
import org.eclipse.osee.ats.core.type.AtsAttributeTypes;
import org.eclipse.osee.ats.core.workflow.transition.TransitionResults;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.IBasicUser;
import org.eclipse.osee.framework.core.util.IWorkPage;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test Case for {@link AtsDecisionReviewPrepareStateItem}
 * 
 * @author Donald G. Dunne
 */
public class AtsDecisionReviewPrepareStateItemTest {

   public static DecisionReviewArtifact decRevArt;

   @Before
   public void setUp() throws Exception {
      // This test should only be run on test db
      assertFalse("Test should not be run in production db", AtsUtil.isProductionDb());

      if (decRevArt == null) {
         // setup fake review artifact with decision options set
         decRevArt =
            (DecisionReviewArtifact) ArtifactTypeManager.addArtifact(AtsArtifactTypes.DecisionReview,
               AtsUtil.getAtsBranch());
         decRevArt.setName(getClass().getSimpleName());
         decRevArt.persist();
      }
   }

   @BeforeClass
   @AfterClass
   public static void testCleanup() throws Exception {
      AtsTestUtil.cleanupSimpleTest(AtsDecisionReviewPrepareStateItemTest.class.getSimpleName());
   }

   @Test
   public void testTransitioning() throws OseeCoreException {
      Assert.assertNotNull(decRevArt);

      // set valid options
      String decisionOptionStr =
         DecisionReviewManager.getDecisionReviewOptionsString(DecisionReviewManager.getDefaultDecisionReviewOptions());
      decRevArt.setSoleAttributeValue(AtsAttributeTypes.DecisionReviewOptions, decisionOptionStr);
      decRevArt.persist();

      IWorkPage fromState = decRevArt.getWorkDefinition().getStateByName(DecisionReviewState.Prepare.getPageName());
      IWorkPage toState = decRevArt.getWorkDefinition().getStateByName(DecisionReviewState.Decision.getPageName());

      // make call to state item that should set options based on artifact's attribute value
      AtsDecisionReviewPrepareStateItem stateItem = new AtsDecisionReviewPrepareStateItem();
      TransitionResults results = new TransitionResults();
      stateItem.transitioning(results, decRevArt, fromState, toState, Arrays.asList((IBasicUser) UserManager.getUser()));

      // verify no errors
      Assert.assertTrue(results.toString(), results.isEmpty());

      // set invalid options; NoState is invalid, should only be Completed or FollowUp
      decisionOptionStr = decisionOptionStr.replaceFirst("Completed", "NoState");
      decRevArt.setSoleAttributeValue(AtsAttributeTypes.DecisionReviewOptions, decisionOptionStr);
      decRevArt.persist();
      stateItem.transitioning(results, decRevArt, fromState, toState, Arrays.asList((IBasicUser) UserManager.getUser()));
      Assert.assertTrue(results.contains("Invalid Decision Option"));

   }
}
