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

package org.eclipse.osee.ats.util.widgets.dialog;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.osee.ats.core.config.ActionableItems;
import org.eclipse.osee.ats.core.model.IAtsActionableItem;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.util.AtsObjectLabelProvider;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.util.ArtifactNameSorter;
import org.eclipse.osee.framework.ui.skynet.util.filteredTree.OSEECheckedFilteredTreeDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.dialogs.PatternFilter;

/**
 * @author Donald G. Dunne
 */
public class AICheckTreeDialog extends OSEECheckedFilteredTreeDialog {

   private static PatternFilter patternFilter = new PatternFilter();
   private final Active active;
   private Collection<IAtsActionableItem> initialAias;

   public AICheckTreeDialog(String title, String message, Active active) {
      super(title, message, patternFilter, new AITreeContentProvider(active), new AtsObjectLabelProvider(),
         new ArtifactNameSorter());
      this.active = active;
   }

   public Collection<IAtsActionableItem> getChecked() {
      if (super.getTreeViewer() == null) {
         return Collections.emptyList();
      }
      Set<IAtsActionableItem> checked = new HashSet<IAtsActionableItem>();
      for (Object obj : super.getTreeViewer().getChecked()) {
         checked.add((IAtsActionableItem) obj);
      }
      return checked;
   }

   @Override
   protected Control createDialogArea(Composite container) {
      Control comp = super.createDialogArea(container);
      try {
         getTreeViewer().getViewer().setInput(ActionableItems.getTopLevelActionableItems(active));
         getTreeViewer().getViewer().addSelectionChangedListener(new ISelectionChangedListener() {
            @Override
            public void selectionChanged(SelectionChangedEvent event) {
               try {
                  for (IAtsActionableItem aia : getChecked()) {
                     if (!aia.isActionable()) {
                        AWorkbench.popup("ERROR", ActionableItems.getNotActionableItemError(aia));
                     }
                  }
               } catch (Exception ex) {
                  OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
               }
            }
         });
         if (getInitialAias() != null) {
            getTreeViewer().setInitalChecked(getInitialAias());
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return comp;
   }

   @Override
   protected Result isComplete() {
      try {
         for (IAtsActionableItem aia : getChecked()) {
            if (!aia.isActionable()) {
               return Result.FalseResult;
            }
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return super.isComplete();
   }

   /**
    * @return the initialAias
    */
   public Collection<IAtsActionableItem> getInitialAias() {
      return initialAias;
   }

   /**
    * @param initialAias the initialAias to set
    */
   public void setInitialAias(Collection<IAtsActionableItem> initialAias) {
      this.initialAias = initialAias;
   }

}
