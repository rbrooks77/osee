/*
 * Created on May 31, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.workflow.transition;

/**
 * @author Donald G. Dunne
 */
public class TransitionResult implements ITransitionResult {

   public static TransitionResult MUST_BE_TARGETED_FOR_VERSION = new TransitionResult(
      "Actions must be targeted for a Version.  Please set \"Target Version\" before transition.");
   public static TransitionResult NO_WORKFLOWS_PROVIDED_FOR_TRANSITION = new TransitionResult(
      "No Workflows provided for transition; aborting.");
   public static TransitionResult TO_STATE_CANT_BE_NULL = new TransitionResult(
      "To-State can not be null for transition.");
   public static TransitionResult MUST_BE_ASSIGNED =
      new TransitionResult(
         "You must be assigned to transition this workflow.\nContact Assignee or Select Priviledged Edit for Authorized Overriders.");
   public static TransitionResult DELETE_WORKING_BRANCH_BEFORE_CANCEL = new TransitionResult(
      "Working Branch exists.\n\nPlease delete working branch before transition to cancel.");
   public static TransitionResult WORKING_BRANCH_BEING_COMMITTED = new TransitionResult(
      "Working Branch is being Committed.\n\nPlease wait till commit completes to transition.");
   public static TransitionResult WORKING_BRANCH_EXISTS = new TransitionResult(
      "Working Branch exists.\n\nPlease commit or delete working branch before transition.");
   public static TransitionResult CAN_NOT_TRANSITION_WITH_SYSTEM_USER_ASSIGNED = new TransitionResult(
      "Can not transition with \"Guest\", or \"OseeSystem\" user as assignee.");
   public static TransitionResult CAN_NOT_TRANSITION_AS_SYSTEM_USER = new TransitionResult(
      "Can not transition as \"Guest\", or \"OseeSystem\".");
   public static TransitionResult COMPLETE_BLOCKING_REVIEWS = new TransitionResult(
      "All Blocking Reviews must be completed before transition.");
   public static TransitionResult TASKS_NOT_COMPLETED = new TransitionResult("Tasks Not Completed");

   private final String details;
   private final Exception exception;

   public TransitionResult(String details, Exception ex) {
      this.details = details;
      this.exception = ex;
   }

   public TransitionResult(String details) {
      this(details, null);
   }

   @Override
   public String getDetails() {
      return details;
   }

   @Override
   public String toString() {
      return getDetails();
   }

   @Override
   public Exception getException() {
      return exception;
   }

}
