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
package org.eclipse.osee.orcs.db.internal.sql;

import java.util.List;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.orcs.core.ds.Criteria;
import org.eclipse.osee.orcs.core.ds.CriteriaSet;

/**
 * @author Roberto E. Escobar
 */
public interface SqlHandlerFactory {

   List<SqlHandler<?>> createHandlers(CriteriaSet... criteriaSet) throws OseeCoreException;

   List<SqlHandler<?>> createHandlers(Iterable<CriteriaSet> criteriaSets) throws OseeCoreException;

   SqlHandler<?> createHandler(Criteria criteria) throws OseeCoreException;
}