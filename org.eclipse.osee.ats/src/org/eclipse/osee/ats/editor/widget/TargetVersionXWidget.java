/*
 * Created on Aug 2, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.editor.widget;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.VersionArtifact.VersionReleaseType;
import org.eclipse.osee.ats.editor.SMAManager;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.ats.workflow.item.AtsWorkDefinitions;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.widgets.XHyperlinkLabelCmdValueSelection;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Donald G. Dunne
 */
public class TargetVersionXWidget extends XHyperlinkLabelCmdValueSelection {

   private final SMAManager smaMgr;

   /**
    * @param label
    */
   public TargetVersionXWidget(SMAManager smaMgr, Composite composite, int horizontalSpan, XModifiedListener xModListener) {
      super("Target Version", false);
      this.smaMgr = smaMgr;
      if (xModListener != null) {
         addXModifiedListener(xModListener);
      }
      final SMAManager fSmaMgr = smaMgr;
      addXModifiedListener(new XModifiedListener() {

         @Override
         public void widgetModified(XWidget widget) {
            try {
               if (fSmaMgr.promptChangeVersion(
                     AtsUtil.isAtsAdmin() ? VersionReleaseType.Both : VersionReleaseType.UnReleased, false)) {
                  refresh();
               }
            } catch (Exception ex) {
               OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }
      });
      super.createControls(composite, horizontalSpan);

   }

   @Override
   public void refresh() {
      super.refresh();
      if (getControl().isDisposed()) {
         return;
      }
      try {
         String str = "";
         if (smaMgr.getTargetedForVersion() != null) {
            str = smaMgr.getTargetedForVersion() + "";
         } else {
            str = "<edit>";
         }
         setValueLabel(str);
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }

   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.XText#isValid()
    */
   @Override
   public IStatus isValid() {
      IStatus status = super.isValid();
      // Don't transition without targeted version if so configured
      try {
         boolean required =
               smaMgr.teamDefHasWorkRule(AtsWorkDefinitions.RuleWorkItemId.atsRequireTargetedVersion.name()) || smaMgr.getWorkPageDefinition().hasWorkRule(
                     AtsWorkDefinitions.RuleWorkItemId.atsRequireTargetedVersion.name());

         if (required && smaMgr.getTargetedForVersion() == null) {
            status = new Status(IStatus.ERROR, AtsPlugin.PLUGIN_ID, "Workflow must be targeted for a version.");
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return status;
   }

}
