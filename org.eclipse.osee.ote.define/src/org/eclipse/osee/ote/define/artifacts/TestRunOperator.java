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

package org.eclipse.osee.ote.define.artifacts;

import java.io.InputStream;
import java.net.URI;
import java.util.Date;
import java.util.List;
import org.eclipse.osee.framework.core.exception.AttributeDoesNotExist;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeWrappedException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.attribute.providers.IAttributeDataProvider;
import org.eclipse.osee.ote.define.AUTOGEN.OTE_SKYNET_ARTIFACTS;
import org.eclipse.osee.ote.define.AUTOGEN.OTE_SKYNET_ATTRIBUTES;

/**
 * @author Roberto E. Escobar
 */
public class TestRunOperator {
   private static final OteArtifactFetcher<Artifact> TEST_RUN_ARTIFACT_FETCHER =
         new OteArtifactFetcher<Artifact>(OTE_SKYNET_ARTIFACTS.TEST_RUN);

   private static final OteArtifactFetcher<Artifact> TEST_SCRIPT_ARTIFACT_FETCHER =
         new OteArtifactFetcher<Artifact>(OTE_SKYNET_ARTIFACTS.TEST_SCRIPT);

   private Artifact artifact;

   public TestRunOperator(Artifact artifact) throws OseeArgumentException {
      checkForNull(artifact);
      checkForType(artifact);
      this.artifact = artifact;
   }

   private void checkForNull(Artifact artifact) throws OseeArgumentException {
      if (artifact == null) {
         throw new OseeArgumentException("Artifact was null.");
      }
   }

   private void checkForType(Artifact artifact) throws OseeArgumentException {
      if (!artifact.isOfType(OTE_SKYNET_ARTIFACTS.TEST_RUN.getName())) {
         throw new OseeArgumentException(String.format("Unable to operate on type [%s]. Only [%s] allowed.",
               artifact.getArtifactTypeName(), OTE_SKYNET_ARTIFACTS.TEST_RUN.getName()));
      }
   }

   public static TestRunOperator getNewArtifactWithOperator(Branch branch) throws OseeCoreException {
      return new TestRunOperator(TEST_RUN_ARTIFACT_FETCHER.getNewArtifact(branch));
   }

   public static OteArtifactFetcher<Artifact> getTestRunFetcher() {
      return TEST_RUN_ARTIFACT_FETCHER;
   }

   public static OteArtifactFetcher<Artifact> getTestScriptFetcher() {
      return TEST_SCRIPT_ARTIFACT_FETCHER;
   }

   public Artifact getTestRunArtifact() {
      return artifact;
   }

   public String getDescriptiveName() {
      return artifact.getName();
   }

   public String getScriptRevision() throws OseeCoreException {
      return artifact.getSoleAttributeValue(OTE_SKYNET_ATTRIBUTES.REVISION.getName(), "");
   }

   public String getScriptUrl() throws OseeCoreException {
      return artifact.getSoleAttributeValue(OTE_SKYNET_ATTRIBUTES.TEST_SCRIPT_URL.getName(), "");
   }

   public void setLastDateUploaded(Date value) throws OseeCoreException {
      artifact.setSoleAttributeValue(OTE_SKYNET_ATTRIBUTES.LAST_DATE_UPLOADED.getName(), value);
   }

   public Date getLastDateUploaded() throws OseeCoreException {
      return artifact.getSoleAttributeValue(OTE_SKYNET_ATTRIBUTES.LAST_DATE_UPLOADED.getName(), null);
   }

   public void setChecksum(String value) throws OseeCoreException {
      artifact.setSoleAttributeValue(OTE_SKYNET_ATTRIBUTES.CHECKSUM.getName(), value);
   }

   public String getChecksum() throws OseeCoreException {
      return artifact.getSoleAttributeValue(OTE_SKYNET_ATTRIBUTES.CHECKSUM.getName(), "");
   }

