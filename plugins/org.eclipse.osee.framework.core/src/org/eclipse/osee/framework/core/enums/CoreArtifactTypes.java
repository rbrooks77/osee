/*******************************************************************************
 * Copyright (c) 2009 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.enums;

import static java.lang.Integer.MAX_VALUE;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.AccessContextId;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.Annotation;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.ContentUrl;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.Description;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.Name;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.RelationOrder;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.StaticId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;

/**
 * @author Ryan D. Brooks
 */
public final class CoreArtifactTypes {
   private static final List<ArtifactTypeToken> tokens = new ArrayList<>();

   // @formatter:off
   public static final ArtifactTypeToken Artifact = add(1, false, "Artifact").put(
      Annotation, 0, MAX_VALUE).put(
      ContentUrl, 0, 1).put(
      Description, 0, 1).put(
      Name, 1, 1).put(
      StaticId, 0, MAX_VALUE).put(
      RelationOrder, 0, 1).get();

   public static final ArtifactTypeToken AbstractAccessControlled = add(17, true, "Abstract Access Controlled", Artifact).put(
      AccessContextId, 0, MAX_VALUE).get();

   public static final ArtifactTypeToken AbstractHeading = add(805, true, "Abstract Heading", Artifact).get();
   public static final ArtifactTypeToken AbstractImplementationDetails = add(921211884, true, "Abstract Implementation Details", Artifact).get();
   public static final ArtifactTypeToken Requirement = add(21, false, "Requirement", Artifact).get();
   public static final ArtifactTypeToken AbstractSpecRequirement = add(58551193202327573L, false, "Abstract Spec Requirement", Requirement).get();
   public static final ArtifactTypeToken AbstractSoftwareRequirement = add(23, true, "Abstract Software Requirement", AbstractSpecRequirement).get();
   public static final ArtifactTypeToken AbstractSubsystemRequirement = add(797, true, "Abstract Subsystem Requirement", AbstractSpecRequirement).get();
   public static final ArtifactTypeToken AbstractSystemRequirement = add(796, true, "Abstract System Requirement", AbstractSpecRequirement).get();
   public static final ArtifactTypeToken AbstractTestResult = add(38, true, "Abstract Test Result", Artifact).get();
   public static final ArtifactTypeToken GeneralData = add(12, false, "General Data", Artifact).get();
   public static final ArtifactTypeToken AccessControlModel = add(2, false, "Access Control Model", GeneralData).get();
   public static final ArtifactTypeToken BranchView = add(5849078277209560034L, false, "Branch View", Artifact).get();
   public static final ArtifactTypeToken Breaker = add(188458869981236L, false, "Breaker", Artifact).get();
   public static final ArtifactTypeToken CertificationBaselineEvent = add(99, false, "Certification Baseline Event", Artifact).get();
   public static final ArtifactTypeToken CodeUnit = add(58, false, "Code Unit", Artifact).get();
   public static final ArtifactTypeToken Component = add(57, false, "Component", Artifact).get();
   public static final ArtifactTypeToken MSWord = add(16, true, "MS Word", Artifact).get();
   public static final ArtifactTypeToken MSWordTemplate = add(19, false, "MS Word Template", MSWord).get();
   public static final ArtifactTypeToken CustomerRequirementMSWord = add(809, false, "Customer Requirement - MS Word", AbstractSpecRequirement, MSWordTemplate).get();
   public static final ArtifactTypeToken Design = add(346, false, "Design", MSWordTemplate).get();
   public static final ArtifactTypeToken DesignDescriptionMSWord = add(807, false, "Design Description - MS Word", AbstractHeading, MSWordTemplate).get();
   public static final ArtifactTypeToken DirectSoftwareRequirement = add(22, true, "Direct Software Requirement", AbstractSoftwareRequirement).get();
   public static final ArtifactTypeToken DocumentDescriptionMSWord = add(806, false, "Document Description - MS Word", AbstractHeading, MSWordTemplate).get();
   public static final ArtifactTypeToken EnumeratedArtifact = add(4619295485563766003L, false, "Enumerated Artifact", Artifact).get();
   public static final ArtifactTypeToken Feature = add(87, false, "Feature", Artifact).get();
   public static final ArtifactTypeToken Folder = add(11, false, "Folder", Artifact).get();
   public static final ArtifactTypeToken Function = add(34, true, "Function", MSWordTemplate).get();
   public static final ArtifactTypeToken NativeArtifact = add(20, true, "Native Artifact", Artifact).get();
   public static final ArtifactTypeToken GeneralDocument = add(14, false, "General Document", NativeArtifact).get();
   public static final ArtifactTypeToken GitCommit = add(100, false, "Git Commit", Artifact).get();
   public static final ArtifactTypeToken GitRepository = add(97, false, "Git Repository", Artifact).get();
   public static final ArtifactTypeToken GlobalPreferences = add(3, false, "Global Preferences", Artifact).get();
   public static final ArtifactTypeToken GroupArtifact = add(6, false, "Group Artifact", Artifact).get();
   public static final ArtifactTypeToken HTMLArtifact = add(798, false, "HTML Artifact", Artifact).get();
   public static final ArtifactTypeToken HardwareRequirement = add(33, false, "Hardware Requirement", AbstractSpecRequirement, MSWordTemplate).get();
   public static final ArtifactTypeToken HeadingHTML = add(804, false, "Heading - HTML", AbstractHeading, HTMLArtifact).get();
   public static final ArtifactTypeToken HeadingMSWord = add(56, false, "Heading - MS Word", AbstractHeading, MSWordTemplate).get();
   public static final ArtifactTypeToken ImageArtifact = add(800, false, "Image Artifact", Artifact).get();
   public static final ArtifactTypeToken ImplementationDetails = add(26, false, "Implementation Details", AbstractImplementationDetails).get();
   public static final ArtifactTypeToken ImplementationDetailsDataDefinition = add(279578, false, "Implementation Details Data Definition", ImplementationDetails).get();
   public static final ArtifactTypeToken ImplementationDetailsDrawing = add(209690, false, "Implementation Details Drawing", ImplementationDetails).get();
   public static final ArtifactTypeToken ImplementationDetailsFunction = add(139802, false, "Implementation Details Function", ImplementationDetails).get();
   public static final ArtifactTypeToken PlainText = add(784, false, "Plain Text", Artifact).get();
   public static final ArtifactTypeToken SoftwareTestProcedurePlainText = add(564397212436322878L,false, "Software Test Procedure Plain Text").get();
   public static final ArtifactTypeToken ImplementationDetailsPlainText = add(638269899, false, "Implementation Details Plain Text", AbstractImplementationDetails, PlainText).get();
   public static final ArtifactTypeToken ImplementationDetailsProcedure = add(69914, false, "Implementation Details Procedure", ImplementationDetails).get();
   public static final ArtifactTypeToken IndirectSoftwareRequirement = add(25, false, "Indirect Software Requirement", AbstractSoftwareRequirement, MSWordTemplate).get();
   public static final ArtifactTypeToken InterfaceRequirement = add(32, false, "Interface Requirement", AbstractSpecRequirement, MSWordTemplate).get();
   public static final ArtifactTypeToken MSWordWholeDocument = add(18, false, "MS Word Whole Document", MSWord).get();
   public static final ArtifactTypeToken MSWordStyles = add(2578, false, "MS Word Styles", MSWordWholeDocument).get();
   public static final ArtifactTypeToken ModelDiagram = add(98, false, "Model Diagram", Artifact).get();
   public static final ArtifactTypeToken OSEEApp = add(89, false, "OSEE App", Artifact).get();
   public static final ArtifactTypeToken OseeApp = add(89, false, "OSEE App", Artifact).get();
   public static final ArtifactTypeToken OseeTypeDefinition = add(60, false, "Osee Type Definition", Artifact).get();
   public static final ArtifactTypeToken OseeTypeEnum = add(5447805027409642344L, false, "Osee Type Enum", EnumeratedArtifact).get();
   public static final ArtifactTypeToken RendererTemplate = add(9, false, "Renderer Template", MSWordWholeDocument).get();
   public static final ArtifactTypeToken RootArtifact = add(10, false, "Root Artifact", Artifact).get();
   public static final ArtifactTypeToken SafetyAssessment = add(59, false, "Safety Assessment", Artifact).get();
   public static final ArtifactTypeToken SoftwareDesign = add(45, false, "Software Design", Design).get();
   public static final ArtifactTypeToken SoftwareRequirement = add(24, false, "Software Requirement", MSWordTemplate, DirectSoftwareRequirement).get();
   public static final ArtifactTypeToken SoftwareRequirementDataDefinition = add(793, false, "Software Requirement Data Definition", IndirectSoftwareRequirement).get();
   public static final ArtifactTypeToken SoftwareRequirementDrawing = add(29, false, "Software Requirement Drawing", IndirectSoftwareRequirement).get();
   public static final ArtifactTypeToken SoftwareRequirementFunction = add(28, false, "Software Requirement Function", IndirectSoftwareRequirement).get();
   public static final ArtifactTypeToken SoftwareRequirementHtml = add(42, false, "Software Requirement - HTML", AbstractSoftwareRequirement, HTMLArtifact).get();
   public static final ArtifactTypeToken SoftwareRequirementPlainText = add(792, false, "Software Requirement Plain Text", PlainText, DirectSoftwareRequirement).get();
   public static final ArtifactTypeToken SoftwareRequirementProcedure = add(27, false, "Software Requirement Procedure", IndirectSoftwareRequirement).get();
   public static final ArtifactTypeToken UserGroup = add(7, false, "User Group", GroupArtifact, AbstractAccessControlled).get();
   public static final ArtifactTypeToken SubscriptionGroup = add(6753071794573299176L, false, "Subscription Group", UserGroup).get();
   public static final ArtifactTypeToken SubsystemDesign = add(43, false, "Subsystem Design", Design).get();
   public static final ArtifactTypeToken SubsystemFunction = add(36, false, "Subsystem Function", Function, SubsystemDesign).get();
   public static final ArtifactTypeToken SubsystemRequirementHTML = add(795, false, "Subsystem Requirement - HTML", AbstractSubsystemRequirement, HTMLArtifact).get();
   public static final ArtifactTypeToken SubsystemRequirementMSWord = add(31, false, "Subsystem Requirement - MS Word", AbstractSubsystemRequirement, MSWordTemplate).get();
   public static final ArtifactTypeToken SupportDocument = add(13, false, "Support Document", MSWordTemplate).get();
   public static final ArtifactTypeToken Url = add(15, false, "Url", Artifact).get();
   public static final ArtifactTypeToken SupportingContent = add(49, false, "Supporting Content", Url).get();
   public static final ArtifactTypeToken SystemDesign = add(44, false, "System Design", Design).get();
   public static final ArtifactTypeToken SystemFunction = add(35, false, "System Function", Function, SystemDesign).get();
   public static final ArtifactTypeToken SystemRequirementHTML = add(794, false, "System Requirement - HTML", AbstractSystemRequirement, HTMLArtifact).get();
   public static final ArtifactTypeToken SystemRequirementMSWord = add(30, false, "System Requirement - MS Word", AbstractSystemRequirement, MSWordTemplate).get();
   public static final ArtifactTypeToken TestUnit = add(4, true, "Test Unit", Artifact).get();
   public static final ArtifactTypeToken TestCase = add(82, false,"Test Case", TestUnit).get();
   public static final ArtifactTypeToken TestInformationSheet = add(41, false, "Test Information Sheet", MSWordTemplate, TestUnit).get();
   public static final ArtifactTypeToken TestPlanElement = add(37, false, "Test Plan Element", MSWordTemplate).get();
   public static final ArtifactTypeToken TestProcedure = add(46, false, "Test Procedure", TestUnit).get();
   public static final ArtifactTypeToken TestProcedureWML = add(47, false, "Test Procedure WML", MSWordWholeDocument, TestProcedure).get();
   public static final ArtifactTypeToken TestResultNative = add(39, false, "Test Result Native", AbstractTestResult, NativeArtifact).get();
   public static final ArtifactTypeToken TestResultWML = add(40, false, "Test Result WML").get();
   public static final ArtifactTypeToken TestRun = add(85, false, "Test Run", AbstractTestResult, MSWordWholeDocument).get();
   public static final ArtifactTypeToken TestRunDisposition = add(84, false, "Test Run Disposition", Artifact).get();
   public static final ArtifactTypeToken TestSupport = add(83, false, "Test Support", TestUnit).get();
   public static final ArtifactTypeToken UniversalGroup = add(8, false, "Universal Group", GroupArtifact).get();
   public static final ArtifactTypeToken User = add(5, false, "User", Artifact).get();
   public static final ArtifactTypeToken WholeWord = add(18, false, "MS Word Whole Document", MSWord).get();
   public static final ArtifactTypeToken WorkItemDefinition = add(50, true, "Work Item Definition", Artifact).get();
   public static final ArtifactTypeToken WorkFlowDefinition = add(52, false, "Work Flow Definition", WorkItemDefinition).get();
   public static final ArtifactTypeToken WorkPageDefinition = add(51, false, "Work Page Definition", WorkItemDefinition).get();
   public static final ArtifactTypeToken WorkRuleDefinition = add(53, false, "Work Rule Definition", WorkItemDefinition).get();
   public static final ArtifactTypeToken WorkWidgetDefinition = add(54, false, "Work Widget Definition", WorkItemDefinition).get();
   public static final ArtifactTypeToken XViewerGlobalCustomization = add(55, false, "XViewer Global Customization", Artifact).get();
// @formatter:on

   private CoreArtifactTypes() {
      // Constants
   }

   public static void putTokensInMap(Map<Long, ArtifactTypeToken> map) {
      for (ArtifactTypeToken token : tokens) {
         map.put(token.getId(), token);
      }
   }

   private static AttributeMultiplicity add(long id, boolean isAbstract, String name, ArtifactTypeToken... superTypes) {
      return new AttributeMultiplicity(tokens, id, isAbstract, name, superTypes);
   }
}