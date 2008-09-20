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
package org.eclipse.osee.framework.ui.skynet.history;

import java.util.logging.Level;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.event.BranchEventType;
import org.eclipse.osee.framework.skynet.core.event.FrameworkTransactionData;
import org.eclipse.osee.framework.skynet.core.event.IBranchEventListener;
import org.eclipse.osee.framework.skynet.core.event.IFrameworkTransactionEventListener;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.Sender;
import org.eclipse.osee.framework.skynet.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.osee.framework.ui.skynet.SkynetContributionItem;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.ats.IActionable;
import org.eclipse.osee.framework.ui.skynet.ats.OseeAts;
import org.eclipse.osee.framework.ui.skynet.menu.ArtifactDiffMenu;
import org.eclipse.osee.framework.ui.skynet.menu.ArtifactPreviewMenu;
import org.eclipse.osee.framework.ui.skynet.util.DbConnectionExceptionComposite;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

/**
 * Displays an artifacts revision history specific to a branch.
 * 
 * @author Jeff C. Phillips
 */
public class RevisionHistoryView extends ViewPart implements IActionable, IFrameworkTransactionEventListener, IBranchEventListener {
   public static final String VIEW_ID = "org.eclipse.osee.framework.ui.skynet.history.RevisionHistoryView";
   private static final String[] columnNames = {"Revision", "Time Stamp", "Author", "Comment"};
   private static final String ARTIFACT_GUID = "GUID";
   private TreeViewer treeViewer;
   private Artifact artifact;

   /**
    * 
    */
   public RevisionHistoryView() {
      super();

      OseeEventManager.addListener(this);
   }

   public static void open(Artifact artifact) {
      IWorkbenchPage page = AWorkbench.getActivePage();
      try {
         RevisionHistoryView revisionHistoryView =
               (RevisionHistoryView) page.showView(RevisionHistoryView.VIEW_ID, artifact.getGuid(),
                     IWorkbenchPage.VIEW_ACTIVATE);
         revisionHistoryView.explore(artifact);
      } catch (Exception ex) {
         OSEELog.logException(SkynetGuiPlugin.class, ex, true);
      }
   }

   @Override
   public void createPartControl(Composite parent) {
      if (!DbConnectionExceptionComposite.dbConnectionIsOk(parent)) return;

      GridData gridData = new GridData();
      gridData.verticalAlignment = GridData.FILL;
      gridData.horizontalAlignment = GridData.FILL;
      gridData.grabExcessVerticalSpace = true;
      gridData.grabExcessHorizontalSpace = true;

      parent.setLayoutData(gridData);

      treeViewer = new TreeViewer(parent, SWT.FULL_SELECTION | SWT.MULTI);
      treeViewer.setContentProvider(new RevisionHistoryContentProvider());
      treeViewer.setLabelProvider(new RevisionHistoryLabelProvider());

      createColumns();
      createTreeExpandListener();
      treeViewer.addDoubleClickListener(new Transaction2ClickListener());

      Menu popupMenu = new Menu(parent);
      ArtifactPreviewMenu.createPreviewMenuItem(popupMenu, treeViewer);
      ArtifactDiffMenu.createDiffMenuItem(popupMenu, treeViewer, "Compare two Artifacts", null);
      treeViewer.getTree().setMenu(popupMenu);
      createActions();

      SkynetContributionItem.addTo(this, true);

      explore(artifact);
   }

   protected void createActions() {
      Action expandAll = new Action("Expand All") {

         @Override
         public void run() {
            treeViewer.expandAll();
         }
      };
      expandAll.setImageDescriptor(SkynetGuiPlugin.getInstance().getImageDescriptor("expandAll.gif"));
      expandAll.setToolTipText("Expand All");

      Action refreshAction = new Action("Refresh") {

         @Override
         public void run() {
            explore(artifact);
         }
      };
      refreshAction.setImageDescriptor(SkynetGuiPlugin.getInstance().getImageDescriptor("refresh.gif"));
      refreshAction.setToolTipText("Refresh");

      IToolBarManager toolbarManager = getViewSite().getActionBars().getToolBarManager();
      toolbarManager.add(expandAll);
      toolbarManager.add(refreshAction);

      OseeAts.addBugToViewToolbar(this, this, SkynetGuiPlugin.getInstance(), VIEW_ID, "Revision History");

   }

   private void createTreeExpandListener() {
      treeViewer.addTreeListener(new ITreeViewerListener() {

         public void treeCollapsed(TreeExpansionEvent event) {
            Display.getCurrent().asyncExec(new Runnable() {
               public void run() {
                  packColumnData();
               }
            });
         }

         public void treeExpanded(TreeExpansionEvent event) {
            Display.getCurrent().asyncExec(new Runnable() {
               public void run() {
                  packColumnData();
               }
            });
         }
      });
   }

