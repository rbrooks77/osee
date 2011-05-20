package org.eclipse.osee.ats.editor.history.column;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.nebula.widgets.xviewer.XViewerValueColumn;
import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.ats.core.type.AtsAttributeTypes;
import org.eclipse.osee.ats.core.workdef.StateDefinition;
import org.eclipse.osee.ats.core.workflow.SMAState;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.framework.core.model.IBasicUser;
import org.eclipse.osee.framework.core.util.WorkPageType;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;

public class EventColumn extends XViewerValueColumn {

   private static EventColumn instance = new EventColumn();

   public static EventColumn getInstance() {
      return instance;
   }

   public EventColumn() {
      super("ats.history.Event", "Event", 400, SWT.LEFT, true, SortDataType.String, false, "");
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public EventColumn copy() {
      EventColumn newXCol = new EventColumn();
      copy(this, newXCol);
      return newXCol;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) {
      try {
         if (element instanceof String) {
            return (String) element;
         }
         if (element instanceof Change) {
            Change change = (Change) element;
            if (change.getItemTypeName().equals(AtsAttributeTypes.CurrentState.getName())) {
               return processCurrentStateChange(change);
            }
            if (change.getItemTypeName().equals(AtsAttributeTypes.CurrentStateType.getName())) {
               if (change.getIsValue().equals(WorkPageType.Completed.name())) {
                  return "Completed";
               } else if (change.getIsValue().equals(WorkPageType.Cancelled.name())) {
                  return "Cancelled";
               }
            }
         }
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }
      return "";
   }

   @Override
   public Image getColumnImage(Object element, XViewerColumn col, int columnIndex) {
      if (col.getName().equals("Event")) {
         String text = getColumnText(element, EventColumn.getInstance(), columnIndex);
         if (text.startsWith("Assigned") || text.equals("UnAssigned")) {
            return ImageManager.getImage(FrameworkImage.USERS);
         } else if (text.startsWith("Statused")) {
            return ImageManager.getImage(FrameworkImage.GREEN_PLUS);
         } else if (text.startsWith("Transition")) {
            return ImageManager.getImage(AtsImage.TRANSITION);
         } else if (text.startsWith("Created")) {
            return ImageManager.getImage(AtsImage.ACTION);
         } else if (text.startsWith("Completed")) {
            return ImageManager.getImage(FrameworkImage.DOT_GREEN);
         } else if (text.startsWith("Cancelled")) {
            return ImageManager.getImage(FrameworkImage.X_RED);
         }
      }
      return null;
   }

   public String processCurrentStateChange(Change change) {
      try {
         StateDefinition stateDef = new StateDefinition("");
         stateDef.setWorkPageType(WorkPageType.Working);
         SMAState was = new SMAState(stateDef, change.getWasValue());
         SMAState is = new SMAState(stateDef, change.getIsValue());
         if (change.getWasValue().equals("")) {
            return "Created in [" + is.getName() + "] state";
         } else if (!was.getName().equals(is.getName())) {
            return "Transition from [" + was.getName() + "] to [" + is.getName() + "]";
         }
         if (was.getName().equals(is.getName()) && (was.getPercentComplete() != is.getPercentComplete() || !was.getHoursSpentStr().equals(
            is.getHoursSpentStr()))) {
            return "Statused [" + is.getName() + "] to: " + is.getPercentComplete() + "% and " + getHoursSpent(is) + " hrs";
         }
         Collection<? extends IBasicUser> wasAssignees = was.getAssignees();
         Collection<? extends IBasicUser> isAssignees = is.getAssignees();
         Set<IBasicUser> assigned = new HashSet<IBasicUser>();
         Set<IBasicUser> unAssigned = new HashSet<IBasicUser>();
         for (IBasicUser isAssignee : isAssignees) {
            if (!wasAssignees.contains(isAssignee)) {
               assigned.add(isAssignee);
            }
         }
         for (IBasicUser wasAssignee : wasAssignees) {
            if (!isAssignees.contains(wasAssignee)) {
               unAssigned.add(wasAssignee);
            }
         }
         if (unAssigned.size() > 0) {
            return "UnAssigned [" + is.getName() + "] removed " + Artifacts.toString("; ", unAssigned);
         }
         if (assigned.size() > 0) {
            return "Assigned [" + is.getName() + "] to " + Artifacts.toString("; ", assigned);
         }
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }
      return "";
   }

   private String getHoursSpent(SMAState state) {
      return Strings.isValid(state.getHoursSpentStr()) ? state.getHoursSpentStr() : "0";
   }
}
