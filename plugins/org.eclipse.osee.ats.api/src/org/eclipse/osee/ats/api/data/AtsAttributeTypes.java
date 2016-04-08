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
package org.eclipse.osee.ats.api.data;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;

/**
 * @author Ryan D. Brooks
 * @author Donald G. Dunne
 */
public final class AtsAttributeTypes {

   public static final Map<String, AttributeTypeToken<?>> nameToTypeMap = new HashMap<>();

   // @formatter:off
   public static final AttributeTypeToken<String> ActionDetailsFormat = createType(1152921504606847199L, "Action Details Format", "Format of string when push Action Details Copy button on SMA Workflow Editor.");
   public static final AttributeTypeToken<String> Actionable = createType(1152921504606847160L, "Actionable", "True if item can have Action written against or assigned to.");
   public static final AttributeTypeToken<ArtifactId> ActionableItemReference = createType(6780739363553225476L, "Actionable Item Reference", "Actionable Items that are impacted by this change.");
   public static final AttributeTypeToken<Boolean> Active = createType(1152921504606847153L, "Active", "Active ATS configuration object.");
   public static final AttributeTypeToken<String> ActivityId = createType(1152921504606847874L, "Activity ID");
   public static final AttributeTypeToken<String> ActivityName = createType(1152921504606847875L, "Activity Name");
   public static final AttributeTypeToken<Boolean> AllowCommitBranch = createType(1152921504606847162L, "Allow Commit Branch");
   public static final AttributeTypeToken<Boolean> AllowCreateBranch = createType(1152921504606847161L, "Allow Create Branch");
   public static final AttributeTypeToken<Boolean> AllowUserActionCreation = createType(1322118789779953012L, "Allow User Action Creation");
   public static final AttributeTypeToken<Boolean> AllowWebExport = createType(1244831604424847172L, "Allow Web Export");
   public static final AttributeTypeToken<Boolean> ApplicabilityWorkflow = createType(1152922022510067882L, "Applicability Workflow");
   public static final AttributeTypeToken<String> ApplicableToProgram = createType(1152921949227188394L, "Applicable To Program");
   public static final AttributeTypeToken<String> AtsConfiguredBranch = createType(72063456936722683L, "ATS Configured Branch", "ATS Configured Branch");
   public static final AttributeTypeToken<String> AtsId = createType(1152921504606847877L, "ATS Id", "ATS Generated Id");
   public static final AttributeTypeToken<String> AtsIdPrefix = createType(1162773128791720837L, "ATS Id Prefix", "ATS Id Prefix");
   public static final AttributeTypeToken<String> AtsIdSequenceName = createType(1163054603768431493L, "ATS Id Sequence Name", "ATS Id Sequence Name");
   public static final AttributeTypeToken<String> BaselineBranchId = createType(1152932018686787753L, "Baseline Branch Id", "Baseline branch associated with ATS object.");
   public static final AttributeTypeToken<String> BlockedReason = createType(7797797474874870503L, "Blocked Reason", "Reason for action being blocked");
   public static final AttributeTypeToken<String> CSCI = createType(72063457007112443L, "CSCI", "CSCI this Team is reponsible for.");
   public static final AttributeTypeToken<String> CancelledBy = createType(1152921504606847170L, "Cancelled By", "UserId of the user who cancelled workflow.");
   public static final AttributeTypeToken<Date> CancelledDate = createType(1152921504606847169L, "Cancelled Date", "Date the workflow was cancelled.");
   public static final AttributeTypeToken<String> CancelledFromState = createType(1152921504606847172L, "Cancelled From State", "State workflow was in when cancelled.");
   public static final AttributeTypeToken<String> CancelledReason = createType(1152921504606847171L, "Cancelled Reason", "Explanation of why worklfow was cancelled.");
   public static final AttributeTypeToken<String> Category1 = createType(1152921504606847212L, "Category", "Open field for user to be able to enter text to use for categorizing/sorting.");
   public static final AttributeTypeToken<String> Category2 = createType(1152921504606847217L, "Category2", Category1.getDescription());
   public static final AttributeTypeToken<String> Category3 = createType(1152921504606847218L, "Category3", Category1.getDescription());
   public static final AttributeTypeToken<String> ChangeType = createType(1152921504606847180L, "Change Type", "Type of change.");
   public static final AttributeTypeToken<String> ClosureActive = createType(1152921875139002555L, "Closure Active status of Program");
   public static final AttributeTypeToken<String> ClosureState = createType(1152921504606847452L, "Closure Status of Build");
   public static final AttributeTypeToken<String> ColorTeam = createType(1364016837443371647L, "Color Team");
   public static final AttributeTypeToken<String> CommitOverride = createType(104739333325561L, "Commit Override", "Commit was overridden by user.");
   public static final AttributeTypeToken<String> CompletedBy = createType(1152921504606847167L, "Completed By", "UserId of the user who completed workflow.");
   public static final AttributeTypeToken<Date> CompletedDate = createType(1152921504606847166L, "Completed Date", "Date the workflow was completed.");
   public static final AttributeTypeToken<String> CompletedFromState = createType(1152921504606847168L, "Completed From State", "State workflow was in when completed.");
   public static final AttributeTypeToken<String> CreatedBy = createType(1152921504606847174L, "Created By", "UserId of the user who created the workflow.");
   public static final AttributeTypeToken<String> CreatedByReference = createType(32875234523958L, "Created By Reference", "Id of the user artifact who created the workflow.");
   public static final AttributeTypeToken<Date> CreatedDate = createType(1152921504606847173L, "Created Date", "Date the workflow was created.");
   public static final AttributeTypeToken<String> CurrentState = createType(1152921504606847192L, "Current State", "Current state of workflow state machine.");
   public static final AttributeTypeToken<String> CurrentStateType = createType(1152921504606847147L, "Current State Type", "Type of Current State: InWork, Completed or Cancelled.");
   public static final AttributeTypeToken<String> Decision = createType(1152921504606847221L, "Decision", "Option selected during decision review.");
   public static final AttributeTypeToken<String> DecisionReviewOptions = createType(1152921504606847220L, "Decision Review Options", "Options available for selection in review.  Each line is a separate option. Format: <option name>;<state to transition to>;<assignee>");
   public static final AttributeTypeToken<Boolean> Default = createType(1152921875139002538L, "Default", "Default");
   public static final AttributeTypeToken<String> Description = createType(1152921504606847196L, "Description", "Detailed explanation.");
   public static final AttributeTypeToken<String> DslSheet = createType(1152921504606847197L, "DSL Sheet", "XText DSL Sheet for ATS");
   public static final AttributeTypeToken<String> DuplicatedPcrId = createType(1152922093378076842L, "Duplicated PCR Id");
   public static final AttributeTypeToken<Date> EndDate = createType(1152921504606847383L, "End Date");
   public static final AttributeTypeToken<Date> EstimatedCompletionDate = createType(1152921504606847165L, "Estimated Completion Date", "Date the changes will be completed.");
   public static final AttributeTypeToken<Double> EstimatedHours = createType(1152921504606847182L, "Estimated Hours", "Hours estimated to implement the changes associated with this Action.\nIncludes estimated hours for workflows, tasks and reviews.");
   public static final AttributeTypeToken<Date> EstimatedReleaseDate = createType(1152921504606847164L, "Estimated Release Date", "Date the changes will be made available to the users.");
   public static final AttributeTypeToken<String> FullName = createType(1152921504606847198L, "Full Name", "Expanded and descriptive name.");
   public static final AttributeTypeToken<String> GoalOrderVote = createType(1152921504606847211L, "Goal Order Vote", "Vote for order item belongs to within goal.");
   public static final AttributeTypeToken<String> Holiday = createType(72064629481881851L, "Holiday");
   public static final AttributeTypeToken<String> HoursPerWorkDay = createType(1152921504606847187L, "Hours Per Work Day");
   public static final AttributeTypeToken<String> IPT = createType(6025996821081174931L, "IPT", "Integrated Product Team");
   public static final AttributeTypeToken<String> IptTeam = createType(1364016887343371647L, "IPT Team");
   public static final AttributeTypeToken<String> KanbanIgnoreStates = createType(726700946264587643L, "kb.Ignore State");
   public static final AttributeTypeToken<String> KanbanStoryName = createType(72645877009467643L, "kb.Story Name");
   public static final AttributeTypeToken<String> LegacyPcrId = createType(1152921504606847219L, "Legacy PCR Id", "Field to register problem change report id from legacy items imported into ATS.");
   public static final AttributeTypeToken<String> LocChanged= createType(1152921504606847207L, "LOC Changed", "Total Lines of Code Changed");
   public static final AttributeTypeToken<String> LocReviewed = createType(1152921504606847208L, "LOC Reviewed", "Total Lines of Code Reviewed");
   public static final AttributeTypeToken<String> Location = createType(1152921504606847223L, "Location", "Enter location of materials to review.");
   public static final AttributeTypeToken<String> Log = createType(1152921504606847202L, "Log");
   public static final AttributeTypeToken<String> MeetingAttendee = createType(1152921504606847225L, "Meeting Attendee", "Attendee of meeting.");
   public static final AttributeTypeToken<Date> MeetingDate = createType(5605018543870805270L, "Meeting Date");
   public static final AttributeTypeToken<String> MeetingLength = createType(1152921504606847188L, "Meeting Length", "Length of meeting.");
   public static final AttributeTypeToken<String> MeetingLocation = createType(1152921504606847224L, "Meeting Location", "Location meeting is held.");
   public static final AttributeTypeToken<String> Namespace = createType(4676151691645786526L, "Namespace");
   public static final AttributeTypeToken<Date> NeedBy = createType(1152921504606847163L, "Need By", "Hard schedule date that workflow must be completed.");
   public static final AttributeTypeToken<Boolean> NextVersion = createType(1152921504606847157L, "Next Version", "True if version artifact is \"Next\" version to be released.");
   public static final AttributeTypeToken<String> Numeric1 = createType(1152921504606847184L, "Numeric1", "Open field for user to be able to enter numbers for sorting.");
   public static final AttributeTypeToken<String> Numeric2 = createType(1152921504606847185L, "Numeric2", Numeric1.getDescription());
   public static final AttributeTypeToken<String> OperationalImpact = createType(1152921504606847213L, "Operational Impact");
   public static final AttributeTypeToken<String> OperationalImpactDescription = createType(1152921504606847214L, "Operational Impact Description");
   public static final AttributeTypeToken<String> OperationalImpactWorkaround = createType(1152921504606847215L, "Operational Impact Workaround");
   public static final AttributeTypeToken<String> OperationalImpactWorkaroundDescription = createType(1152921504606847216L, "Operational Impact Workaround Description");
   public static final AttributeTypeToken<String> OriginatingPcrId = createType(1152922093379125418L, "Originating PCR Id");
   public static final AttributeTypeToken<String> PagesChanged= createType(1152921504606847209L, "Pages Changed", "Total Pages of Changed");
   public static final AttributeTypeToken<String> PagesReviewed = createType(1152921504606847210L, "Pages Reviewed", "Total Pages Reviewed");
   public static final AttributeTypeToken<String> PcrToolId = createType(1152922093370736810L, "PCR Tool Id");
   public static final AttributeTypeToken<Integer> PercentComplete = createType(1152921504606847183L, "Percent Complete");
   public static final AttributeTypeToken<Integer> PercentRework = createType(1152921504606847189L, "Percent Rework");
   public static final AttributeTypeToken<Integer> PlannedPoints = createType(232851836925913430L, "Planned Points");
   public static final AttributeTypeToken<String> Points = createType(1152921504606847178L, "Points", "Abstract value that describes risk, complexity, and size of Actions.");
   public static final AttributeTypeToken<String> PointsAttributeType = createType(1152921573057888257L, "Points Attribute Type", "Used to store the agile points type name (ats.Points or ats.Points Numeric).");
   public static final AttributeTypeToken<Double> PointsNumeric = createType(1728793301637070003L, "Points Numeric", "Abstract value that describes risk, complexity, and size of Actions as float.");
   public static final AttributeTypeToken<String> PriorityType = createType(1152921504606847179L, "Priority", "1 = High; 5 = Low");
   public static final AttributeTypeToken<String> Problem = createType(1152921504606847193L, "Problem", "Problem found during analysis.");
   public static final AttributeTypeToken<ArtifactId> ProgramId = createType(1152922093377028266L, "Program Id");
   public static final AttributeTypeToken<String> ProposedResolution = createType(1152921504606847194L, "Proposed Resolution", "Recommended resolution.");
   public static final AttributeTypeToken<String> QuickSearch = createType(72063457009467643L, "ATS Quick Search", "Saved ATS Quick Searches.");
   public static final AttributeTypeToken<String> Rationale = createType(1152922093379715242L, "Rationale");
   public static final AttributeTypeToken<String> RelatedPeerWorkflowDefinitionReference = createType(6245695017677665082L, "Related Peer Workflow Definition", "Specific work flow definition id used by Peer To Peer Reviews for this Team");
   public static final AttributeTypeToken<String> RelatedTaskWorkDefinitionReference = createType(2492475839748929444L, "Related Task Workflow Definition Reference", "Specific work flow definition id used by Tasks related to this Workflow");
   public static final AttributeTypeToken<String> RelatedToState = createType(1152921504606847204L, "Related To State", "State of parent workflow this object is related to.");
   public static final AttributeTypeToken<Date> ReleaseDate = createType(1152921504606847175L, "Release Date", "Date the changes were made available to the users.");
   public static final AttributeTypeToken<Boolean> Released = createType(1152921504606847155L, "Released", "True if object is in a released state.");
   public static final AttributeTypeToken<String> Resolution = createType(1152921504606847195L, "Resolution", "Implementation details.");
   public static final AttributeTypeToken<String> ReviewBlocks = createType(1152921504606847176L, "Review Blocks", "Review Completion will block it's parent workflow in this manner.");
   public static final AttributeTypeToken<String> ReviewDefect = createType(1152921504606847222L, "Review Defect");
   public static final AttributeTypeToken<String> ReviewFormalType = createType(1152921504606847177L, "Review Formal Type");
   public static final AttributeTypeToken<Integer> RequestedHours = createType(5708922143198125133L, "Requested Hours");
   public static final AttributeTypeToken<Integer> RequestedFunding = createType(323824792847329487L, "Requested Funding");
   public static final AttributeTypeToken<String> Role = createType(1152921504606847226L, "Role");
   public static final AttributeTypeToken<String> RuleDefinition = createType(1152921504606847150L, "Rule Definition");
   public static final AttributeTypeToken<String> SmaNote = createType(1152921504606847205L, "SMA Note", "Notes applicable to ATS object");
   public static final AttributeTypeToken<Date> StartDate = createType(1152921504606847382L, "Start Date");
   public static final AttributeTypeToken<String> State = createType(1152921504606847191L, "State", "States of workflow state machine.");
   public static final AttributeTypeToken<String> StateNotes = createType(1152921504606847203L, "State Notes");
   public static final AttributeTypeToken<String> SwEnhancement = createType(1152921504606847227L, "SW Enhancement");
   public static final AttributeTypeToken<ArtifactId> TaskToChangedArtifactReference = createType(1153126013769613562L, "Task To Changed Artifact Reference");
   public static final AttributeTypeToken<ArtifactId> TeamDefinitionReference = createType(4730961339090285773L, "Team Definition Reference");
   public static final AttributeTypeToken<String> TeamWorkflowArtifactType = createType(1152921504606847148L, "Team Workflow Artifact Type", "Specific Artifact Type to use in creation of Team Workflow");
   public static final AttributeTypeToken<String> TestToSourceLocator = AttributeTypeToken.valueOf(130595201919637916L, "Test Run to Source Locator");
   public static final AttributeTypeToken<String> Title = createType(CoreAttributeTypes.Name.getId(), CoreAttributeTypes.Name.getName(), "Enter clear and concise title that can be generally understood.");
   public static final AttributeTypeToken<Integer> UnPlannedPoints = createType(284254492767020802L, "Un-Planned Points");
   public static final AttributeTypeToken<Boolean> UnPlannedWork = createType(2421093774890249189L, "Unplanned Work");
   public static final AttributeTypeToken<Boolean> ValidationRequired = createType(1152921504606847146L, "Validation Required", "If selected, originator will be asked to validate the implementation.");
   public static final AttributeTypeToken<String> VerificationCodeInspection = createType(3454966334779726518L, "Verification Code Inspection");
   public static final AttributeTypeToken<Boolean> VersionLocked = createType(1152921504606847156L, "Version Locked", "True if version artifact is locked.");
   public static final AttributeTypeToken<String> WeeklyBenefit = createType(1152921504606847186L, "Weekly Benefit", "Estimated number of hours that will be saved over a single year if this change is completed.");
   public static final AttributeTypeToken<String> WorkPackage = createType(1152921504606847206L, "Work Package", "Designated accounting work package for completing workflow.");
   public static final AttributeTypeToken<String> WorkPackageId = createType(1152921504606847872L, "Work Package ID");
   public static final AttributeTypeToken<String> WorkPackageProgram = createType(1152921504606847873L, "Work Package Program");
   public static final AttributeTypeToken<ArtifactId> WorkPackageReference = createType(473096133909456789L, "Work Package Reference", "Designated accounting work package for completing workflow.");
   public static final AttributeTypeToken<String> WorkPackageType = createType(72057594037928065L, "Work Package Type");
   public static final AttributeTypeToken<String> WorkType = createType(72063456955810043L, "Work Type", "Work Type of this Team.");
   public static final AttributeTypeToken<ArtifactId> WorkflowDefinitionReference = createType(53049621055799825L, "Workflow Definition Reference", "Specific work flow definition id used by this Workflow artifact");
   // @formatter:on

   public static <T> AttributeTypeToken<T> createType(Long id, String name) {
      AttributeTypeToken<T> type = AttributeTypeToken.valueOf(id, "ats." + name);
      nameToTypeMap.put(type.getName(), type);
      return type;
   }

   public static <T> AttributeTypeToken<T> createType(Long id, String name, String description) {
      AttributeTypeToken<T> type = AttributeTypeToken.create(id, "ats." + name, description);
      nameToTypeMap.put(type.getName(), type);
      return type;
   }

   public static <T> AttributeTypeToken<T> getTypeByName(String name) {
      return (AttributeTypeToken<T>) nameToTypeMap.get(name);
   }
}