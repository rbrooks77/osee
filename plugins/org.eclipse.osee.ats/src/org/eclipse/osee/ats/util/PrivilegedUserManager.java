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
package org.eclipse.osee.ats.util;

import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.client.util.AtsUsersClient;
import org.eclipse.osee.ats.core.client.util.AtsUtilCore;
import org.eclipse.osee.ats.core.client.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.core.model.IAtsActionableItem;
import org.eclipse.osee.ats.core.model.IAtsTeamDefinition;
import org.eclipse.osee.ats.core.model.IAtsUser;
import org.eclipse.osee.ats.core.workdef.RuleDefinitionOption;
import org.eclipse.osee.ats.core.workdef.StateDefinition;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;

public class PrivilegedUserManager {

   public static Set<IAtsUser> getPrivilegedUsers(AbstractWorkflowArtifact workflow) throws OseeCoreException {
      Set<IAtsUser> users = new HashSet<IAtsUser>();
      if (workflow.getParentTeamWorkflow() != null) {
         users.addAll(getPrivilegedUsers(workflow.getParentTeamWorkflow()));
      } else {
         for (IAtsActionableItem aia : workflow.getParentTeamWorkflow().getActionableItemsDam().getActionableItems()) {
            addPrivilegedUsersUpTeamDefinitionTree(aia.getTeamDefinitionInherited(), users);
         }
      }
      AbstractWorkflowArtifact parentSma = workflow.getParentAWA();
      if (parentSma != null) {
         users.addAll(parentSma.getStateMgr().getAssignees());
      }
      if (AtsUtilCore.isAtsAdmin()) {
         users.add(AtsUsersClient.getUser());
      }
      return users;
   }

   public static Set<IAtsUser> getPrivilegedUsers(TeamWorkFlowArtifact teamArt) {
      Set<IAtsUser> users = new HashSet<IAtsUser>();
      try {
         addPrivilegedUsersUpTeamDefinitionTree(teamArt.getTeamDefinition(), users);

         StateDefinition stateDefinition = teamArt.getStateDefinition();

         // Add user if allowing privileged edit to all users
         if (!users.contains(AtsUsersClient.getUser()) && (stateDefinition.hasRule(RuleDefinitionOption.AllowPrivilegedEditToAll) || teamArt.getTeamDefinition().hasRule(
            RuleDefinitionOption.AllowPrivilegedEditToAll))) {
            users.add(AtsUsersClient.getUser());
         }

         // Add user if user is team member and rule exists
         boolean workPageToTeamMember = stateDefinition.hasRule(RuleDefinitionOption.AllowPrivilegedEditToTeamMember);
         boolean teamDefToTeamMember =
            teamArt.getTeamDefinition().hasRule(RuleDefinitionOption.AllowPrivilegedEditToTeamMember);
         if (!users.contains(AtsUsersClient.getUser()) && (workPageToTeamMember || teamDefToTeamMember) && //
         teamArt.getTeamDefinition().getMembers().contains(AtsUsersClient.getUser())) {
            users.add(AtsUsersClient.getUser());
         }

         // Add user if team member is originator and rule exists
         boolean workPageToMemberAndOriginator =
            stateDefinition.hasRule(RuleDefinitionOption.AllowPrivilegedEditToTeamMemberAndOriginator);
         boolean teamDefToMemberAndOriginator =
            teamArt.getTeamDefinition().hasRule(RuleDefinitionOption.AllowPrivilegedEditToTeamMemberAndOriginator);
         if (!users.contains(AtsUsersClient.getUser()) && (workPageToMemberAndOriginator || teamDefToMemberAndOriginator) && //
         teamArt.getCreatedBy().equals(AtsUsersClient.getUser()) && teamArt.getTeamDefinition().getMembers().contains(
            AtsUsersClient.getUser())) {
            users.add(AtsUsersClient.getUser());
         }

      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return users;
   }

   protected static void addPrivilegedUsersUpTeamDefinitionTree(IAtsTeamDefinition tda, Set<IAtsUser> users) throws OseeCoreException {
      users.addAll(tda.getLeads());
      users.addAll(tda.getPrivilegedMembers());

      // Walk up tree to get other editors
      if (tda.getParentTeamDef() != null) {
         addPrivilegedUsersUpTeamDefinitionTree(tda.getParentTeamDef(), users);
      }
   }

}
