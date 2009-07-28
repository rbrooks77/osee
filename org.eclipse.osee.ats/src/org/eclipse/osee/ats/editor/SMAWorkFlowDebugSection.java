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
package org.eclipse.osee.ats.editor;

import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.TeamDefinitionArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.XFormToolkit;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkItemDefinition;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPageDefinition;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkRuleDefinition;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.forms.widgets.Section;

/**
 * @author Donald G. Dunne
 */
public class SMAWorkFlowDebugSection extends SectionPart {

   private Composite workComp;
   private final XFormToolkit toolkit;
   private final SMAManager smaMgr;

   /**
    * @param parent
    * @param toolkit
    * @param style
    * @param page
    * @param smaMgr
    * @throws Exception
    */
   public SMAWorkFlowDebugSection(Composite parent, XFormToolkit toolkit, int style, SMAManager smaMgr) throws OseeCoreException {
      super(parent, toolkit, style | Section.TWISTIE | Section.TITLE_BAR);
      this.smaMgr = smaMgr;
      this.toolkit = toolkit;
   }

   /* (non-Javadoc)
    * @see org.eclipse.ui.forms.AbstractFormPart#initialize(org.eclipse.ui.forms.IManagedForm)
    */
   @Override
   public void initialize(final IManagedForm form) {
      super.initialize(form);

      Section section = getSection();

      workComp = toolkit.createClientContainer(section, 1);
      section.setText("Debug - Admin Only");

      Hyperlink link = toolkit.createHyperlink(workComp, "Dirty Report", SWT.NONE);
      link.addHyperlinkListener(new IHyperlinkListener() {

         public void linkEntered(HyperlinkEvent e) {
         }

         public void linkExited(HyperlinkEvent e) {
         }

         public void linkActivated(HyperlinkEvent e) {
            Result result = smaMgr.getEditor().isDirtyResult();
            AWorkbench.popup("Success", Strings.isValid(result.getText()) ? result.getText() : "Success");
         }

      });
      link = toolkit.createHyperlink(workComp, "Refresh Dirty", SWT.NONE);
      link.addHyperlinkListener(new IHyperlinkListener() {

         public void linkEntered(HyperlinkEvent e) {
         }

         public void linkExited(HyperlinkEvent e) {
         }

         public void linkActivated(HyperlinkEvent e) {
            smaMgr.getEditor().onDirtied();
         }

      });

      try {
         // Display team definition
         if (smaMgr.getSma() instanceof TeamWorkFlowArtifact) {
            TeamDefinitionArtifact teamDef = ((TeamWorkFlowArtifact) smaMgr.getSma()).getTeamDefinition();
            addDebug("Team Definition: " + teamDef);
            for (WorkRuleDefinition workItemDefinition : teamDef.getWorkRules()) {
               addDebug("        " + workItemDefinition.toString());
            }
         }

         // Display workflows
         addDebug("WorkflowId: " + smaMgr.getWorkFlowDefinition().getId());
         if (smaMgr.getWorkFlowDefinition().getParentId() != null && !smaMgr.getWorkFlowDefinition().getParentId().equals(
               "")) addDebug("Inherit Workflow from Parent Id: " + smaMgr.getWorkFlowDefinition().getParentId());
         for (WorkRuleDefinition workItemDefinition : smaMgr.getWorkFlowDefinition().getWorkRules()) {
            addDebug("        " + workItemDefinition.toString());
         }

         // Display pages
         for (WorkPageDefinition atsPage : smaMgr.getWorkFlowDefinition().getPagesOrdered()) {
            addDebug(atsPage.toString());
            for (WorkItemDefinition wid : atsPage.getWorkItems(true)) {
               addDebug("        " + wid.toString());
            }
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE, ex);
      }

   }

   public void addDebug(String str) {
      toolkit.createText(workComp, str, SWT.MULTI | SWT.WRAP);
      workComp.layout();
   }

}
