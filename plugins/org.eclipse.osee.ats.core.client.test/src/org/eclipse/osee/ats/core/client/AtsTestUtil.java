/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.client;

import static org.eclipse.osee.framework.core.enums.DeletionFlag.EXCLUDE_DELETED;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.core.client.action.ActionArtifact;
import org.eclipse.osee.ats.core.client.action.ActionManager;
import org.eclipse.osee.ats.core.client.actions.ISelectedAtsArtifacts;
import org.eclipse.osee.ats.core.client.branch.AtsBranchManagerCore;
import org.eclipse.osee.ats.core.client.review.AbstractReviewArtifact;
import org.eclipse.osee.ats.core.client.review.DecisionReviewArtifact;
import org.eclipse.osee.ats.core.client.review.DecisionReviewManager;
import org.eclipse.osee.ats.core.client.review.DecisionReviewState;
import org.eclipse.osee.ats.core.client.review.PeerToPeerReviewArtifact;
import org.eclipse.osee.ats.core.client.review.PeerToPeerReviewManager;
import org.eclipse.osee.ats.core.client.review.ReviewManager;
import org.eclipse.osee.ats.core.client.task.TaskArtifact;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.client.util.AtsUsersClient;
import org.eclipse.osee.ats.core.client.util.AtsUtilCore;
import org.eclipse.osee.ats.core.client.workdef.WorkDefinitionFactory;
import org.eclipse.osee.ats.core.client.workflow.ChangeType;
import org.eclipse.osee.ats.core.client.workflow.transition.TransitionHelper;
import org.eclipse.osee.ats.core.client.workflow.transition.TransitionManager;
import org.eclipse.osee.ats.core.client.workflow.transition.TransitionOption;
import org.eclipse.osee.ats.core.client.workflow.transition.TransitionResults;
import org.eclipse.osee.ats.core.config.ActionableItemFactory;
import org.eclipse.osee.ats.core.config.AtsConfigCache;
import org.eclipse.osee.ats.core.config.TeamDefinitionFactory;
import org.eclipse.osee.ats.core.config.VersionFactory;
import org.eclipse.osee.ats.core.model.IAtsActionableItem;
import org.eclipse.osee.ats.core.model.IAtsTeamDefinition;
import org.eclipse.osee.ats.core.model.IAtsUser;
import org.eclipse.osee.ats.core.model.IAtsVersion;
import org.eclipse.osee.ats.core.workdef.DecisionReviewOption;
import org.eclipse.osee.ats.core.workdef.ReviewBlockType;
import org.eclipse.osee.ats.core.workdef.StateDefinition;
import org.eclipse.osee.ats.core.workdef.WidgetDefinition;
import org.eclipse.osee.ats.core.workdef.WorkDefinition;
import org.eclipse.osee.ats.core.workflow.IWorkPage;
import org.eclipse.osee.ats.core.workflow.WorkPageAdapter;
import org.eclipse.osee.ats.core.workflow.WorkPageType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.HumanReadableId;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCache;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.PurgeArtifacts;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.osee.support.test.util.DemoSawBuilds;
import org.eclipse.osee.support.test.util.TestUtil;

/**
 * Test utility that will create a new work definition, team definition, versions and allow tests to easily
 * create/cleanup team workflows, tasks and reviews.
 * 
 * @author Donald G. Dunne
 */
public class AtsTestUtil {

   private static TeamWorkFlowArtifact teamArt = null, teamArt2 = null, teamArt3 = null, teamArt4 = null;
   private static IAtsTeamDefinition teamDef = null;
   private static IAtsVersion verArt1 = null, verArt2 = null, verArt3 = null, verArt4 = null;
   private static DecisionReviewArtifact decRevArt = null;
   private static PeerToPeerReviewArtifact peerRevArt = null;
   private static TaskArtifact taskArtWf1 = null, taskArtWf2 = null;
   private static IAtsActionableItem testAi = null, testAi2 = null, testAi3 = null, testAi4 = null;
   private static ActionArtifact actionArt = null, actionArt2 = null, actionArt3 = null, actionArt4 = null;
   private static StateDefinition analyze, implement, completed, cancelled = null;
   private static WorkDefinition workDef = null;
   public static String WORK_DEF_NAME = "Test_Team _Workflow_Definition";
   private static WidgetDefinition estHoursWidgetDef, workPackageWidgetDef;
   private static String postFixName;

