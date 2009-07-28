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
package org.eclipse.osee.framework.skynet.core.access;

import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.database.sql.SkynetDatabase;
import org.eclipse.osee.framework.jdk.core.type.DoubleKeyHashMap;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Jeff C. Phillips
 */
public class ArtifactAccessObject extends AccessObject {

   private final Integer artId;
   private final Integer branchId;
   private static final DoubleKeyHashMap<Integer, Integer, ArtifactAccessObject> cache =
         new DoubleKeyHashMap<Integer, Integer, ArtifactAccessObject>();

   public ArtifactAccessObject(Integer artId, Integer branchId) {
      super();
      this.artId = artId;
      this.branchId = branchId;
   }

   public Integer getArtId() {
      return artId;
   }

   @Override
   public int getBranchId() {
      return branchId;
   }

   @Override
   public void removeFromCache() {
      cache.remove(artId, branchId);
   }

   @Override
   public void removeFromDatabase(int subjectId) throws OseeDataStoreException {
      final String DELETE_ARTIFACT_ACL =
            "DELETE FROM " + SkynetDatabase.ARTIFACT_TABLE_ACL + " WHERE privilege_entity_id = ? AND art_id =? AND branch_id =?";
      ConnectionHandler.runPreparedUpdate(DELETE_ARTIFACT_ACL, subjectId, artId, branchId);
   }

   public static ArtifactAccessObject getArtifactAccessObject(Artifact artifact) {
      Integer artId = artifact.getArtId();
      Integer branchId = artifact.getBranch().getBranchId();
      return getArtifactAccessObject(artId, branchId);
   }

   public static ArtifactAccessObject getArtifactAccessObject(Integer artId, Integer branchId) {
      ArtifactAccessObject accessObject = cache.get(artId, branchId);

      if (accessObject == null) {
         accessObject = new ArtifactAccessObject(artId, branchId);
         cache.put(artId, branchId, accessObject);
      }
      return accessObject;
   }

   public static AccessObject getArtifactAccessObjectFromCache(Artifact art) {
      return getArtifactAccessObjectFromCache(art.getArtId(), art.getBranch().getBranchId());
   }

   public static AccessObject getArtifactAccessObjectFromCache(Integer artId2, Integer branchId2) {
      return cache.get(artId2, branchId2);
   }

   @Override
   public boolean equals(Object obj) {
      if (!(obj instanceof ArtifactAccessObject)) {
         return false;
      }
      ArtifactAccessObject ao = (ArtifactAccessObject) obj;
      return ao.artId.equals(this.artId) && ao.branchId.equals(this.branchId);
   }
}
