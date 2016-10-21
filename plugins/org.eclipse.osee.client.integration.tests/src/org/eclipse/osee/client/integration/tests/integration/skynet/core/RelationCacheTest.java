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
package org.eclipse.osee.client.integration.tests.integration.skynet.core;

import static org.eclipse.osee.client.demo.DemoChoice.OSEE_CLIENT_DEMO;
import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.client.integration.tests.integration.skynet.core.utils.TestUtil;
import org.eclipse.osee.client.test.framework.OseeClientIntegrationRule;
import org.eclipse.osee.client.test.framework.OseeLogMonitor;
import org.eclipse.osee.client.test.framework.OseeLogMonitorRule;
import org.eclipse.osee.framework.core.data.ApplicabilityId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.enums.RelationTypeMultiplicity;
import org.eclipse.osee.framework.core.model.type.RelationType;
import org.eclipse.osee.framework.jdk.core.util.Compare;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.relation.RelationCache;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * Test Case for {@link RelationCache}
 *
 * @author Roberto E. Escobar
 */
public class RelationCacheTest {

   @Rule
   public OseeClientIntegrationRule integration = new OseeClientIntegrationRule(OSEE_CLIENT_DEMO);

   @Rule
   public OseeLogMonitorRule monitorRule = new OseeLogMonitorRule();

   private final BranchId branch1 = BranchId.valueOf(100L);
   private final BranchId branch2 = BranchId.valueOf(200L);

   private final Artifact artifact1 = new Artifact(branch1);
   private final Artifact artifact2 = new Artifact(branch2);
   private final ArtifactToken art22 = ArtifactToken.valueOf(22, branch1);
   private final ArtifactToken art44 = ArtifactToken.valueOf(44, branch1);
   private final ArtifactToken art77 = ArtifactToken.valueOf(77, branch2);

   private List<RelationLink> sourceLinksRelType1;
   private List<RelationLink> sourceLinksRelType2;
   private List<RelationLink> sourceLinksRelType1AndType2;

   private RelationType relType1;
   private RelationType relType2;

   @OseeLogMonitor
   private SevereLoggingMonitor severeLoggingMonitor;

   @Before
   public void setup() throws Exception {

      relType1 = TestUtil.createRelationType(51);
      relType2 = TestUtil.createRelationType(52);

      sourceLinksRelType1 = TestUtil.createLinks(4, branch1, relType1);
      sourceLinksRelType2 = TestUtil.createLinks(4, branch1, relType2);

      sourceLinksRelType1AndType2 = new ArrayList<>();
      sourceLinksRelType1AndType2.addAll(sourceLinksRelType1);
      sourceLinksRelType1AndType2.addAll(sourceLinksRelType2);

      TestUtil.setEveryOtherToDeleted(sourceLinksRelType1);
      TestUtil.setEveryOtherToDeleted(sourceLinksRelType2);

      Assert.assertTrue(!artifact1.equals(artifact2));
      Assert.assertTrue(!artifact1.isOnBranch(artifact2.getBranch()));
   }

   @Test
   public void testAddRemoveFromCache() {
      RelationCache relCache = new RelationCache();
      fillCache(relCache);

      List<RelationLink> art1Rels = relCache.getAll(artifact1);
      int totalArt1 = sourceLinksRelType1.size() + sourceLinksRelType2.size();
      Assert.assertEquals(totalArt1, art1Rels.size());
      Assert.assertFalse(Compare.isDifferent(sourceLinksRelType1AndType2, art1Rels));

      int totalArt2 = sourceLinksRelType1.size();
      List<RelationLink> actualArt2 = relCache.getAll(artifact2);
      Assert.assertEquals(totalArt2, actualArt2.size());

      relCache.deCache(artifact1);

      Assert.assertEquals(0, relCache.getAll(artifact1).size());

      //Decache again check no problem
      relCache.deCache(artifact1);
      Assert.assertEquals(0, relCache.getAll(artifact1).size());

      // Check Art 2 still there
      Assert.assertEquals(sourceLinksRelType1.size(), relCache.getAll(artifact2).size());
      relCache.deCache(artifact2);
      Assert.assertEquals(0, relCache.getAll(artifact2).size());
   }