   public static void validateArtifactCache() throws OseeStateException {
      final Collection<Artifact> dirtyArtifacts = ArtifactCache.getDirtyArtifacts();
      if (!dirtyArtifacts.isEmpty()) {
         for (Artifact artifact : dirtyArtifacts) {
            System.err.println(String.format("Artifact [%s] is dirty [%s]", artifact.toStringWithId(),
               Artifacts.getDirtyReport(artifact)));
         }
         throw new OseeStateException("[%d] Dirty Artifacts found after populate (see console for details)",
            dirtyArtifacts.size());
      }
   }

   public static void validateObjectsNull() throws OseeStateException {
      validateObjectsNull("teamArt", teamArt);
      validateObjectsNull("teamArt2", teamArt2);
      validateObjectsNull("teamArt3", teamArt3);
      validateObjectsNull("teamArt4", teamArt4);
      validateObjectsNull("teamDef", teamDef);
      validateObjectsNull("verArt1", verArt1);
      validateObjectsNull("verArt2", verArt2);
      validateObjectsNull("verArt3", verArt3);
      validateObjectsNull("verArt4", verArt4);
      validateObjectsNull("decRevArt", decRevArt);
      validateObjectsNull("peerRevArt", peerRevArt);
      validateObjectsNull("taskArt1", taskArtWf1);
      validateObjectsNull("taskArt2", taskArtWf2);
      validateObjectsNull("testAi", testAi);
      validateObjectsNull("testAi2", testAi2);
      validateObjectsNull("testAi3", testAi3);
      validateObjectsNull("testAi4", testAi4);
      validateObjectsNull("actionArt", actionArt);
      validateObjectsNull("actionArt2", actionArt2);
      validateObjectsNull("actionArt3", actionArt3);
      validateObjectsNull("actionArt4", actionArt4);
      validateObjectsNull("analyze", analyze);
      validateObjectsNull("implement", implement);
      validateObjectsNull("completed", completed);
      validateObjectsNull("cancelled", cancelled);
      validateObjectsNull("workDef", workDef);
      validateObjectsNull("estHoursWidgetDef", estHoursWidgetDef);
      validateObjectsNull("workPackageWidgetDef", workPackageWidgetDef);
   }

   private static void validateObjectsNull(String name, Object obj) throws OseeStateException {
      if (obj != null) {
         throw new OseeStateException("[%s] objects should be null but is not", name);
      }
   }

   public static WorkDefinition getWorkDef() throws OseeCoreException {
      ensureLoaded();
      return workDef;
   }

   public static StateDefinition getAnalyzeStateDef() throws OseeCoreException {
      ensureLoaded();
      return analyze;
   }

   public static WidgetDefinition getEstHoursWidgetDef() throws OseeCoreException {
      ensureLoaded();
      return estHoursWidgetDef;
   }

   public static WidgetDefinition getWorkPackageWidgetDef() throws OseeCoreException {
      ensureLoaded();
      return workPackageWidgetDef;
   }

   public static StateDefinition getImplementStateDef() throws OseeCoreException {
      ensureLoaded();
      return implement;
   }

   public static StateDefinition getCompletedStateDef() throws OseeCoreException {
      ensureLoaded();
      return completed;
   }

   public static StateDefinition getCancelledStateDef() throws OseeCoreException {
      ensureLoaded();
      return cancelled;
   }

   private static void ensureLoaded() throws OseeCoreException {
      if (workDef == null) {
         throw new OseeStateException("Must call cleanAndReset before using this method");
      }
   }

