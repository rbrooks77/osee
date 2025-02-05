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
package org.eclipse.osee.framework.skynet.core.attribute.providers;

import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Roberto E. Escobar
 */
public interface IAttributeDataProvider {

   public String getDisplayableString() throws OseeCoreException;

   public void setDisplayableString(String toDisplay) throws OseeDataStoreException;

   public void loadData(Object... objects) throws OseeCoreException;

   public Object getValue();

   public Object[] getData() throws OseeDataStoreException;

   public void persist(int storageId) throws OseeCoreException;

   public void purge() throws OseeCoreException;
}
