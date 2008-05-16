/*
 * Created on May 15, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.config.demo.config;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.TaskArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.artifact.VersionArtifact;
import org.eclipse.osee.ats.config.demo.OseeAtsConfigDemoPlugin;
import org.eclipse.osee.ats.config.demo.artifact.DemoCodeTeamWorkflowArtifact;
import org.eclipse.osee.ats.config.demo.config.AtsConfigDemoDatabaseConfig.SawBuilds;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.UniversalGroup;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.relation.RelationSide;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;

/**
 * @author Donald G. Dunne
 */
public class DemoDbGroups {
   public static String TEST_GROUP_NAME = "Test Group";

   public static List<TeamWorkFlowArtifact> createGroups() throws Exception {

      // Create group of all resulting objects
      List<TeamWorkFlowArtifact> codeWorkflows = new ArrayList<TeamWorkFlowArtifact>();
      OSEELog.logInfo(OseeAtsConfigDemoPlugin.class, "Create Groups and add objects", false);
      Artifact groupArt = UniversalGroup.addGroup(TEST_GROUP_NAME, AtsPlugin.getAtsBranch());
      for (DemoCodeTeamWorkflowArtifact codeArt : DemoDbUtil.getSampleCodeWorkflows()) {

         // Add Action to Universal Group
         groupArt.relate(RelationSide.UNIVERSAL_GROUPING__MEMBERS, codeArt.getParentActionArtifact(), true);

         // Add All Team Workflows to Universal Group
         groupArt.relate(RelationSide.UNIVERSAL_GROUPING__MEMBERS,
               codeArt.getParentActionArtifact().getTeamWorkFlowArtifacts(), true);

         // Relate codeArt to SAW_Bld_2
         codeArt.relate(RelationSide.TeamWorkflowTargetedForVersion_Version, ArtifactQuery.getArtifactFromTypeAndName(
               VersionArtifact.ARTIFACT_NAME, SawBuilds.SAW_Bld_2.name(), AtsPlugin.getAtsBranch()));

         codeArt.persist(true);

      }

      // Add all Tasks to Group
      groupArt.relate(RelationSide.UNIVERSAL_GROUPING__MEMBERS, ArtifactQuery.getArtifactsFromType(
            TaskArtifact.ARTIFACT_NAME, AtsPlugin.getAtsBranch()), true);

      return codeWorkflows;
   }
}