   private static void clearCaches() {
      if (workDef != null) {
         WorkDefinitionFactory.removeWorkDefinition(workDef);
      }
      analyze = null;
      implement = null;
      completed = null;
      cancelled = null;
      workDef = null;
      estHoursWidgetDef = null;
      workPackageWidgetDef = null;
      teamArt = null;
      teamArt2 = null;
      teamArt3 = null;
      teamArt4 = null;
      teamDef = null;
      taskArtWf1 = null;
      taskArtWf2 = null;
      testAi = null;
      testAi2 = null;
      testAi3 = null;
      testAi4 = null;
      actionArt = null;
      actionArt2 = null;
      actionArt3 = null;
      actionArt4 = null;
      verArt1 = null;
      verArt2 = null;
      verArt3 = null;
      verArt4 = null;
      decRevArt = null;
      peerRevArt = null;
      for (IAtsActionableItem aia : AtsConfigCache.get(IAtsActionableItem.class)) {
         if (aia.getName().contains("AtsTestUtil")) {
            AtsConfigCache.decache(aia);
         }
      }
      for (IAtsTeamDefinition aia : AtsConfigCache.get(IAtsTeamDefinition.class)) {
         if (aia.getName().contains("AtsTestUtil")) {
            AtsConfigCache.decache(aia);
         }
      }
      for (IAtsVersion ver : AtsConfigCache.get(IAtsVersion.class)) {
         if (ver.getName().contains("AtsTestUtil")) {
            AtsConfigCache.decache(ver);
         }
      }
   }

   private static String getTitle(String objectName, String postFixName) {
      return String.format("%s - %s [%s]", AtsTestUtil.class.getSimpleName(), objectName, postFixName);
   }

   /**
    * Clear workDef from cache, clear all objects and create new objects with postFixName in titles
    */
   private static void reset(String postFixName) throws OseeCoreException {
      AtsTestUtil.postFixName = postFixName;
      SkynetTransaction transaction =
         TransactionManager.createTransaction(AtsUtilCore.getAtsBranch(), AtsTestUtil.class.getSimpleName());
      workDef = new WorkDefinition(WORK_DEF_NAME);

      analyze = new StateDefinition("Analyze");
      analyze.setWorkDefinition(workDef);
      analyze.setWorkPageType(WorkPageType.Working);
      analyze.setOrdinal(1);
      workDef.getStates().add(analyze);

      workDef.setStartState(analyze);

      implement = new StateDefinition("Implement");
      implement.setWorkDefinition(workDef);
      implement.setWorkPageType(WorkPageType.Working);
      implement.setOrdinal(2);
      workDef.getStates().add(implement);

      completed = new StateDefinition("Completed");
      completed.setWorkDefinition(workDef);
      completed.setWorkPageType(WorkPageType.Completed);
      completed.setOrdinal(3);
      workDef.getStates().add(completed);

      cancelled = new StateDefinition("Cancelled");
      cancelled.setWorkDefinition(workDef);
      cancelled.setWorkPageType(WorkPageType.Cancelled);
      cancelled.setOrdinal(4);
      workDef.getStates().add(cancelled);

      analyze.setDefaultToState(implement);
      analyze.getToStates().addAll(Arrays.asList(implement, completed, cancelled));
      analyze.getOverrideAttributeValidationStates().addAll(Arrays.asList(cancelled));

      implement.setDefaultToState(completed);
      implement.getToStates().addAll(Arrays.asList(analyze, completed, cancelled));
      implement.getOverrideAttributeValidationStates().addAll(Arrays.asList(cancelled, analyze));

      completed.setDefaultToState(completed);
      completed.getToStates().addAll(Arrays.asList(implement));
      completed.getOverrideAttributeValidationStates().addAll(Arrays.asList(implement));

      cancelled.getToStates().addAll(Arrays.asList(analyze, implement));
      cancelled.getOverrideAttributeValidationStates().addAll(Arrays.asList(analyze, implement));

      estHoursWidgetDef = new WidgetDefinition(AtsAttributeTypes.EstimatedHours.getUnqualifiedName());
      estHoursWidgetDef.setAttributeName(AtsAttributeTypes.EstimatedHours.getName());
      estHoursWidgetDef.setXWidgetName("XFloatDam");

      workPackageWidgetDef = new WidgetDefinition(AtsAttributeTypes.WorkPackage.getUnqualifiedName());
      workPackageWidgetDef.setAttributeName(AtsAttributeTypes.WorkPackage.getName());
      workPackageWidgetDef.setXWidgetName("XTextDam");

      WorkDefinitionFactory.addWorkDefinition(workDef);

      testAi = ActionableItemFactory.createActionableItem(GUID.create(), getTitle("AI", postFixName));
      testAi.setActive(true);
      testAi.setActionable(true);

      testAi2 = ActionableItemFactory.createActionableItem(GUID.create(), getTitle("AI2", postFixName));
      testAi2.setActive(true);
      testAi2.setActionable(true);

      testAi3 = ActionableItemFactory.createActionableItem(GUID.create(), getTitle("AI3", postFixName));
      testAi3.setActive(true);
      testAi3.setActionable(true);

      testAi4 = ActionableItemFactory.createActionableItem(GUID.create(), getTitle("AI4", postFixName));
      testAi4.setActive(true);
      testAi4.setActionable(true);

      teamDef = TeamDefinitionFactory.createTeamDefinition(GUID.create(), getTitle("Team Def", postFixName));
      teamDef.setWorkflowDefinition(WORK_DEF_NAME);
      teamDef.setActive(true);
      teamDef.setTeamUsesVersions(true);
      teamDef.getLeads().add(AtsUsersClient.getUser());

      testAi.setTeamDefinition(teamDef);
      testAi2.setTeamDefinition(teamDef);
      testAi3.setTeamDefinition(teamDef);
      testAi4.setTeamDefinition(teamDef);

      verArt1 =
         VersionFactory.createVersion(getTitle("ver 1.0", postFixName), GUID.create(), HumanReadableId.generate());
      teamDef.getVersions().add(verArt1);

      verArt2 = VersionFactory.createVersion(getTitle("ver 2.0", postFixName));
      teamDef.getVersions().add(verArt2);

      verArt3 = VersionFactory.createVersion(getTitle("ver 3.0", postFixName));
      teamDef.getVersions().add(verArt3);

      verArt4 = VersionFactory.createVersion(getTitle("ver 4.0", postFixName));
      teamDef.getVersions().add(verArt4);

      actionArt =
         ActionManager.createAction(null, getTitle("Team WF", postFixName), "description", ChangeType.Improvement, "1",
            false, null, Arrays.asList(testAi), new Date(), AtsUsersClient.getUser(), null, transaction);

      teamArt = actionArt.getFirstTeam();

      teamArt.persist(transaction);
      actionArt.persist(transaction);
      transaction.execute();
   }

