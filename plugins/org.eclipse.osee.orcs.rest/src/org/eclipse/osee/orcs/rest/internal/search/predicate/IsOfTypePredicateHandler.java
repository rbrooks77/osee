/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.rest.internal.search.predicate;

import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.orcs.rest.internal.search.Predicate;
import org.eclipse.osee.orcs.rest.internal.search.PredicateHandler;
import org.eclipse.osee.orcs.rest.internal.search.dsl.SearchMethod;
import org.eclipse.osee.orcs.search.QueryBuilder;

/**
 * @author John R. Misinco
 * @author Roberto E. Escobar
 */
public class IsOfTypePredicateHandler implements PredicateHandler {

   @Override
   public QueryBuilder handle(QueryBuilder builder, Predicate predicate) throws OseeCoreException {
      if (predicate.getType() != SearchMethod.IS_OF_TYPE) {
         throw new OseeArgumentException("This predicate handler only supports [%s]", SearchMethod.IS_OF_TYPE);
      }
      List<String> values = predicate.getValues();
      Conditions.checkNotNull(values, "values");
      Collection<IArtifactType> artTypes = PredicateHandlerUtil.getIArtifactTypes(values);
      builder = andIsOfType(builder, artTypes);
      return builder;
   }

   protected QueryBuilder andIsOfType(QueryBuilder builder, Collection<IArtifactType> artTypes) throws OseeCoreException {
      return builder.andIsOfType(artTypes);
   }

}
