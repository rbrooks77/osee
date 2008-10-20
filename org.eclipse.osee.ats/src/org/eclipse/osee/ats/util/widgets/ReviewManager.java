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
package org.eclipse.osee.ats.util.widgets;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.actions.NewDecisionReviewJob;
import org.eclipse.osee.ats.artifact.ATSAttributes;
import org.eclipse.osee.ats.artifact.DecisionReviewArtifact;
import org.eclipse.osee.ats.artifact.PeerToPeerReviewArtifact;
import org.eclipse.osee.ats.artifact.ReviewSMArtifact;
import org.eclipse.osee.ats.artifact.StateMachineArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.artifact.ATSLog.LogType;
import org.eclipse.osee.ats.artifact.ReviewSMArtifact.ReviewBlockType;
import org.eclipse.osee.ats.editor.SMAManager;
import org.eclipse.osee.ats.util.AtsRelation;
import org.eclipse.osee.ats.util.UsersByIds;
import org.eclipse.osee.ats.workflow.item.AtsWorkDefinitions;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.SkynetAuthentication;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;

/**
 * @author Donald G. Dunne
 */
public class ReviewManager {

   private final SMAManager smaMgr;
   private static String VALIDATE_REVIEW_TITLE = "Is the resolution of this Action valid?";

   public ReviewManager(SMAManager smaMgr) {
      super();
      this.smaMgr = smaMgr;
   }

   /**
    * Create a new decision review configured and transitioned to handle action validation
    * 
    * @param force will force the creation of the review without checking that a review should be created
    * @return new review
    * @throws
    */
   public DecisionReviewArtifact createValidateReview(boolean force) throws OseeCoreException {
      // If not validate page, don't do anything
      if (!force && !AtsWorkDefinitions.isValidatePage(smaMgr.getWorkPageDefinition())) {
         return null;
      }
      // If validate review already created for this state, return
      if (!force && getReviewsFromCurrentState().size() > 0) {
         for (ReviewSMArtifact rev : getReviewsFromCurrentState()) {
            if (rev.getDescriptiveName().equals(VALIDATE_REVIEW_TITLE)) return null;
         }
      }
      // Create validate review
      try {

         DecisionReviewArtifact decRev =
               NewDecisionReviewJob.createNewDecisionReview(
                     smaMgr.getSma(),
                     AtsWorkDefinitions.isValidateReviewBlocking(smaMgr.getWorkPageDefinition()) ? ReviewBlockType.Transition : ReviewBlockType.None,
                     true);
         decRev.setDescriptiveName(VALIDATE_REVIEW_TITLE);
         decRev.setSoleAttributeValue(ATSAttributes.DECISION_REVIEW_OPTIONS_ATTRIBUTE.getStoreName(),
               "No;Followup;" + getValidateReviewFollowupUsersStr() + "\n" + "Yes;Completed;");

         SMAManager revSmaMgr = new SMAManager(decRev);
         revSmaMgr.transition(DecisionReviewArtifact.DecisionReviewState.Decision.name(), smaMgr.getOriginator(), true);

         return decRev;

      } catch (Exception ex) {
         OSEELog.logException(AtsPlugin.class, ex, true);
      }
      return null;
   }

   public PeerToPeerReviewArtifact createNewPeerToPeerReview(String reviewTitle, String againstState) throws OseeCoreException {
      return createNewPeerToPeerReview(reviewTitle, againstState, SkynetAuthentication.getUser(), new Date());
   }

   public PeerToPeerReviewArtifact createNewPeerToPeerReview(String reviewTitle, String againstState, User origUser, Date origDate) throws OseeCoreException {
      return createNewPeerToPeerReview(smaMgr.getSma(), reviewTitle, againstState, origUser, origDate);
   }

   public static PeerToPeerReviewArtifact createNewPeerToPeerReview(StateMachineArtifact teamParent, String reviewTitle, String againstState, User origUser, Date origDate) throws OseeCoreException {
      PeerToPeerReviewArtifact peerToPeerRev =
            (PeerToPeerReviewArtifact) ArtifactTypeManager.addArtifact(PeerToPeerReviewArtifact.ARTIFACT_NAME,
                  BranchPersistenceManager.getAtsBranch(), reviewTitle == null ? "Peer to Peer Review" : reviewTitle);

      if (teamParent != null) {
         teamParent.addRelation(AtsRelation.TeamWorkflowToReview_Review, peerToPeerRev);
         if (againstState != null) peerToPeerRev.setSoleAttributeValue(
               ATSAttributes.RELATED_TO_STATE_ATTRIBUTE.getStoreName(), againstState);
      }

      peerToPeerRev.getSmaMgr().getLog().addLog(LogType.Originated, "", "", origDate, origUser);
      peerToPeerRev.setSoleAttributeValue(ATSAttributes.REVIEW_BLOCKS_ATTRIBUTE.getStoreName(),
            ReviewBlockType.None.name());

      // Initialize state machine
      peerToPeerRev.getSmaMgr().getStateMgr().initializeStateMachine(DecisionReviewArtifact.DecisionReviewState.Prepare.name());
      peerToPeerRev.getSmaMgr().getLog().addLog(LogType.StateEntered, DecisionReviewArtifact.DecisionReviewState.Prepare.name(),
            "", origDate, origUser);
      peerToPeerRev.persistAttributesAndRelations();
      return peerToPeerRev;
   }

