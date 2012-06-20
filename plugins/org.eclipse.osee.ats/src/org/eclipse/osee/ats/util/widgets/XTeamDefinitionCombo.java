/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.util.widgets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.eclipse.osee.ats.core.config.TeamDefinitionSorter;
import org.eclipse.osee.ats.core.config.TeamDefinitions;
import org.eclipse.osee.ats.core.model.IAtsTeamDefinition;
import org.eclipse.osee.framework.core.enums.Active;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.XComboViewer;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

public class XTeamDefinitionCombo extends XComboViewer {
   public static final String WIDGET_ID = XTeamDefinitionCombo.class.getSimpleName();
   private Artifact selectedTeamDef = null;

   public XTeamDefinitionCombo() {
      super("Team Definition", SWT.READ_ONLY);
   }

   @Override
   protected void createControls(Composite parent, int horizontalSpan) {
      super.createControls(parent, horizontalSpan);

      Collection<IAtsTeamDefinition> teamDefs = TeamDefinitions.getTeamDefinitions(Active.Active);
      List<IAtsTeamDefinition> sortedTeamDefs = new ArrayList<IAtsTeamDefinition>();
      sortedTeamDefs.addAll(teamDefs);
      Collections.sort(sortedTeamDefs, new TeamDefinitionSorter());
      getComboViewer().setInput(sortedTeamDefs);
      ArrayList<Object> defaultSelection = new ArrayList<Object>();
      defaultSelection.add("--select--");
      setSelected(defaultSelection);
      addXModifiedListener(new XModifiedListener() {

         @Override
         public void widgetModified(XWidget widget) {
            selectedTeamDef = (Artifact) getSelected();
         }
      });
   }

   public Artifact getSelectedTeamDef() {
      return selectedTeamDef;
   }

}
