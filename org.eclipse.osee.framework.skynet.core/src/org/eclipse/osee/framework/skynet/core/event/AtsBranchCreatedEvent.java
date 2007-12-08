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
package org.eclipse.osee.framework.skynet.core.event;

/**
 * Special branch event so ATS can perform certain functionality when branch is created
 * 
 * @author Donald G. Dunne
 */
public class AtsBranchCreatedEvent extends GuidEvent {

   /**
    * @param sender
    */
   public AtsBranchCreatedEvent(Object sender) {
      super(sender);
   }
}
