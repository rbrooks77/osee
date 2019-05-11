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
package org.eclipse.osee.framework.core.enums;

import java.io.InputStream;
import java.util.Date;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;

/**
 * @author Roberto E. Escobar
 */
public final class CoreAttributeTypes {

   // @formatter:off
   public static final AttributeTypeToken<String> Afha = AttributeTypeToken.valueOf(1152921504606847139L, "AFHA");
   public static final AttributeTypeToken<String> AccessContextId = AttributeTypeToken.valueOf(1152921504606847102L, "Access Context Id");
   public static final AttributeTypeToken<Boolean> Active = AttributeTypeToken.valueOf(1152921504606847065L, "Active");
   public static final AttributeTypeToken<String> Annotation = AttributeTypeToken.valueOf(1152921504606847094L, "Annotation");
   public static final AttributeTypeToken<String> ArtifactReference = AttributeTypeToken.valueOf(1153126013769613560L, "Artifact Reference");
   public static final AttributeTypeToken<String> PlainTextContent = AttributeTypeToken.valueOf(1152921504606847866L, "Plain Text Content");
   public static final AttributeTypeToken<ArtifactId> BaselinedBy = AttributeTypeToken.valueOf(1152921504606847247L, "Baselined By");
   public static final AttributeTypeToken<Date> BaselinedTimestamp = AttributeTypeToken.valueOf(1152921504606847244L, "Baselined Timestamp");
   public static final AttributeTypeToken<String> BranchReference = AttributeTypeToken.valueOf(1153126013769613563L, "Branch Reference");
   public static final AttributeTypeToken<String> Category = AttributeTypeToken.valueOf(1152921504606847121L, "Category");
   public static final AttributeTypeToken<String> CircuitBreakerId = AttributeTypeToken.valueOf(188458869981238L, "Circuit Breaker ID");
   public static final AttributeTypeToken<String> City = AttributeTypeToken.valueOf(1152921504606847068L, "City");
   public static final AttributeTypeToken<String> CommonNalRequirement = AttributeTypeToken.valueOf(1152921504606847105L, "Common NAL Requirement");
   public static final AttributeTypeToken<String> Company = AttributeTypeToken.valueOf(1152921504606847066L, "Company");
   public static final AttributeTypeToken<String> CompanyTitle = AttributeTypeToken.valueOf(1152921504606847067L, "Company Title");
   public static final AttributeTypeToken<String> Component = AttributeTypeToken.valueOf(1152921504606847125L, "Component");
   public static final AttributeTypeToken<String> ContentUrl = AttributeTypeToken.valueOf(1152921504606847100L, "Content URL");
   public static final AttributeTypeToken<String> Country = AttributeTypeToken.valueOf(1152921504606847072L, "Country");
   public static final AttributeTypeToken<String> CrewInterfaceRequirement = AttributeTypeToken.valueOf(1152921504606847106L, "Crew Interface Requirement");
   public static final AttributeTypeToken<String> Csci = AttributeTypeToken.valueOf(1152921504606847136L, "CSCI");
   public static final AttributeTypeToken<String> DataRightsClassification = AttributeTypeToken.valueOf(1152921504606847317L, "Data Rights Classification");
   public static final AttributeTypeToken<String> DataRightsBasis = AttributeTypeToken.valueOf(72057594037928276L, "Data Rights Basis");
   public static final AttributeTypeToken<String> SubjectMatterExpert = AttributeTypeToken.valueOf(72057594037928275L, "Subject Matter Expert");
   public static final AttributeTypeToken<String> DefaultMailServer = AttributeTypeToken.valueOf(1152921504606847063L, "osee.config.Default Mail Server");
   public static final AttributeTypeToken<String> DefaultGroup = AttributeTypeToken.valueOf(1152921504606847086L, "Default Group");
   public static final AttributeTypeToken<String> DefaultTrackingBranch = AttributeTypeToken.valueOf(1152921504606847709L, "Default Tracking Branch");
   public static final AttributeTypeToken<String> Description = AttributeTypeToken.valueOf(1152921504606847090L, "Description");
   public static final AttributeTypeToken<String> Dictionary = AttributeTypeToken.valueOf(1152921504606847083L, "Dictionary");
   public static final AttributeTypeToken<String> DoorsID = AttributeTypeToken.valueOf(8243262488122393232L, "Doors ID");
   public static final AttributeTypeToken<String> DoorsModID = AttributeTypeToken.valueOf(5326122488147393161L, "Doors Mod ID");
   public static final AttributeTypeToken<String> DoorsHierarchy = AttributeTypeToken.valueOf(1873562488122323009L, "Doors Hierarchy");
   public static final AttributeTypeToken<String> Effectivity = AttributeTypeToken.valueOf(1152921504606847108L, "Effectivity");
   public static final AttributeTypeToken<String> Email = AttributeTypeToken.valueOf(1152921504606847082L, "Email");
   public static final AttributeTypeToken<String> ExcludePath = AttributeTypeToken.valueOf(1152921504606847708L, "Exclude Path");
   public static final AttributeTypeToken<String> Extension = AttributeTypeToken.valueOf(1152921504606847064L, "Extension");
   public static final AttributeTypeToken<String> FavoriteBranch = AttributeTypeToken.valueOf(1152921504606847074L, "Favorite Branch");
   public static final AttributeTypeToken<String> FaxPhone = AttributeTypeToken.valueOf(1152921504606847081L, "Fax Phone");
   public static final AttributeTypeToken<Boolean> FeatureMultivalued = AttributeTypeToken.valueOf(3641431177461038717L, "Feature Multivalued");
   public static final AttributeTypeToken<String> FeatureValueType = AttributeTypeToken.valueOf(31669009535111027L, "Feature Value Type");
   public static final AttributeTypeToken<String> FileSystemPath = AttributeTypeToken.valueOf(1152921504606847707L, "File System Path");
   public static final AttributeTypeToken<String> FunctionalCategory = AttributeTypeToken.valueOf(1152921504606847871L, "Functional Category");
   public static final AttributeTypeToken<String> FunctionalDAL = AttributeTypeToken.valueOf(8007959514939954596L, "Functional Development Assurance Level");
   public static final AttributeTypeToken<String> FunctionalDALRationale = AttributeTypeToken.valueOf(926274413268034710L, "Functional Development Assurance Level Rationale");
   public static final AttributeTypeToken<String> GeneralStringData = AttributeTypeToken.valueOf(1152921504606847096L, "General String Data");
   public static final AttributeTypeToken<String> GfeCfe = AttributeTypeToken.valueOf(1152921504606847144L, "GFE / CFE");
   public static final AttributeTypeToken<String> GitChangeId = AttributeTypeToken.valueOf(1152921504606847702L, "Git Change-Id");
   public static final AttributeTypeToken<String> GitCommitSHA = AttributeTypeToken.valueOf(1152921504606847703L, "Git Commit SHA");
   public static final AttributeTypeToken<Date> GitCommitAuthorDate = AttributeTypeToken.valueOf(1152921504606847704L, "Git Commit Author Date");
   public static final AttributeTypeToken<String> GitCommitMessage = AttributeTypeToken.valueOf(1152921504606847705L, "Git Commit Message");
   public static final AttributeTypeToken<String> GitRepositoryReference = AttributeTypeToken.valueOf(1152921504606847706L, "Git Repository Reference");
   public static final AttributeTypeToken<String> GraphitiDiagram = AttributeTypeToken.valueOf(1152921504606847319L, "Graphiti Diagram");
   public static final AttributeTypeToken<String> Hazard = AttributeTypeToken.valueOf(1152921504606847138L, "Hazard");
   public static final AttributeTypeToken<String> HazardSeverity = AttributeTypeToken.valueOf(1152921504606847141L, "Hazard Severity");
   public static final AttributeTypeToken<String> HTMLContent = AttributeTypeToken.valueOf(1152921504606847869L, "HTML Content");
   public static final AttributeTypeToken<InputStream> ImageContent = AttributeTypeToken.valueOf(1152921504606847868L, "Image Content");
   public static final AttributeTypeToken<String> IdValue = AttributeTypeToken.valueOf(72057896045641815L, "ID Value");
   public static final AttributeTypeToken<String> ItemDAL = AttributeTypeToken.valueOf(2612838829556295211L, "Item Development Assurance Level");
   public static final AttributeTypeToken<String> ItemDALRationale = AttributeTypeToken.valueOf(2517743638468399405L, "Item Development Assurance Level Rationale");
   public static final AttributeTypeToken<String> LegacyDAL = AttributeTypeToken.valueOf(1152921504606847120L, "Legacy Development Assurance Level");
   public static final AttributeTypeToken<String> LegacyId = AttributeTypeToken.valueOf(1152921504606847107L, "Legacy Id");
   public static final AttributeTypeToken<String> MobilePhone = AttributeTypeToken.valueOf(1152921504606847080L, "Mobile Phone");
   public static final AttributeTypeToken<String> Name = AttributeTypeToken.valueOf(1152921504606847088L, "Name");
   public static final AttributeTypeToken<String> NativeContent = AttributeTypeToken.valueOf(1152921504606847097L, "Native Content");
   public static final AttributeTypeToken<String> Notes = AttributeTypeToken.valueOf(1152921504606847085L, "Notes");
   public static final AttributeTypeToken<String> OseeAppDefinition = AttributeTypeToken.valueOf(1152921504606847380L, "Osee App Definition");
   public static final AttributeTypeToken<String> PageType = AttributeTypeToken.valueOf(1152921504606847091L, "Page Type");
   public static final AttributeTypeToken<String> ParagraphNumber = AttributeTypeToken.valueOf(1152921504606847101L, "Paragraph Number");
   public static final AttributeTypeToken<String> Partition = AttributeTypeToken.valueOf(1152921504606847111L, "Partition");
   public static final AttributeTypeToken<String> Phone = AttributeTypeToken.valueOf(1152921504606847079L, "Phone");
   public static final AttributeTypeToken<String> PublishInline = AttributeTypeToken.valueOf(1152921504606847122L, "PublishInline");
   public static final AttributeTypeToken<String> QualificationMethod = AttributeTypeToken.valueOf(1152921504606847113L, "Qualification Method");
   public static final AttributeTypeToken<String> RelationOrder = AttributeTypeToken.valueOf(1152921504606847089L, "Relation Order");
   public static final AttributeTypeToken<String> RendererOptions = AttributeTypeToken.valueOf(904, "Renderer Options");
   public static final AttributeTypeToken<String> RepositoryUrl = AttributeTypeToken.valueOf(1152921504606847700L, "Repository URL");
   public static final AttributeTypeToken<Integer> ReviewId = AttributeTypeToken.valueOf(1152921504606847245L, "Review Id");
   public static final AttributeTypeToken<String> ReviewStoryId = AttributeTypeToken.valueOf(1152921504606847246L, "Review Story Id");
   public static final AttributeTypeToken<String> RequireConfirmation = AttributeTypeToken.valueOf(188458869981239L, "Require Confirmation");
   public static final AttributeTypeToken<String> Sfha = AttributeTypeToken.valueOf(1152921504606847140L, "SFHA");
   public static final AttributeTypeToken<String> SafetySeverity = AttributeTypeToken.valueOf(846763346271224762L, "Safety Severity");
   public static final AttributeTypeToken<String> SeverityCategory = AttributeTypeToken.valueOf(1152921504606847114L, "Severity Category");
   public static final AttributeTypeToken<String> SoftwareControlCategory = AttributeTypeToken.valueOf(1958401980089733639L, "Software Control Category");
   public static final AttributeTypeToken<String> SoftwareControlCategoryRationale = AttributeTypeToken.valueOf(750929222178534710L, "Software Control Category Rationale");
   public static final AttributeTypeToken<String> SoftwareSafetyImpact = AttributeTypeToken.valueOf(8318805403746485981L, "Software Safety Impact");
   public static final AttributeTypeToken<String> SafetyImpact = AttributeTypeToken.valueOf(1684721504606847095L, "Safety Impact");
   public static final AttributeTypeToken<String> State = AttributeTypeToken.valueOf(1152921504606847070L, "State");
   public static final AttributeTypeToken<String> StaticId = AttributeTypeToken.valueOf(1152921504606847095L, "Static Id");
   public static final AttributeTypeToken<String> Street = AttributeTypeToken.valueOf(1152921504606847069L, "Street");
   public static final AttributeTypeToken<String> Subsystem = AttributeTypeToken.valueOf(1152921504606847112L, "Subsystem");
   public static final AttributeTypeToken<String> TechnicalPerformanceParameter =AttributeTypeToken.valueOf(1152921504606847123L, "Techinical Performance Parameter");
   public static final AttributeTypeToken<String> TemplateMatchCriteria = AttributeTypeToken.valueOf(1152921504606847087L, "Template Match Criteria");
   public static final AttributeTypeToken<String> TestProcedureStatus = AttributeTypeToken.valueOf(1152921504606847075L, "Test Procedure Status");
   public static final AttributeTypeToken<String> TestScriptGuid = AttributeTypeToken.valueOf(1152921504606847301L, "Test Script GUID");
   public static final AttributeTypeToken<String> UserId = AttributeTypeToken.valueOf(1152921504606847073L, "User Id");
   public static final AttributeTypeToken<String> UriGeneralStringData = AttributeTypeToken.valueOf(1152921504606847381L, "Uri General String Data");
   public static final AttributeTypeToken<String> UserArtifactId = AttributeTypeToken.valueOf(1152921504606847701L, "User Artifact Id");
   public static final AttributeTypeToken<String> UserSettings = AttributeTypeToken.valueOf(1152921504606847076L, "User Settings");
   public static final AttributeTypeToken<String> VerificationEvent = AttributeTypeToken.valueOf(1152921504606847124L, "Verification Event");
   public static final AttributeTypeToken<String> VerificationLevel = AttributeTypeToken.valueOf(1152921504606847115L, "Verification Level");
   public static final AttributeTypeToken<String> VerificationCriteria = AttributeTypeToken.valueOf(1152921504606847117L, "Verification Acceptance Criteria");
   public static final AttributeTypeToken<String> Website = AttributeTypeToken.valueOf(1152921504606847084L, "Website");
   public static final AttributeTypeToken<String> WebPreferences = AttributeTypeToken.valueOf(1152921504606847386L, "Web Preferences");
   public static final AttributeTypeToken<String> WholeWordContent = AttributeTypeToken.valueOf(1152921504606847099L, "Whole Word Content");
   public static final AttributeTypeToken<String> WordOleData = AttributeTypeToken.valueOf(1152921504606847092L, "Word Ole Data");
   public static final AttributeTypeToken<String> WordTemplateContent = AttributeTypeToken.valueOf(1152921504606847098L, "Word Template Content");
   public static final AttributeTypeToken<String> WorkData = AttributeTypeToken.valueOf(1152921504606847126L, "osee.wi.Work Data");
   public static final AttributeTypeToken<String> WorkTransition = AttributeTypeToken.valueOf(1152921504606847133L, "osee.wi.Transition");
   public static final AttributeTypeToken<String> XViewerCustomization = AttributeTypeToken.valueOf(1152921504606847077L, "XViewer Customization");
   public static final AttributeTypeToken<String> XViewerDefaults = AttributeTypeToken.valueOf(1152921504606847078L, "XViewer Defaults");
   public static final AttributeTypeToken<String> Zip = AttributeTypeToken.valueOf(1152921504606847071L, "Zip");
   public static final AttributeTypeToken<String> DefaultValue = AttributeTypeToken.valueOf(2221435335730390044L, "Default Value");
   public static final AttributeTypeToken<String> Value = AttributeTypeToken.valueOf(861995499338466438L, "Value");

   // @formatter:on

   private CoreAttributeTypes() {
      // Constants
   }
}
