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
package org.eclipse.osee.ats.impl.internal.version;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;

public class AtsVersionCache {

   public static AtsVersionCache instance = new AtsVersionCache();

   public Map<String, IAtsVersion> targetedTeamWfHridToVersion = new HashMap<String, IAtsVersion>(500);

   public IAtsVersion getVersion(IAtsTeamWorkflow teamWf) {
      return targetedTeamWfHridToVersion.get(teamWf.getHumanReadableId());
   }

   public boolean hasVersion(IAtsTeamWorkflow teamWf) {
      IAtsVersion version = getVersion(teamWf);
      return version != null;
   }

   public IAtsVersion cache(IAtsTeamWorkflow teamWf, IAtsVersion version) {
      return targetedTeamWfHridToVersion.put(teamWf.getHumanReadableId(), version);
   }

   public void deCache(IAtsTeamWorkflow teamWf) {
      targetedTeamWfHridToVersion.remove(teamWf.getHumanReadableId());
   }

}
