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
package org.eclipse.osee.ats.core.client.internal.config;

import org.eclipse.osee.ats.api.IAtsServices;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.core.client.util.AtsUtilClient;
import org.eclipse.osee.ats.core.config.ActionableItem;
import org.eclipse.osee.ats.core.config.IActionableItemFactory;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.jdk.core.util.GUID;

/**
 * @author Donald G. Dunne
 */
public class ActionableItemFactory implements IActionableItemFactory {

   @Override
   public IAtsActionableItem createActionableItem(String guid, String name, long uuid, IAtsChangeSet changes, IAtsServices services) {
      if (guid == null) {
         throw new IllegalArgumentException("guid can not be null");
      }
      ArtifactToken artifact = changes.createArtifact(AtsArtifactTypes.ActionableItem, name, guid, uuid);
      return new ActionableItem(services.getLogger(), services, artifact);
   }

   @Override
   public IAtsActionableItem createActionableItem(String name, IAtsChangeSet changes, IAtsServices services) {
      return createActionableItem(GUID.create(), name, AtsUtilClient.createConfigObjectUuid(), changes, services);
   }

}