   /**
    * <p>
    * When RelationCache.cache() is changed to throw an exception for a duplicate relation, <br/>
    * then this test should fail and severeLoggingMonitor should not be paused.
    * </p>
    */
   @Test
   public void testCanAddCacheSameRelTwice() throws Exception {
      RelationCache relCache = new RelationCache();

      RelationLink link1 = sourceLinksRelType1.iterator().next();

      severeLoggingMonitor.pause();
      relCache.cache(artifact1, link1);
      relCache.cache(artifact1, link1);
      severeLoggingMonitor.resume();

      Assert.assertEquals(2, relCache.getAll(artifact1).size());
   }

   @Test
   public void testGetByType() {
      RelationCache relCache = new RelationCache();
      fillCache(relCache);

      int totalArt1 = sourceLinksRelType1.size() + sourceLinksRelType2.size();
      Assert.assertEquals(totalArt1, relCache.getAll(artifact1).size());

      int totalArt2 = sourceLinksRelType1.size();
      List<RelationLink> actualArt2 = relCache.getAll(artifact2);
      Assert.assertEquals(totalArt2, actualArt2.size());

      // Find RelationType1s for Artifact 1
      List<RelationLink> art1RelType1 = relCache.getAllByType(artifact1, relType1);
      Assert.assertEquals(sourceLinksRelType1.size(), art1RelType1.size());
      Assert.assertFalse(Compare.isDifferent(sourceLinksRelType1, art1RelType1));
      Assert.assertTrue(Compare.isDifferent(sourceLinksRelType2, art1RelType1));

      // Find RelationType2s for Artifact 1
      List<RelationLink> art1RelType2 = relCache.getAllByType(artifact1, relType2);
      Assert.assertEquals(sourceLinksRelType1.size(), art1RelType2.size());
      Assert.assertTrue(Compare.isDifferent(sourceLinksRelType1, art1RelType2));
      Assert.assertFalse(Compare.isDifferent(sourceLinksRelType2, art1RelType2));

      // Find nothing
      List<RelationLink> relType2ForArt2 = relCache.getAllByType(artifact2, relType2);
      Assert.assertNull(relType2ForArt2);

      // Find RelationType1s for Artifact 2
      List<RelationLink> art2RelType1 = relCache.getAllByType(artifact2, relType1);
      Assert.assertEquals(sourceLinksRelType1.size(), art2RelType1.size());
      Assert.assertFalse(Compare.isDifferent(sourceLinksRelType1, art2RelType1));
      Assert.assertTrue(Compare.isDifferent(sourceLinksRelType2, art2RelType1));
   }

   @Test
   public void testGetRelations() {
      RelationCache relCache = new RelationCache();
      fillCache(relCache);

      List<RelationLink> art1RelsAll = relCache.getRelations(artifact1, DeletionFlag.INCLUDE_DELETED);

      int totalArt1 = sourceLinksRelType1.size() + sourceLinksRelType2.size();
      Assert.assertEquals(totalArt1, art1RelsAll.size());
      Assert.assertFalse(Compare.isDifferent(sourceLinksRelType1AndType2, art1RelsAll));

      List<RelationLink> art1RelsNoDeleteds = relCache.getRelations(artifact1, DeletionFlag.EXCLUDE_DELETED);
      Assert.assertEquals(totalArt1 / 2, art1RelsNoDeleteds.size());

      for (RelationLink link : art1RelsNoDeleteds) {
         Assert.assertFalse(String.format("Was deleted [%s]", link), link.isDeleted());
      }
   }