   public static TaskArtifact getOrCreateTaskOffTeamWf1() throws OseeCoreException {
      ensureLoaded();
      if (taskArtWf1 == null) {
         taskArtWf1 = teamArt.createNewTask(getTitle("Task", postFixName), new Date(), AtsUsersClient.getUser());
         taskArtWf1.setSoleAttributeValue(AtsAttributeTypes.RelatedToState, teamArt.getCurrentStateName());
         taskArtWf1.persist("AtsTestUtil - addTaskWf1");
      }
      return taskArtWf1;
   }

   public static TaskArtifact getOrCreateTaskOffTeamWf2() throws OseeCoreException {
      ensureLoaded();
      if (taskArtWf2 == null) {
         taskArtWf2 = teamArt.createNewTask(getTitle("Task", postFixName), new Date(), AtsUsersClient.getUser());
         taskArtWf2.setSoleAttributeValue(AtsAttributeTypes.RelatedToState, teamArt.getCurrentStateName());
         taskArtWf2.persist("AtsTestUtil - addTaskWf2");
      }
      return taskArtWf2;
   }

   public static DecisionReviewArtifact getOrCreateDecisionReview(ReviewBlockType reviewBlockType, AtsTestUtilState relatedToState) throws OseeCoreException {
      ensureLoaded();
      if (decRevArt == null) {
         List<DecisionReviewOption> options = new ArrayList<DecisionReviewOption>();
         options.add(new DecisionReviewOption(DecisionReviewState.Completed.getPageName(), false, null));
         options.add(new DecisionReviewOption(DecisionReviewState.Followup.getPageName(), true,
            Arrays.asList(AtsUsersClient.getUser().getUserId())));
         decRevArt =
            DecisionReviewManager.createNewDecisionReview(teamArt, reviewBlockType,
               AtsTestUtil.class.getSimpleName() + " Test Decision Review", relatedToState.getPageName(),
               "Decision Review", options, Arrays.asList(AtsUsersClient.getUser()), new Date(),
               AtsUsersClient.getUser());
      }
      return decRevArt;
   }

