/*******************************************************************************
 * Copyright (c) 2019 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.enums;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.jdk.core.type.Pair;

/**
 * @author Ryan D. Brooks
 */
public final class AttributeMultiplicity extends ConcurrentHashMap<AttributeTypeToken, Pair<Integer, Integer>> {
   private final ArtifactTypeToken artifactType;

   public AttributeMultiplicity(List<ArtifactTypeToken> tokens, long id, boolean isAbstract, String name, ArtifactTypeToken... superTypes) {
      ArtifactTypeToken artifactType = ArtifactTypeToken.valueOf(id, isAbstract, name, this, superTypes);
      tokens.add(artifactType);
      this.artifactType = artifactType;
   }

   public AttributeMultiplicity put(AttributeTypeToken attributeType, Integer minOccurrence, Integer maxOccurrence) {
      put(attributeType, new Pair<>(minOccurrence, maxOccurrence));
      return this;
   }

   public Integer getMinimum(AttributeTypeToken attributeType) {
      return get(attributeType).getFirst();
   }

   public Integer getMaximum(AttributeTypeToken attributeType) {
      return get(attributeType).getSecond();
   }

   public ArtifactTypeToken get() {
      return artifactType;
   }
}