   @Test
   public void testGetRelationByIdOnArtifact() {
      RelationCache relCache = new RelationCache();

      RelationLink link11 = TestUtil.createRelationLink(0, 11, 22, branch1, relType1);
      RelationLink link12 = TestUtil.createRelationLink(0, 33, 44, branch1, relType1);

      RelationLink link21 = TestUtil.createRelationLink(1, 55, 66, branch2, relType2);
      RelationLink link22 = TestUtil.createRelationLink(1, 77, 88, branch2, relType2);

      relCache.cache(artifact1, link11);
      relCache.cache(artifact1, link12);

      relCache.cache(artifact2, link21);
      relCache.cache(artifact2, link22);

      RelationLink actual = null;
      ArtifactToken art1WrongBranch = ArtifactToken.valueOf(artifact1, branch2);
      ArtifactToken art2WrongBranch = ArtifactToken.valueOf(artifact2, branch1);

      // Find Relation Link Id 1 -
      actual = relCache.getByRelIdOnArtifact(1, artifact1, ArtifactToken.SENTINEL);
      Assert.assertNull(actual);

      actual = relCache.getByRelIdOnArtifact(1, art1WrongBranch, ArtifactToken.SENTINEL);
      Assert.assertNull(actual);

      actual = relCache.getByRelIdOnArtifact(1, art2WrongBranch, ArtifactToken.SENTINEL);
      Assert.assertNull(actual);

      actual = relCache.getByRelIdOnArtifact(1, artifact2, ArtifactToken.SENTINEL);
      Assert.assertEquals(link21, actual);
      Assert.assertFalse(link22.equals(actual));

      actual = relCache.getByRelIdOnArtifact(1, ArtifactToken.SENTINEL, artifact2);
      Assert.assertEquals(link21, actual);
      Assert.assertFalse(link22.equals(actual));

      // Find Relation Link Id 0 -
      actual = relCache.getByRelIdOnArtifact(0, ArtifactToken.SENTINEL, artifact2);
      Assert.assertNull(actual);

      actual = relCache.getByRelIdOnArtifact(0, ArtifactToken.SENTINEL, artifact1);
      Assert.assertEquals(link11, actual);
      Assert.assertFalse(link12.equals(actual));

      actual = relCache.getByRelIdOnArtifact(0, artifact1, ArtifactToken.SENTINEL);
      Assert.assertEquals(link11, actual);
      Assert.assertFalse(link12.equals(actual));
   }

   @Test
   public void testGetLoadedRelation() {
      RelationCache relCache = new RelationCache();

      int art1Id = artifact1.getArtId();
      int art2Id = artifact2.getArtId();

      RelationLink link11 = TestUtil.createRelationLink(0, art1Id, 22, branch1, relType1);
      RelationLink link12 = TestUtil.createRelationLink(0, 33, 44, branch1, relType1);

      RelationLink link21 = TestUtil.createRelationLink(1, 55, 66, branch2, relType2);
      RelationLink link22 = TestUtil.createRelationLink(1, 77, art2Id, branch2, relType2);
      link22.delete(false);

      relCache.cache(artifact1, link11);
      relCache.cache(artifact1, link12);

      relCache.cache(artifact2, link21);
      relCache.cache(artifact2, link22);

      RelationLink actual = null;
      // Find relation link 11
      actual = relCache.getLoadedRelation(artifact1, artifact1, art44, relType1, DeletionFlag.INCLUDE_DELETED);
      Assert.assertNull(actual);

      actual = relCache.getLoadedRelation(artifact1, artifact1, art22, relType1, DeletionFlag.INCLUDE_DELETED);
      Assert.assertEquals(link11, actual);

      // Find relation link 22
      actual = relCache.getLoadedRelation(artifact2, art77, artifact2, relType2, DeletionFlag.EXCLUDE_DELETED);
      Assert.assertNull(actual);

      actual = relCache.getLoadedRelation(artifact2, art77, artifact2, relType2, DeletionFlag.INCLUDE_DELETED);
      Assert.assertEquals(link22, actual);
   }