   private void createColumns() {
      Tree tree = treeViewer.getTree();

      tree.setHeaderVisible(true);
      TreeColumn column1 = new TreeColumn(tree, SWT.LEFT);
      column1.setWidth(100);
      column1.setText(columnNames[0]);

      TreeColumn column2 = new TreeColumn(tree, SWT.LEFT);
      column2.setWidth(200);
      column2.setText(columnNames[1]);

      TreeColumn column3 = new TreeColumn(tree, SWT.LEFT);
      column3.setWidth(150);
      column3.setText(columnNames[2]);

      TreeColumn column4 = new TreeColumn(tree, SWT.LEFT);
      column4.setWidth(250);
      column4.setText(columnNames[3]);

      setHelpContexts();
   }

   @Override
   public void setFocus() {
      treeViewer.getControl().setFocus();
   }

   /**
    * Explores an artifacts history.
    * 
    * @param artifact
    */
   public void explore(Artifact artifact) {
      if (treeViewer != null && artifact != null) {
         this.artifact = artifact;
         //         Pair<TransactionId, TransactionId> points = transactionIdManager.getStartEndPoint(artifact.getBranch());
         //         historyTable.setInput(new ArtifactChange(ChangeType.OUTGOING, ModificationType.CHANGE, artifact, null, null, points.getKey(), points.getValue(),0));
         treeViewer.setInput(artifact);
         setContentDescription("Artifact: " + artifact.getDescriptiveName());
         packColumnData();
      }
   }

   public String getActionDescription() {
      return "";
   }

   private void packColumnData() {
      TreeColumn[] columns = treeViewer.getTree().getColumns();
      for (TreeColumn column : columns) {
         column.pack();
      }
   }

   private void setHelpContexts() {
      SkynetGuiPlugin.getInstance().setHelp(treeViewer.getControl(), "revision_history_tree_viewer");
   }

   /* (non-Javadoc)
    * @see org.eclipse.ui.part.ViewPart#init(org.eclipse.ui.IViewSite, org.eclipse.ui.IMemento)
    */
   @Override
   public void init(IViewSite site, IMemento memento) throws PartInitException {
      super.init(site, memento);

      try {
         if (memento != null) {
            String guid = memento.getString(ARTIFACT_GUID);
            if (guid != null) {
               artifact = ArtifactQuery.getArtifactFromId(guid, BranchPersistenceManager.getDefaultBranch());
            }
         }
      } catch (ArtifactDoesNotExist ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.INFO, ex);
      } catch (Exception ex) {
         OSEELog.logException(SkynetGuiPlugin.class, ex, true);
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.ui.part.ViewPart#saveState(org.eclipse.ui.IMemento)
    */
   @Override
   public void saveState(IMemento memento) {
      if (artifact != null) {
         memento.putString(ARTIFACT_GUID, artifact.getGuid());
      }
      super.saveState(memento);
   }

   /* (non-Javadoc)
    * @see org.eclipse.ui.part.WorkbenchPart#dispose()
    */
   @Override
   public void dispose() {
      OseeEventManager.removeListener(this);
      super.dispose();
   }

   private void closeView() {
      Displays.ensureInDisplayThread(new Runnable() {
         /* (non-Javadoc)
          * @see java.lang.Runnable#run()
          */
         @Override
         public void run() {
            getViewSite().getPage().hideView(getViewSite().getPage().findViewReference(VIEW_ID, artifact.getGuid()));
         }
      });
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.eventx.IFrameworkTransactionEventListener#handleFrameworkTransactionEvent(org.eclipse.osee.framework.ui.plugin.event.Sender.Source, org.eclipse.osee.framework.skynet.core.eventx.FrameworkTransactionData)
    */
   @Override
   public void handleFrameworkTransactionEvent(Sender sender, FrameworkTransactionData transData) {
      if (transData.isDeleted(artifact)) {
         closeView();
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.eventx.IBranchEventListener#handleBranchEvent(org.eclipse.osee.framework.ui.plugin.event.Sender, org.eclipse.osee.framework.skynet.core.artifact.BranchModType, org.eclipse.osee.framework.skynet.core.artifact.Branch, int)
    */
   @Override
   public void handleBranchEvent(Sender sender, BranchEventType branchModType, int branchId) {
      if (artifact.getBranch().isArchived() || artifact.getBranch().getBranchId() != branchId) {
         closeView();
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.eventx.IBranchEventListener#handleLocalBranchToArtifactCacheUpdateEvent(org.eclipse.osee.framework.ui.plugin.event.Sender)
    */
   @Override
   public void handleLocalBranchToArtifactCacheUpdateEvent(Sender sender) {
   }
}