   /**
    * Return Remain Hours for all reviews
    * 
    * @return remain hours
    * @throws Exception
    */
   public double getRemainHours() throws OseeCoreException {
      double hours = 0;
      for (ReviewSMArtifact reviewArt : getReviews())
         hours += reviewArt.getRemainHoursFromArtifact();
      return hours;

   }

   /**
    * Return Estimated Review Hours of "Related to State" stateName
    * 
    * @param relatedToStateName state name of parent workflow's state
    * @return Returns the Estimated Hours
    */
   public double getEstimatedHours(String relatedToStateName) throws OseeCoreException {
      double hours = 0;
      for (ReviewSMArtifact revArt : getReviews(relatedToStateName))
         hours += revArt.getEstimatedHoursTotal();
      return hours;
   }

   /**
    * Return Estimated Hours for all reviews
    * 
    * @return estimated hours
    * @throws Exception
    */
   public double getEstimatedHours() throws OseeCoreException {
      double hours = 0;
      for (ReviewSMArtifact revArt : getReviews())
         hours += revArt.getEstimatedHoursTotal();
      return hours;

   }

   public String getValidateReviewFollowupUsersStr() {
      try {
         return UsersByIds.getStorageString(getValidateReviewFollowupUsers());
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
         return ex.getLocalizedMessage();
      }
   }

   public Collection<User> getValidateReviewFollowupUsers() throws OseeCoreException {
      Collection<User> users = smaMgr.getStateMgr().getAssignees("Implement");
      if (users.size() > 0) return users;

      // Else if Team Workflow , return it to the leads of this team
      if (smaMgr.getSma() instanceof TeamWorkFlowArtifact) return ((TeamWorkFlowArtifact) smaMgr.getSma()).getTeamDefinition().getLeads();

      // Else, return current user; should never hit this
      return Arrays.asList(SkynetAuthentication.getUser());
   }

   public Collection<ReviewSMArtifact> getReviews() throws OseeCoreException {
      return smaMgr.getSma().getRelatedArtifacts(AtsRelation.TeamWorkflowToReview_Review, ReviewSMArtifact.class);
   }

   public Collection<ReviewSMArtifact> getReviewsFromCurrentState() throws OseeCoreException {
      return getReviews(smaMgr.getStateMgr().getCurrentStateName());
   }

   public Collection<ReviewSMArtifact> getReviews(String stateName) throws OseeCoreException {
      Set<ReviewSMArtifact> arts = new HashSet<ReviewSMArtifact>();
      if (!smaMgr.getSma().isTaskable()) return arts;
      for (ReviewSMArtifact revArt : getReviews()) {
         if (revArt.getSoleAttributeValue(ATSAttributes.RELATED_TO_STATE_ATTRIBUTE.getStoreName(), "").equals(stateName)) arts.add(revArt);
      }
      return arts;
   }

   public boolean hasReviews() {
      try {
         return smaMgr.getSma().getRelatedArtifactsCount(AtsRelation.TeamWorkflowToReview_Review) > 0;
      } catch (OseeCoreException ex) {
         OSEELog.logException(AtsPlugin.class, ex, true);
         return false;
      }
   }

   public Result areReviewsComplete() {
      return areReviewsComplete(true);
   }

   public Result areReviewsComplete(boolean popup) {
      try {
         for (ReviewSMArtifact reviewArt : getReviews()) {
            SMAManager smaMgr = new SMAManager(reviewArt);
            if (!smaMgr.isCompleted() && smaMgr.isCancelled()) return new Result("Not Complete");
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }
      return Result.TrueResult;
   }

   public static Collection<String> getAllReviewArtifactTypeNames() {
      return Arrays.asList(DecisionReviewArtifact.ARTIFACT_NAME, PeerToPeerReviewArtifact.ARTIFACT_NAME);
   }

   /**
    * Return Hours Spent for Reviews of "Related to State" stateName
    * 
    * @param relatedToStateName state name of parent workflow's state
    * @return Returns the Hours Spent
    */
   public double getHoursSpent(String relatedToStateName) throws OseeCoreException {
      double spent = 0;
      for (ReviewSMArtifact reviewArt : getReviews(relatedToStateName))
         spent += reviewArt.getHoursSpentSMATotal();
      return spent;
   }

   /**
    * Return Total Percent Complete / # Reviews for "Related to State" stateName
    * 
    * @param relatedToStateName state name of parent workflow's state
    * @return Returns the Percent Complete.
    */
   public int getPercentComplete(String relatedToStateName) throws OseeCoreException {
      int spent = 0;
      Collection<ReviewSMArtifact> reviewArts = getReviews(relatedToStateName);
      for (ReviewSMArtifact reviewArt : reviewArts)
         spent += reviewArt.getPercentCompleteSMATotal();
      if (spent == 0) return 0;
      return spent / reviewArts.size();
   }

}
