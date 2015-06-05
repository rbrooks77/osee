/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.program;

import java.util.List;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;

/**
 * @author Donald G. Dunne
 */
public interface IAtsProgramService {

   IAtsTeamDefinition getTeamDefinition(IAtsProgram atsProgram);

   IAtsProgram getProgram(IAtsWorkItem workItem);

   IAtsProgram getProgramByGuid(String guid);

   List<IAtsProgram> getAllPrograms();
}
