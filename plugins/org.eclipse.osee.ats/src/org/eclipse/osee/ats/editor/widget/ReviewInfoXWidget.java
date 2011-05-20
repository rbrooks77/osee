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
package org.eclipse.osee.ats.editor.widget;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.actions.NewDecisionReviewJob;
import org.eclipse.osee.ats.actions.NewPeerToPeerReviewJob;
import org.eclipse.osee.ats.core.review.AbstractReviewArtifact;
import org.eclipse.osee.ats.core.review.DecisionReviewManager;
import org.eclipse.osee.ats.core.review.PeerToPeerReviewManager;
import org.eclipse.osee.ats.core.review.ReviewManager;
import org.eclipse.osee.ats.core.team.TeamState;
import org.eclipse.osee.ats.core.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.util.AtsUtilCore;
import org.eclipse.osee.ats.core.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.core.workflow.transition.TransitionHelper;
import org.eclipse.osee.ats.core.workflow.transition.TransitionManager;
import org.eclipse.osee.ats.core.workflow.transition.TransitionOption;
import org.eclipse.osee.ats.core.workflow.transition.TransitionResults;
import org.eclipse.osee.ats.editor.SMAEditor;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.ats.util.Overview;
import org.eclipse.osee.ats.util.widgets.dialog.StateListAndTitleDialog;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.util.IWorkPage;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.XFormToolkit;
import org.eclipse.osee.framework.ui.skynet.widgets.XLabelValueBase;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.IMessageManager;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.Hyperlink;

/**
 * @author Donald G. Dunne
 */
public class ReviewInfoXWidget extends XLabelValueBase {

   private final IWorkPage forState;
   private final List<Label> labelWidgets = new ArrayList<Label>();
   private Composite destroyableComposite = null;
   private final Composite composite;
   private final IManagedForm managedForm;
   private final int horizontalSpan;
   private final XFormToolkit toolkit;
   private final TeamWorkFlowArtifact teamArt;

   public ReviewInfoXWidget(IManagedForm managedForm, XFormToolkit toolkit, final TeamWorkFlowArtifact teamArt, final IWorkPage forState, Composite composite, int horizontalSpan) {
      super("\"" + forState.getPageName() + "\" State Reviews");
      this.managedForm = managedForm;
      this.toolkit = toolkit;
      this.teamArt = teamArt;
      this.forState = forState;
      this.composite = composite;
      this.horizontalSpan = horizontalSpan;
      reDisplay();
   }

