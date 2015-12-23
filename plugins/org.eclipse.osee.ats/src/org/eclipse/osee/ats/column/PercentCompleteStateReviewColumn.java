/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.column;

import org.eclipse.nebula.widgets.xviewer.IXViewerValueColumn;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.workdef.IStateToken;
import org.eclipse.osee.ats.artifact.WorkflowManager;
import org.eclipse.osee.ats.core.client.action.ActionManager;
import org.eclipse.osee.ats.core.client.review.ReviewManager;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowManager;
import org.eclipse.osee.ats.util.xviewer.column.XViewerAtsColumn;
import org.eclipse.osee.ats.world.WorldXViewerFactory;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.util.LogUtil;
import org.eclipse.swt.SWT;

/**
 * @author Donald G. Dunne
 */
public class PercentCompleteStateReviewColumn extends XViewerAtsColumn implements IXViewerValueColumn {

   public static PercentCompleteStateReviewColumn instance = new PercentCompleteStateReviewColumn();

   public static PercentCompleteStateReviewColumn getInstance() {
      return instance;
   }

   private PercentCompleteStateReviewColumn() {
      super(WorldXViewerFactory.COLUMN_NAMESPACE + ".stateReviewPercentComplete", "State Review Percent Complete", 40,
         SWT.CENTER, false, SortDataType.Percent, false,
         "Percent Complete for the reviews related to the current state.\n\nCalculation: total percent of all reviews related to state / number of reviews related to state");
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public PercentCompleteStateReviewColumn copy() {
      PercentCompleteStateReviewColumn newXCol = new PercentCompleteStateReviewColumn();
      super.copy(this, newXCol);
      return newXCol;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) {
      try {
         if (element instanceof Artifact) {
            return String.valueOf(getPercentCompleteStateReview((Artifact) element));
         }
      } catch (OseeCoreException ex) {
         return LogUtil.getCellExceptionString(ex);
      }
      return "";
   }

   /**
    * Return Percent Complete ONLY on reviews related to stateName. Total Percent / # Reviews
    */
   public static int getPercentCompleteStateReview(Artifact artifact) throws OseeCoreException {
      if (artifact.isOfType(AtsArtifactTypes.Action)) {
         double percent = 0;
         for (TeamWorkFlowArtifact team : ActionManager.getTeams(artifact)) {
            if (!team.isCancelled()) {
               percent += getPercentCompleteStateReview(team);
            }
         }
         if (percent == 0) {
            return 0;
         }
         Double rollPercent = percent / ActionManager.getTeams(artifact).size();
         return rollPercent.intValue();
      }
      if (artifact.isOfType(AtsArtifactTypes.AbstractWorkflowArtifact)) {
         return getPercentCompleteStateReview(artifact, WorkflowManager.getStateManager(artifact).getCurrentState());
      }
      return 0;
   }

   /**
    * Return Percent Complete ONLY on reviews related to stateName. Total Percent / # Reviews
    */
   public static int getPercentCompleteStateReview(Artifact artifact, IStateToken state) throws OseeCoreException {
      if (artifact.isOfType(AtsArtifactTypes.TeamWorkflow)) {
         return ReviewManager.getPercentComplete(TeamWorkFlowManager.cast(artifact), state);
      }
      return 0;
   }

}