   @Test
   public void testGetLoadedRelationNoId() {
      RelationCache relCache = new RelationCache();

      ArtifactToken art11 = ArtifactToken.valueOf(11, branch1);

      ArtifactToken art22WrongBranch = ArtifactToken.valueOf(22, branch2);
      ArtifactToken art551 = ArtifactToken.valueOf(551, branch2);
      ArtifactToken art661 = ArtifactToken.valueOf(661, branch2);
      ArtifactToken art661WrongBranch = ArtifactToken.valueOf(661, branch1);

      RelationLink link11 = TestUtil.createRelationLink(0, 11, 22, branch1, relType1);
      RelationLink link12 = TestUtil.createRelationLink(1, 11, 22, branch1, relType1);
      RelationLink link13 = TestUtil.createRelationLink(3, 22, 11, branch1, relType1);

      RelationLink link21 = TestUtil.createRelationLink(4, 551, 661, branch2, relType2);
      RelationLink link22 = TestUtil.createRelationLink(5, 551, 661, branch2, relType2);
      RelationLink link23 = TestUtil.createRelationLink(6, 661, 551, branch2, relType2);

      relCache.cache(artifact1, link11);
      relCache.cache(artifact1, link12);
      relCache.cache(artifact1, link13);

      relCache.cache(artifact2, link21);
      relCache.cache(artifact2, link22);
      relCache.cache(artifact2, link23);

      RelationLink actual = null;

      //  Must match branch
      actual = relCache.getLoadedRelation(relType1, artifact1, art22WrongBranch);
      Assert.assertNull(actual);

      actual = relCache.getLoadedRelation(relType2, artifact1, art22);
      Assert.assertNull(actual);

      actual = relCache.getLoadedRelation(relType1, artifact1, art22);
      Assert.assertEquals(link11, actual);
      Assert.assertFalse(link12.equals(actual));

      actual = relCache.getLoadedRelation(relType1, art22, artifact1);
      Assert.assertEquals(link13, actual);

      actual = relCache.getLoadedRelation(relType1, artifact1, art11);
      Assert.assertEquals(link13, actual);

      // Find  Must match branch
      actual = relCache.getLoadedRelation(relType2, artifact2, art661WrongBranch);
      Assert.assertNull(actual);

      actual = relCache.getLoadedRelation(relType2, artifact2, art661);
      Assert.assertEquals(link21, actual);
      Assert.assertFalse(link22.equals(actual));

      actual = relCache.getLoadedRelation(relType2, art661, artifact2);
      Assert.assertEquals(link23, actual);

      actual = relCache.getLoadedRelation(relType2, artifact2, art551);
      Assert.assertEquals(link23, actual);
   }

   @Test
   public void testDeCache() {
      RelationCache relCache = new RelationCache();

      Artifact artifactA = new Artifact(54L, COMMON);
      Artifact artifactB = new Artifact(55L, COMMON);

      RelationType type = new RelationType(0x00L, "type name", artifactA.getName(), artifactB.getName(),
         CoreArtifactTypes.Artifact, CoreArtifactTypes.Artifact, RelationTypeMultiplicity.MANY_TO_MANY, null);
      RelationLink link = new RelationLink(artifactA, artifactB, COMMON, type, 77, GammaId.valueOf(88), "",
         ModificationType.MODIFIED, ApplicabilityId.BASE);
      relCache.cache(artifactA, link);
      relCache.cache(artifactB, link);

      List<RelationLink> artARels = relCache.getAll(artifactA);
      Assert.assertEquals(1, artARels.size());

      relCache.deCache(artifactA);
      artARels = relCache.getAll(artifactA);
      Assert.assertEquals(0, artARels.size());

      List<RelationLink> artBRels = relCache.getAll(artifactB);
      Assert.assertEquals(0, artBRels.size());
   }

   private void fillCache(RelationCache relCache) {
      for (RelationLink link : sourceLinksRelType1) {
         relCache.cache(artifact1, link);
      }

      for (RelationLink link : sourceLinksRelType1) {
         relCache.cache(artifact2, link);
      }

      for (RelationLink link : sourceLinksRelType2) {
         relCache.cache(artifact1, link);
      }
   }
}