   public String getOutfileExtension() throws OseeCoreException {
      return artifact.getSoleAttributeValue(OTE_SKYNET_ATTRIBUTES.EXTENSION.getName(), "");
   }

   public void setOutfileExtension(String outfile) throws OseeCoreException {
      artifact.setSoleAttributeValue(OTE_SKYNET_ATTRIBUTES.EXTENSION.getName(), outfile);
   }

   public boolean isFromLocalWorkspace() throws OseeCoreException, AttributeDoesNotExist {
      return getLastDateUploaded() == null;
   }

   public void setLocalOutfileURI(String uri) throws OseeCoreException, AttributeDoesNotExist {
      IAttributeDataProvider provider = getOutfileAttribute().getAttributeDataProvider();
      if (provider instanceof MappedAttributeDataProvider) {
         ((MappedAttributeDataProvider) provider).setLocalUri(uri);
      }
   }

   public String getOutfileUrl() throws OseeCoreException {
      return artifact.getSoleAttributeValue(OTE_SKYNET_ATTRIBUTES.OUTFILE_URL.getName());
   }

   public String getOutfileContents() throws OseeCoreException {
      try {
         return Lib.inputStreamToString(new URI(getOutfileUrl()).toURL().openStream());
      } catch (Exception ex) {
         throw new OseeWrappedException(ex);
      }
   }

   public Attribute<InputStream> getOutfileAttribute() throws AttributeDoesNotExist, OseeCoreException {
      List<Attribute<InputStream>> attributes = artifact.getAttributes(OTE_SKYNET_ATTRIBUTES.OUTFILE_URL.getName());
      return attributes != null && attributes.size() > 0 ? attributes.get(0) : null;
   }

   public boolean isScriptRevisionValid() {
      boolean toReturn = false;
      try {
         URI url = new URI(getScriptUrl());
         if (url != null) {
            String revision = getScriptRevision();
            if (Strings.isValid(revision)) {
               toReturn = true;
            }
         }
      } catch (Exception ex) {
      }
      return toReturn;
   }

   public boolean hasNotBeenCommitted() {
      Artifact fetched = null;
      try {
         fetched =
               getTestRunFetcher().searchForUniqueArtifactMatching(OTE_SKYNET_ATTRIBUTES.CHECKSUM.getName(),
                     getChecksum(), artifact.getBranch());
      } catch (Exception ex) {
      }
      return fetched == null;
   }

   public boolean isCommitAllowed() {
      return isScriptRevisionValid() && hasNotBeenCommitted();
   }

   public boolean hasValidArtifact() {
      return artifact != null && artifact.isDeleted() != true;
   }

   public void createTestScriptSoftLink() throws OseeCoreException {
      Artifact testScript =
            getTestScriptFetcher().searchForUniqueArtifactMatching("Name", artifact.getName(),
                  artifact.getBranch());
      if (testScript != null) {
         artifact.setSoleAttributeValue(OTE_SKYNET_ATTRIBUTES.TEST_SCRIPT_GUID.getName(), testScript.getGuid());
      }
   }

   public String getPartition() {
      String name = artifact.getName();
      String[] data = name.split("\\.");
      if (data.length - 3 > 0) {
         name = data[data.length - 3];
      }
      return name;
   }

   public String getSubsystem() {
      String name = artifact.getName();
      String[] data = name.split("\\.");
      if (data.length - 2 > 0) {
         name = data[data.length - 2];
      }
      return name;
   }

   public int getTestPointsPassed() throws OseeCoreException {
      return artifact.getSoleAttributeValue(OTE_SKYNET_ATTRIBUTES.PASSED.getName());
   }

   public int getTestPointsFailed() throws OseeCoreException {
      return artifact.getSoleAttributeValue(OTE_SKYNET_ATTRIBUTES.FAILED.getName());
   }

   public int getTotalTestPoints() throws OseeCoreException {
      return artifact.getSoleAttributeValue(OTE_SKYNET_ATTRIBUTES.TOTAL_TEST_POINTS.getName());
   }

