/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.impl.internal.workitem;

import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.impl.internal.AtsServerService;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.orcs.data.ArtifactReadable;

public class TeamWorkflow extends WorkItem implements IAtsTeamWorkflow {

   public TeamWorkflow(ArtifactReadable artifact) {
      super(artifact);
   }

   @Override
   public Set<IAtsActionableItem> getActionableItems() throws OseeCoreException {
      Set<IAtsActionableItem> ais = new HashSet<IAtsActionableItem>();
      for (Object aiGuidObj : artifact.getAttributeValues(AtsAttributeTypes.ActionableItem)) {
         String aiGuid = (String) aiGuidObj;
         ArtifactReadable aiArt = AtsServerService.get().getArtifactByGuid(aiGuid);
         IAtsActionableItem ai = AtsServerService.get().getConfigItemFactory().getActionableItem(aiArt);
         ais.add(ai);
      }
      return ais;
   }

   @Override
   public IAtsTeamDefinition getTeamDefinition() throws OseeCoreException {
      String teamDefGuid = artifact.getSoleAttributeValue(AtsAttributeTypes.TeamDefinition);
      ArtifactReadable teamDefArt = AtsServerService.get().getArtifactByGuid(teamDefGuid);
      IAtsTeamDefinition teamDef = AtsServerService.get().getConfigItemFactory().getTeamDef(teamDefArt);
      return teamDef;
   }

}
