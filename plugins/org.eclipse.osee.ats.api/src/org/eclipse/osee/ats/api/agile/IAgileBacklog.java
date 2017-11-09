/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.agile;

import org.eclipse.osee.ats.api.workflow.IAtsGoal;

/**
 * @author Donald G. Dunne
 */
public interface IAgileBacklog extends IAtsGoal, IAgileObject {

   public long getTeamId();

   public boolean isActive();

}