   public Date getEndDate() throws OseeCoreException {
      return processDateAttribute(OTE_SKYNET_ATTRIBUTES.END_DATE);
   }

   public Date getLastModifiedDate() throws OseeCoreException {
      return processDateAttribute(OTE_SKYNET_ATTRIBUTES.LAST_MODIFIED_DATE);
   }

   public Date getTestStartDate() throws OseeCoreException {
      return processDateAttribute(OTE_SKYNET_ATTRIBUTES.START_DATE);
   }

   private Date processDateAttribute(OTE_SKYNET_ATTRIBUTES attributeType) throws OseeCoreException {
      Date date = artifact.getSoleAttributeValue(attributeType.getName(), null);
      if (date == null) {
         date = new Date(0);
      }
      return date;
   }

   public boolean wasAborted() {
      boolean toReturn = true;
      try {
         toReturn = artifact.getSoleAttributeValue(OTE_SKYNET_ATTRIBUTES.SCRIPT_ABORTED.getName(), false);
      } catch (Exception ex) {
      }
      return toReturn;
   }

   public String getTestResultStatus() throws OseeCoreException {
      String result = "FAILED";
      if (wasAborted() != true) {
         int total = getTotalTestPoints();
         if (total > 0) {
            if (getTestPointsFailed() <= 0) {
               int passed = getTestPointsPassed();
               if (passed == total) {
                  result = "PASSED";
               }
            }
         }
      }
      return result;
   }

   public boolean isBatchModeAllowed() {
      boolean toReturn = false;
      try {
         toReturn = artifact.getSoleAttributeValue(OTE_SKYNET_ATTRIBUTES.IS_BATCH_MODE_ALLOWED.getName(), false);
      } catch (Exception ex) {
      }
      return toReturn;
   }

   public String getOseeVersion() throws OseeCoreException {
      return artifact.getSoleAttributeValue(OTE_SKYNET_ATTRIBUTES.OSEE_VERSION.getName(), "").trim();
   }

   public String getOseeServerTitle() throws OseeCoreException {
      return artifact.getSoleAttributeValue(OTE_SKYNET_ATTRIBUTES.OSEE_SERVER_TITLE.getName(), "").trim();
   }

   public String getOseeServerVersion() throws OseeCoreException {
      return artifact.getSoleAttributeValue(OTE_SKYNET_ATTRIBUTES.OSEE_SERVER_JAR_VERSION.getName(), "").trim();
   }

   public String getProcessorId() throws OseeCoreException {
      return artifact.getSoleAttributeValue(OTE_SKYNET_ATTRIBUTES.PROCESSOR_ID.getName(), "");
   }

   public String getRunDuration() throws OseeCoreException {
      return artifact.getSoleAttributeValue(OTE_SKYNET_ATTRIBUTES.ELAPSED_DATE.getName(), "");
   }

   public String getQualificationLevel() throws OseeCoreException {
      return artifact.getSoleAttributeValue(OTE_SKYNET_ATTRIBUTES.QUALIFICATION_LEVEL.getName(), "");
   }

   public String getBuildId() throws OseeCoreException {
      return artifact.getSoleAttributeValue(OTE_SKYNET_ATTRIBUTES.BUILD_ID.getName(), "");
   }

   public String getRanOnOperatingSystem() throws OseeCoreException {
      return artifact.getSoleAttributeValue(OTE_SKYNET_ATTRIBUTES.OS_NAME.getName(), "");
   }

   public String getLastAuthor() throws OseeCoreException {
      return artifact.getSoleAttributeValue(OTE_SKYNET_ATTRIBUTES.LAST_AUTHOR.getName(), null);
   }

   public String getScriptSimpleName() {
      String rawName = getDescriptiveName();
      String[] qualifiers = rawName.split("\\.");
      return qualifiers[qualifiers.length - 1];
   }
}