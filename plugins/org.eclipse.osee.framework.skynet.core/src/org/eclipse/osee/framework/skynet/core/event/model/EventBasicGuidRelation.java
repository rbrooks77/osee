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
package org.eclipse.osee.framework.skynet.core.event.model;

import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.core.model.event.DefaultBasicGuidArtifact;
import org.eclipse.osee.framework.core.model.event.DefaultBasicIdRelation;
import org.eclipse.osee.framework.skynet.core.relation.RelationEventType;

/**
 * @author Donald G. Dunne
 */
public class EventBasicGuidRelation extends DefaultBasicIdRelation {

   private final RelationEventType relationEventType;
   private final long artAId;
   private final long artBId;
   private String rationale;

   public EventBasicGuidRelation(RelationEventType relationEventType, ArtifactId artAId, ArtifactId artBId, DefaultBasicIdRelation guidRel) {
      this(relationEventType, guidRel.getBranch(), guidRel.getRelTypeGuid(), guidRel.getRelationId(),
         guidRel.getGammaId(), artAId.getId().intValue(), guidRel.getArtA(), artBId.getId().intValue(),
         guidRel.getArtB());
   }

   public EventBasicGuidRelation(RelationEventType relationEventType, BranchId branchUuid, Long relTypeGuid, long relationId, GammaId gammaId, long artAId, DefaultBasicGuidArtifact artA, long artBId, DefaultBasicGuidArtifact artB) {
      super(branchUuid, relTypeGuid, relationId, gammaId, artA, artB);
      this.relationEventType = relationEventType;
      this.artAId = artAId;
      this.artBId = artBId;
   }

   public RelationEventType getModType() {
      return relationEventType;
   }

   @Override
   public String toString() {
      return String.format("[%s - B:%s - TG:%s - GI:%s - RI:%s - A:%s - B:%s]", relationEventType,
         getBranch().getIdString(), getBranch().getIdString(), getGammaId(), getRelationId(), getArtA(), getArtB());
   }

   public long getArtAId() {
      return artAId;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + (int) (artAId ^ (artAId >>> 32));
      result = prime * result + (int) (artBId ^ (artBId >>> 32));
      result = prime * result + ((relationEventType == null) ? 0 : relationEventType.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }
      if (!super.equals(obj)) {
         return false;
      }
      if (getClass() != obj.getClass()) {
         return false;
      }
      EventBasicGuidRelation other = (EventBasicGuidRelation) obj;
      if (artAId != other.artAId) {
         return false;
      }
      if (artBId != other.artBId) {
         return false;
      }
      if (relationEventType != other.relationEventType) {
         return false;
      }
      return true;
   }

   public long getArtBId() {
      return artBId;
   }

   public String getRationale() {
      return rationale;
   }

   public void setRationale(String rationale) {
      this.rationale = rationale;
   }

   public boolean is(RelationEventType... relationEventTypes) {
      for (RelationEventType eventModType : relationEventTypes) {
         if (this.relationEventType == eventModType) {
            return true;
         }
      }
      return false;
   }

}