   public static TeamWorkFlowArtifact getTeamWf() throws OseeCoreException {
      ensureLoaded();
      return teamArt;
   }

   public static IAtsActionableItem getTestAi() throws OseeCoreException {
      ensureLoaded();
      return testAi;

   }

   public static IAtsTeamDefinition getTestTeamDef() throws OseeCoreException {
      ensureLoaded();
      return teamDef;
   }

   /**
    * All team defs, AIs, action and workflows will be deleted and new ones created with "name" as part of object
    * names/titles. In addition, ArtifactCache will validate that it is not dirty or display errors if it is.
    * 
    * @throws OseeCoreException
    */
   public static void cleanupAndReset(String name) throws OseeCoreException {
      cleanup();
      reset(name);
   }

   private static void delete(SkynetTransaction transaction, Artifact artifact) throws OseeCoreException {
      if (artifact != null) {
         artifact.deleteAndPersist(transaction);
      }
   }

   private static void deleteTeamWf(TeamWorkFlowArtifact teamWfToDelete) throws OseeCoreException {
      if (teamWfToDelete != null) {
         SkynetTransaction transaction =
            TransactionManager.createTransaction(AtsUtilCore.getAtsBranch(),
               AtsTestUtil.class.getSimpleName() + " - cleanup deleteTeamWf");

         if (teamWfToDelete.getWorkingBranch() != null) {
            Result result = AtsBranchManagerCore.deleteWorkingBranch(teamWfToDelete, true);
            if (result.isFalse()) {
               throw new OseeStateException("Error deleting working branch [%s]", result.getText());
            }
         }
         for (TaskArtifact taskArt : teamWfToDelete.getTaskArtifacts()) {
            taskArt.deleteAndPersist(transaction);
         }
         for (AbstractReviewArtifact revArt : ReviewManager.getReviews(teamWfToDelete)) {
            revArt.deleteAndPersist(transaction);
         }

         delete(transaction, teamWfToDelete);
         transaction.execute();
      }
   }

   /**
    * Cleanup all artifacts and confirm that ArtifactCache has no dirty artifacts. Should be called at beginning at end
    * of each test.
    */
   public static void cleanup() throws OseeCoreException {
      SkynetTransaction transaction1 =
         TransactionManager.createTransaction(AtsUtilCore.getAtsBranch(),
            AtsTestUtil.class.getSimpleName() + " - cleanup 1");
      delete(transaction1, peerRevArt);
      delete(transaction1, decRevArt);
      delete(transaction1, taskArtWf1);
      delete(transaction1, taskArtWf2);
      delete(transaction1, actionArt);
      delete(transaction1, actionArt2);
      delete(transaction1, actionArt3);
      delete(transaction1, actionArt4);
      transaction1.execute();

      deleteTeamWf(teamArt);
      deleteTeamWf(teamArt2);
      deleteTeamWf(teamArt3);
      deleteTeamWf(teamArt4);

      clearCaches();

      // validate that there are no dirty artifacts in cache
      AtsTestUtil.validateArtifactCache();
   }

   public static IAtsVersion getVerArt1() {
      return verArt1;
   }

   public static IAtsVersion getVerArt2() {
      return verArt2;
   }

   public static IAtsVersion getVerArt3() {
      return verArt3;
   }

   public static IAtsVersion getVerArt4() {
      return verArt4;
   }

   /**
    * Deletes any artifact with name that starts with title
    */
   public static void cleanupSimpleTest(String title) throws Exception {
      cleanupSimpleTest(Arrays.asList(title));
   }

   /**
    * Deletes all artifacts with names that start with any title given
    */
   public static void cleanupSimpleTest(Collection<String> titles) throws Exception {
      List<Artifact> artifacts = new ArrayList<Artifact>();
      for (String title : titles) {
         artifacts.addAll(ArtifactQuery.getArtifactListFromName(title + "%", AtsUtilCore.getAtsBranch(),
            EXCLUDE_DELETED));
      }
      Operations.executeWorkAndCheckStatus(new PurgeArtifacts(artifacts));
      TestUtil.sleep(4000);
   }

