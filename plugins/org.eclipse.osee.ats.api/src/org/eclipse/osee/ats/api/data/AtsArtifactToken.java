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
package org.eclipse.osee.ats.api.data;

import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import org.eclipse.osee.ats.api.config.tx.AtsActionableItemArtifactToken;
import org.eclipse.osee.ats.api.config.tx.AtsTeamDefinitionArtifactToken;
import org.eclipse.osee.ats.api.config.tx.IAtsActionableItemArtifactToken;
import org.eclipse.osee.ats.api.config.tx.IAtsTeamDefinitionArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;

/**
 * @author Donald G. Dunne
 */
public final class AtsArtifactToken {

   // @formatter:off
   /**
    * This token points to the ATS Access Control artifact that matches the current code base. Production and other code
    * bases may be looking at different artifacts for their access control based on the current OSEE Types and Access
    * grammar.
    */
   public static final ArtifactToken AtsCmAccessControl = ArtifactToken.valueOf(9885573, "ATS CM Access Control", COMMON, CoreArtifactTypes.GeneralData);

   public static final ArtifactToken AtsConfig = ArtifactToken.valueOf(5367053, "ATS Config", COMMON, CoreArtifactTypes.GeneralData);
   public static final ArtifactToken RuleDefinitions = ArtifactToken.valueOf(7880473, "Rule Definitions", COMMON, AtsArtifactTypes.RuleDefinition);
   public static final ArtifactToken Users = ArtifactToken.valueOf(95459, "Users", COMMON, CoreArtifactTypes.Folder);
   public static ArtifactToken HeadingFolder = ArtifactToken.valueOf(114713, "Action Tracking System", COMMON, CoreArtifactTypes.Folder);
   public static IAtsTeamDefinitionArtifactToken TopTeamDefinition = AtsTeamDefinitionArtifactToken.valueOf(56004L, "Teams");
   public static IAtsActionableItemArtifactToken TopActionableItem = AtsActionableItemArtifactToken.valueOf(122894L, "Actionable Items");
   public static ArtifactToken CountryFolder = ArtifactToken.valueOf(7968155, "Countries", COMMON, CoreArtifactTypes.Folder);
   public static ArtifactToken ProgramFolder = ArtifactToken.valueOf(90442279, "Programs", COMMON, CoreArtifactTypes.Folder);
   public static ArtifactToken ConfigsFolder = ArtifactToken.valueOf(5086714, "Configs", COMMON, CoreArtifactTypes.Folder);
   public static ArtifactToken WorkDefinitionsFolder = ArtifactToken.valueOf(284655, "Work Definitions", COMMON, CoreArtifactTypes.Folder);
   public static ArtifactToken WebPrograms = ArtifactToken.valueOf(277592, "Web Programs", COMMON, CoreArtifactTypes.UniversalGroup);
   public static ArtifactToken EVReportPrograms = ArtifactToken.valueOf(8174118, "EV Report Programs", COMMON, CoreArtifactTypes.UniversalGroup);

   // Default Work Definitions

   // Safety Workflow configuration
   public static final ArtifactToken SafetyActionableItem = ArtifactToken.valueOf(7244546, "System Safety", COMMON, AtsArtifactTypes.ActionableItem);
   public static final ArtifactToken SafetyTeamDefinition = ArtifactToken.valueOf(7241589, "System Safety Team", COMMON, AtsArtifactTypes.TeamDefinition);

   // Agile
   public static ArtifactToken TopAgileFolder = ArtifactToken.valueOf(6915493, "Agile", COMMON, CoreArtifactTypes.Folder);
   // @formatter:on

   private AtsArtifactToken() {
      // Constants
   }
}