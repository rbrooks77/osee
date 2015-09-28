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
package org.eclipse.osee.ats.core.client.internal.store;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.api.IAtsConfigObject;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.core.client.internal.IAtsArtifactWriter;
import org.eclipse.osee.ats.core.client.internal.config.AtsArtifactConfigCache;
import org.eclipse.osee.ats.core.util.AtsUtilCore;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IRelationTypeSide;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;

/**
 * @author Donald G. Dunne
 */
public abstract class AbstractAtsArtifactWriter<T extends IAtsConfigObject> implements IAtsArtifactWriter<T> {

   /**
    * Overwrite current relations of type side with new atsObject artifact. Artifacts must already exist in system for
    * this method to work. Persist must be done outside this method
    * 
    * @return collection of artifacts that were related
    */
   protected Collection<Artifact> setRelationsOfType(AtsArtifactConfigCache cache, Artifact artifact, Collection<? extends IAtsObject> atsObjects, IRelationTypeSide side) throws OseeCoreException {
      Conditions.checkNotNull(artifact, "artifact");
      List<Artifact> newArts = new ArrayList<>();
      for (IAtsObject version : atsObjects) {
         Artifact verArt = cache.getArtifact(version);
         newArts.add(verArt);
      }
      artifact.setRelations(side, newArts);
      return newArts;
   }

   protected Artifact getArtifactOrCreate(AtsArtifactConfigCache cache, IArtifactType artifactType, IAtsConfigObject atsObject, IAtsChangeSet changes) throws OseeCoreException {
      Artifact artifact = cache.getArtifact(atsObject);
      if (artifact == null) {
         artifact =
            ArtifactTypeManager.addArtifact(artifactType, AtsUtilCore.getAtsBranch(), atsObject.getName(),
               GUID.create(), atsObject.getUuid());
         changes.add(artifact);
      }
      return artifact;
   }

}