   public static Result transitionTo(AtsTestUtilState atsTestUtilState, IAtsUser user, SkynetTransaction transaction, TransitionOption... transitionOptions) {
      if (atsTestUtilState == AtsTestUtilState.Analyze && teamArt.isInState(AtsTestUtilState.Analyze)) {
         return Result.TrueResult;
      }

      if (atsTestUtilState == AtsTestUtilState.Cancelled) {
         Result result = transitionToState(teamArt, AtsTestUtilState.Cancelled, user, transaction, transitionOptions);
         if (result.isFalse()) {
            return result;
         }
         return Result.TrueResult;
      }

      Result result = transitionToState(teamArt, AtsTestUtilState.Implement, user, transaction, transitionOptions);
      if (result.isFalse()) {
         return result;
      }

      if (atsTestUtilState == AtsTestUtilState.Implement) {
         return Result.TrueResult;
      }

      if (atsTestUtilState == AtsTestUtilState.Completed) {
         result = transitionToState(teamArt, AtsTestUtilState.Completed, user, transaction, transitionOptions);
         if (result.isFalse()) {
            return result;
         }

      }
      return Result.TrueResult;

   }

   private static Result transitionToState(TeamWorkFlowArtifact teamArt, IWorkPage toState, IAtsUser user, SkynetTransaction transaction, TransitionOption... transitionOptions) {
      TransitionHelper helper =
         new TransitionHelper("Transition to " + toState.getPageName(), Arrays.asList(teamArt), toState.getPageName(),
            Arrays.asList(user), null, transitionOptions);
      TransitionManager transitionMgr = new TransitionManager(helper, transaction);
      TransitionResults results = transitionMgr.handleAll();
      if (results.isEmpty()) {
         return Result.TrueResult;
      }
      return new Result("Transition Error %s", results.toString());
   }

   public static class AtsTestUtilState extends WorkPageAdapter {
      public static AtsTestUtilState Analyze = new AtsTestUtilState("Analyze", WorkPageType.Working);
      public static AtsTestUtilState Implement = new AtsTestUtilState("Implement", WorkPageType.Working);
      public static AtsTestUtilState Completed = new AtsTestUtilState("Completed", WorkPageType.Completed);
      public static AtsTestUtilState Cancelled = new AtsTestUtilState("Cancelled", WorkPageType.Cancelled);

      private AtsTestUtilState(String pageName, WorkPageType workPageType) {
         super(AtsTestUtilState.class, pageName, workPageType);
      }

      public static AtsTestUtilState valueOf(String pageName) {
         return WorkPageAdapter.valueOfPage(AtsTestUtilState.class, pageName);
      }

      public static List<AtsTestUtilState> values() {
         return WorkPageAdapter.pages(AtsTestUtilState.class);
      }
   }

   public static PeerToPeerReviewArtifact getOrCreatePeerReview(ReviewBlockType reviewBlockType, AtsTestUtilState relatedToState, SkynetTransaction transaction) throws OseeCoreException {
      ensureLoaded();
      if (peerRevArt == null) {
         peerRevArt =
            PeerToPeerReviewManager.createNewPeerToPeerReview(teamArt,
               AtsTestUtil.class.getSimpleName() + " Test Peer Review", relatedToState.getPageName(), transaction);
      }
      return peerRevArt;
   }

   public static TeamWorkFlowArtifact getTeamWf2() throws OseeCoreException {
      ensureLoaded();
      if (teamArt2 == null) {
         SkynetTransaction transaction =
            TransactionManager.createTransaction(AtsUtilCore.getAtsBranch(), AtsTestUtil.class.getSimpleName());
         actionArt2 =
            ActionManager.createAction(null, getTitle("Team WF2", postFixName), "description", ChangeType.Improvement,
               "1", false, null, Arrays.asList(testAi2), new Date(), AtsUsersClient.getUser(), null, transaction);

         teamArt2 = actionArt2.getFirstTeam();
         transaction.execute();
      }
      return teamArt2;
   }

   public static IAtsActionableItem getTestAi2() throws OseeCoreException {
      ensureLoaded();
      return testAi2;
   }