   public void reDisplay() {
      if (composite != null && composite.isDisposed()) {
         return;
      }
      if (destroyableComposite != null) {
         destroyableComposite.dispose();
      }
      destroyableComposite = new Composite(composite, SWT.None);
      destroyableComposite.setLayout(ALayout.getZeroMarginLayout(4, false));

      setToolTip("Blocking Reviews must be completed before transtion.  Select Review hyperlink to view.");
      createWidgets(managedForm, destroyableComposite, horizontalSpan);

      try {
         addAdminRightClickOption();
         Collection<AbstractReviewArtifact> revArts = ReviewManager.getReviews(teamArt, forState);
         if (revArts.isEmpty()) {
            setValueText("No Reviews Created");
         }

         Hyperlink link = toolkit.createHyperlink(destroyableComposite, "[Add Decision Review]", SWT.NONE);
         link.addHyperlinkListener(new IHyperlinkListener() {

            @Override
            public void linkEntered(HyperlinkEvent e) {
               // do nothing
            }

            @Override
            public void linkExited(HyperlinkEvent e) {
               // do nothing
            }

            @Override
            public void linkActivated(HyperlinkEvent e) {
               try {
                  StateListAndTitleDialog dialog =
                     new StateListAndTitleDialog("Create Decision Review",
                        "Select state to that review will be associated with.",
                        teamArt.getWorkDefinition().getStateNames());
                  dialog.setInitialSelections(new Object[] {forState.getPageName()});
                  if (dialog.open() == 0) {
                     if (!Strings.isValid(dialog.getReviewTitle())) {
                        AWorkbench.popup("ERROR", "Must enter review title");
                        return;
                     }
                     NewDecisionReviewJob job =
                        new NewDecisionReviewJob(teamArt, null, dialog.getReviewTitle(), dialog.getSelectedState(),
                           null, DecisionReviewManager.getDefaultDecisionReviewOptions(), null, new Date(),
                           UserManager.getUser());
                     job.setUser(true);
                     job.setPriority(Job.LONG);
                     job.schedule();
                  }
               } catch (Exception ex) {
                  OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
               }
            }
         });

         link = toolkit.createHyperlink(destroyableComposite, "[Add Peer to Peer Review]", SWT.NONE);
         link.addHyperlinkListener(new IHyperlinkListener() {

            @Override
            public void linkEntered(HyperlinkEvent e) {
               // do nothing
            }

            @Override
            public void linkExited(HyperlinkEvent e) {
               // do nothing
            }

            @Override
            public void linkActivated(HyperlinkEvent e) {
               try {
                  StateListAndTitleDialog dialog =
                     new StateListAndTitleDialog("Add Peer to Peer Review",
                        "Select state to that review will be associated with.",
                        teamArt.getWorkDefinition().getStateNames());
                  dialog.setInitialSelections(new Object[] {forState.getPageName()});
                  dialog.setReviewTitle(PeerToPeerReviewManager.getDefaultReviewTitle(teamArt));
                  if (dialog.open() == 0) {
                     if (!Strings.isValid(dialog.getReviewTitle())) {
                        AWorkbench.popup("ERROR", "Must enter review title");
                        return;
                     }
                     NewPeerToPeerReviewJob job =
                        new NewPeerToPeerReviewJob(teamArt, dialog.getReviewTitle(), dialog.getSelectedState());
                     job.setUser(true);
                     job.setPriority(Job.LONG);
                     job.schedule();
                  }
               } catch (Exception ex) {
                  OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
               }
            }
         });
         if (revArts.size() > 0) {
            Composite workComp = toolkit.createContainer(destroyableComposite, 1);
            workComp.setLayoutData(new GridData(GridData.FILL_BOTH | GridData.VERTICAL_ALIGN_BEGINNING));
            GridData gd = new GridData();
            gd.horizontalIndent = 20;
            gd.horizontalSpan = 4;
            workComp.setLayoutData(gd);

            for (AbstractReviewArtifact revArt : revArts) {
               createReviewHyperlink(workComp, managedForm, toolkit, revArt, forState);
            }
         }

      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   @Override
   public String toString() {
      try {
         return "ReviewInfoXWidget for SMA \"" + teamArt + "\"";
      } catch (Exception ex) {
         return "ReviewInfoXWidget " + ex.getLocalizedMessage();
      }
   }

   public static String toHTML(final TeamWorkFlowArtifact teamArt, IWorkPage forState) throws OseeCoreException {
      if (ReviewManager.getReviews(teamArt, forState).isEmpty()) {
         return "";
      }
      StringBuffer html = new StringBuffer();
      try {
         html.append(AHTML.addSpace(1) + AHTML.getLabelStr(AHTML.LABEL_FONT,
            "\"" + forState.getPageName() + "\" State Reviews"));
         html.append(AHTML.startBorderTable(100, Overview.normalColor, ""));
         html.append(AHTML.addHeaderRowMultiColumnTable(new String[] {"Review Type", "Title", "ID"}));
         for (AbstractReviewArtifact art : ReviewManager.getReviews(teamArt, forState)) {
            html.append(AHTML.addRowMultiColumnTable(new String[] {
               art.getArtifactTypeName(),
               art.getName(),
               art.getHumanReadableId()}));
         }
         html.append(AHTML.endBorderTable());
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
         return "Review Exception - " + ex.getLocalizedMessage();
      }
      return html.toString();
   }

   public String toHTML() throws OseeCoreException {
      return ReviewInfoXWidget.toHTML(teamArt, forState);
   }

   private void createReviewHyperlink(Composite comp, IManagedForm managedForm, XFormToolkit toolkit, final AbstractReviewArtifact revArt, IWorkPage forState) throws OseeCoreException {

      Composite workComp = toolkit.createContainer(comp, 1);
      workComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING));
      workComp.setLayout(ALayout.getZeroMarginLayout(3, false));

      Label strLabel = new Label(workComp, SWT.NONE);
      labelWidgets.add(strLabel);
      if (revArt.isBlocking() && !revArt.isCompletedOrCancelled()) {
         strLabel.setText("State Blocking [" + revArt.getArtifactTypeName() + "] must be completed: ");
         IMessageManager messageManager = managedForm.getMessageManager();
         if (messageManager != null) {
            messageManager.addMessage(
               "validation.error",
               "\"" + forState.getPageName() + "\" State has a blocking [" + revArt.getArtifactTypeName() + "] that must be completed.",
               null, IMessageProvider.ERROR, strLabel);
         }
      } else if (!revArt.isCompletedOrCancelled()) {
         strLabel.setText("Open [" + revArt.getArtifactTypeName() + "] exists: ");
         IMessageManager messageManager = managedForm.getMessageManager();
         if (messageManager != null) {
            messageManager.addMessage("validation.error",
               "\"" + forState.getPageName() + "\" State has an open [" + revArt.getArtifactTypeName() + "].", null,
               IMessageProvider.WARNING, strLabel);
         }
      } else {
         strLabel.setText(revArt.getStateMgr().getCurrentStateName() + " [" + revArt.getArtifactTypeName() + "] exists: ");
      }

      String str = "[" + revArt.getName() + "]";
      Hyperlink hyperLabel =
         toolkit.createHyperlink(workComp, (str.length() > 300 ? Strings.truncate(str, 300) + "..." : str), SWT.NONE);
      hyperLabel.setToolTipText("Select to open review");
      hyperLabel.addListener(SWT.MouseUp, new Listener() {
         @Override
         public void handleEvent(Event event) {
            SMAEditor.editArtifact(revArt);
         }
      });
   }

