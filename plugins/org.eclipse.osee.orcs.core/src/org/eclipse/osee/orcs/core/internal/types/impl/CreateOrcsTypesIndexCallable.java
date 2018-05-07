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
package org.eclipse.osee.orcs.core.internal.types.impl;

import org.eclipse.osee.framework.core.executor.CancellableCallable;
import org.eclipse.osee.framework.resource.management.IResource;
import org.eclipse.osee.orcs.core.internal.types.OrcsTypesIndex;

/**
 * @author Roberto E. Escobar
 */
public class CreateOrcsTypesIndexCallable extends CancellableCallable<OrcsTypesIndex> {
   private final OrcsTypesIndexer indexer;
   private final IResource resource;

   public CreateOrcsTypesIndexCallable(OrcsTypesIndexer indexer, IResource resource) {
      this.indexer = indexer;
      this.resource = resource;
   }

   @Override
   public OrcsTypesIndex call() throws Exception {
      return indexer.index(resource);
   }
}