   public static TeamWorkFlowArtifact getTeamWf3() throws OseeCoreException {
      ensureLoaded();
      if (teamArt3 == null) {
         SkynetTransaction transaction =
            TransactionManager.createTransaction(AtsUtilCore.getAtsBranch(), AtsTestUtil.class.getSimpleName());
         actionArt3 =
            ActionManager.createAction(null, getTitle("Team WF3", postFixName), "description", ChangeType.Improvement,
               "1", false, null, Arrays.asList(testAi3), new Date(), AtsUsersClient.getUser(), null, transaction);

         teamArt3 = actionArt3.getFirstTeam();
         transaction.execute();
      }
      return teamArt3;
   }

   public static IAtsActionableItem getTestAi3() throws OseeCoreException {
      ensureLoaded();
      return testAi3;
   }

   public static TeamWorkFlowArtifact getTeamWf4() throws OseeCoreException {
      ensureLoaded();
      if (teamArt4 == null) {
         SkynetTransaction transaction =
            TransactionManager.createTransaction(AtsUtilCore.getAtsBranch(), AtsTestUtil.class.getSimpleName());
         actionArt4 =
            ActionManager.createAction(null, getTitle("Team WF4", postFixName), "description", ChangeType.Improvement,
               "1", false, null, Arrays.asList(testAi4), new Date(), AtsUsersClient.getUser(), null, transaction);

         teamArt4 = actionArt4.getFirstTeam();
         transaction.execute();
      }
      return teamArt4;
   }

   public static IAtsActionableItem getTestAi4() throws OseeCoreException {
      ensureLoaded();
      return testAi4;
   }

   /**
    * @return 2nd Action with single Team Workflow not tied to other ActionArt or TeamWf
    */
   public static ActionArtifact getActionArt2() throws OseeCoreException {
      ensureLoaded();
      if (actionArt2 == null) {
         getTeamWf2();
      }
      return actionArt2;
   }

   /**
    * @return 3rd Action with single Team Workflow not tied to other ActionArt or TeamWf
    */
   public static ActionArtifact getActionArt3() throws OseeCoreException {
      ensureLoaded();
      if (actionArt3 == null) {
         getTeamWf3();
      }
      return actionArt3;
   }

   public static ActionArtifact getActionArt() throws OseeCoreException {
      ensureLoaded();
      return actionArt;
   }

   public static ISelectedAtsArtifacts getSelectedAtsArtifactsForTeamWf() {
      return new ISelectedAtsArtifacts() {

         @Override
         public Set<? extends Artifact> getSelectedSMAArtifacts() throws OseeCoreException {
            return Collections.singleton(getTeamWf());
         }

         @Override
         public List<Artifact> getSelectedAtsArtifacts() throws OseeCoreException {
            return Arrays.asList((Artifact) getTeamWf());
         }

         @Override
         public List<TaskArtifact> getSelectedTaskArtifacts() {
            return Collections.emptyList();
         }

      };
   }

   public static Result createWorkingBranchFromTeamWf() throws OseeCoreException {
      configureVer1ForWorkingBranch();
      Result result = AtsBranchManagerCore.createWorkingBranch_Validate(teamArt);
      if (result.isFalse()) {
         return result;
      }
      AtsBranchManagerCore.createWorkingBranch_Create(teamArt, true);
      teamArt.getWorkingBranchForceCacheUpdate();
      return Result.TrueResult;
   }

   public static void configureVer1ForWorkingBranch() throws OseeCoreException {
      IAtsVersion verArt = getVerArt1();
      verArt.setAllowCreateBranch(true);
      verArt.setAllowCommitBranch(true);
      verArt.setBaselineBranchGuid(BranchManager.getBranch(DemoSawBuilds.SAW_Bld_1).getGuid());
      if (getTeamWf().getTargetedVersion() == null) {
         getTeamWf().setTargetedVersion(getVerArt1());
         getTeamWf().setTargetedVersionLink(getVerArt1());
         getTeamWf().persist(AtsTestUtil.class.getSimpleName() + "-SetTeamWfTargetedVer1");
      }
   }

   public static String getName() {
      return postFixName;
   }

}
