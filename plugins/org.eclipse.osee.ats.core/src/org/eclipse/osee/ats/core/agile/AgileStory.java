/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.agile;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.agile.IAgileStory;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.core.model.impl.AtsConfigObject;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.logger.Log;

/**
 * @author Donald G Dunne
 */
public class AgileStory extends AtsConfigObject implements IAgileStory {

   public AgileStory(Log logger, AtsApi atsApi, ArtifactToken artifact) {
      super(logger, atsApi, artifact);
   }

   @Override
   public List<Long> getTaskIds() {
      List<Long> ids = new ArrayList<>();
      for (ArtifactId child : atsApi.getRelationResolver().getChildren(artifact)) {
         if (atsApi.getStoreService().isOfType(child, AtsArtifactTypes.AgileStory)) {
            ids.add(new Long(child.getId()));
         }
      }
      return ids;
   }

   @Override
   public Long getFeatureId() {
      return atsApi.getRelationResolver().getParent(artifact).getId();
   }

}
