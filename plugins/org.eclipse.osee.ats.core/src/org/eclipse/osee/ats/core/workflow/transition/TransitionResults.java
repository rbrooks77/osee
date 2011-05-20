/*
 * Created on May 31, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.workflow.transition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.osee.ats.core.internal.Activator;
import org.eclipse.osee.ats.core.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.framework.core.util.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Donald G. Dunne
 */
public class TransitionResults {

   boolean cancelled;

   private final List<ITransitionResult> results = new ArrayList<ITransitionResult>();

   private final Map<AbstractWorkflowArtifact, List<ITransitionResult>> awaToResults =
      new HashMap<AbstractWorkflowArtifact, List<ITransitionResult>>();

   public void addResult(AbstractWorkflowArtifact awa, ITransitionResult result) {
      List<ITransitionResult> results = awaToResults.get(awa);
      if (results == null) {
         results = new ArrayList<ITransitionResult>();
         awaToResults.put(awa, results);
      }
      results.add(result);
   }

   public void clear() {
      results.clear();
      awaToResults.clear();
   }

   public void addResult(ITransitionResult result) {
      results.add(result);
   }

   public boolean isEmpty() {
      return results.isEmpty() && awaToResults.isEmpty();
   }

   public boolean isCancelled() {
      return cancelled;
   }

   public void setCancelled(boolean cancelled) {
      this.cancelled = cancelled;
   }

   public boolean contains(String string) {
      return toString().contains(string);
   }

   public boolean contains(TransitionResult transitionResult) {
      return results.contains(transitionResult);
   }

   public boolean isEmpty(AbstractWorkflowArtifact awa) {
      List<ITransitionResult> awaResults = awaToResults.get(awa);
      if (awaResults == null || awaResults.isEmpty()) {
         return true;
      }
      return false;
   }

   public boolean contains(AbstractWorkflowArtifact awa, TransitionResult transitionResult) {
      List<ITransitionResult> awaResults = awaToResults.get(awa);
      if (awaResults == null) {
         return false;
      }
      return awaResults.contains(transitionResult);
   }

   public String getResultString() {
      if (results.isEmpty() && awaToResults.isEmpty()) {
         return "<Empty>";
      }
      StringBuffer sb = new StringBuffer();
      sb.append("Reason(s):\n");
      appendResultsString(sb, results);
      for (AbstractWorkflowArtifact awa : awaToResults.keySet()) {
         sb.append("\n");
         sb.append(awa.getArtifactTypeName());
         sb.append(" [");
         sb.append(awa.getHumanReadableId());
         sb.append("] Titled [");
         sb.append(awa.getName());
         sb.append("]\n\n");
         appendResultsString(sb, awaToResults.get(awa));
      }

      return sb.toString();
   }

   /**
    * Log exceptions to OseeLog. Don't always want to do this due to testing.
    */
   public void logExceptions() {
      for (ITransitionResult result : results) {
         Exception ex = result.getException();
         if (ex != null) {
            OseeLog.log(Activator.class, Level.SEVERE, result.getDetails(), ex);
         }
      }
      for (AbstractWorkflowArtifact awa : awaToResults.keySet()) {
         List<ITransitionResult> results = awaToResults.get(awa);
         for (ITransitionResult result : results) {
            Exception ex = result.getException();
            if (ex != null) {
               String message = awa.toStringWithId() + " - " + result.getDetails();
               OseeLog.log(Activator.class, Level.SEVERE, message, ex);
            }
         }
      }
   }

   public void appendResultsString(StringBuffer sb, List<ITransitionResult> results) {
      for (ITransitionResult result : results) {
         sb.append("    - ");
         sb.append(result.getDetails());
         if (result.getException() != null) {
            if (Strings.isValid(result.getException().getLocalizedMessage())) {
               sb.append(" - Exception [");
               sb.append(result.getException().getLocalizedMessage());
               sb.append("] (see log for details)");
            } else {
               sb.append(" - (see log for details)");
            }
         }
         sb.append("\n");
      }

   }

   public XResultData getResultXResultData() {
      XResultData resultData = new XResultData(false);
      resultData.log("Transition Failed");
      String str = getResultString();
      resultData.addRaw(str);
      return resultData;
   }

   @Override
   public String toString() {
      return getResultString();
   }
}