   public void addAdminRightClickOption() throws OseeCoreException {
      // If ATS Admin, allow right-click to auto-complete tasks
      if (AtsUtilCore.isAtsAdmin() && !AtsUtil.isProductionDb()) {
         labelWidget.addListener(SWT.MouseUp, new Listener() {
            @Override
            public void handleEvent(Event event) {
               if (event.button == 3) {
                  if (!MessageDialog.openConfirm(Displays.getActiveShell(), "Auto Complete Reviews",
                     "ATS Admin\n\nAuto Complete Reviews?")) {
                     return;
                  }
                  try {
                     List<AbstractWorkflowArtifact> awas = new ArrayList<AbstractWorkflowArtifact>();
                     for (AbstractReviewArtifact revArt : ReviewManager.getReviewsFromCurrentState(teamArt)) {
                        if (!revArt.isCompletedOrCancelled()) {
                           if (revArt.getStateMgr().isUnAssigned()) {
                              revArt.getStateMgr().setAssignee(UserManager.getUser());
                           }
                           awas.add(revArt);
                        }
                     }
                     TransitionHelper helper =
                        new TransitionHelper("ATS Auto Complete Reviews", awas, TeamState.Completed.getPageName(),
                           null, null, TransitionOption.OverrideTransitionValidityCheck, TransitionOption.None);
                     TransitionManager transitionMgr = new TransitionManager(helper);
                     TransitionResults results = transitionMgr.handleAll();
                     if (!results.isEmpty()) {
                        AWorkbench.popup(String.format("Transition Error %s", results.toString()));
                     }
                     transitionMgr.getTransaction().execute();
                  } catch (OseeCoreException ex) {
                     OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
                  }
               }
            }
         });
      }
   }
}
