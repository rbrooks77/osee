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
package org.eclipse.osee.framework.search.engine;

import org.eclipse.osee.framework.search.engine.internal.SearchStatistics;

/**
 * @author Roberto E. Escobar
 */
public interface ISearchEngine {

   /**
    * Searches tagged items for matches meeting criteria specified. If branchId is less than 0, search will include all
    * branches in the system.
    * 
    * @param searchString
    * @param branchId
    * @param options
    * @return result string having the following convention - artifact join query id, expected number of artifacts
    * @throws Exception
    */
   public String search(String searchString, int branchId, Options options) throws Exception;

   /**
    * Get statistics
    * 
    * @return search statistics
    */
   SearchStatistics getStatistics();

   /**
    * Clear Statistics
    */
   void clearStatistics();
}
