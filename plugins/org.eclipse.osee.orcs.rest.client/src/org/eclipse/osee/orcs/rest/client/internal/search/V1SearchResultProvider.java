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
package org.eclipse.osee.orcs.rest.client.internal.search;

import java.net.URI;
import java.util.List;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.framework.core.services.URIProvider;
import org.eclipse.osee.orcs.rest.client.internal.WebClientProvider;
import org.eclipse.osee.orcs.rest.model.search.OutputFormat;
import org.eclipse.osee.orcs.rest.model.search.Predicate;
import org.eclipse.osee.orcs.rest.model.search.RequestType;
import org.eclipse.osee.orcs.rest.model.search.SearchParameters;
import org.eclipse.osee.orcs.rest.model.search.SearchResult;
import com.sun.jersey.api.client.WebResource;

/**
 * @author John Misinco
 */
public class V1SearchResultProvider implements SearchResultProvider {

   private final WebClientProvider clientProvider;
   private final URIProvider uriProvider;

   public V1SearchResultProvider(URIProvider uriProvider, WebClientProvider clientProvider) {
      super();
      this.uriProvider = uriProvider;
      this.clientProvider = clientProvider;
   }

   @Override
   public int getSearchCount(IOseeBranch branch, List<Predicate> predicates, QueryOptions options) throws OseeCoreException {
      SearchResult result = performSearch(RequestType.COUNT, OutputFormat.XML, branch, predicates, options);
      return result.getTotal();
   }

   @Override
   public SearchResult getSearchResults(IOseeBranch branch, List<Predicate> predicates, QueryOptions options) throws OseeCoreException {
      SearchResult result = performSearch(RequestType.IDS, OutputFormat.XML, branch, predicates, options);
      return result;
   }

   private SearchResult performSearch(RequestType requestType, OutputFormat outputFormat, IOseeBranch branch, List<Predicate> predicates, QueryOptions options) throws OseeCoreException {
      int fromTx = 0;
      if (options.isHistorical()) {
         fromTx = options.getFromTransaction();
      }

      boolean includeTypeInh = false;
      if (options.isTypeInheritanceIncluded()) {
         includeTypeInh = true;
      }

      boolean includeCache = false;
      if (options.isCacheIncluded()) {
         includeCache = true;
      }

      boolean includeDeleted = false;
      if (options.areDeletedIncluded()) {
         includeDeleted = true;
      }

      SearchParameters params =
         new SearchParameters(branch.getGuid(), predicates, outputFormat.name().toLowerCase(),
            requestType.name().toLowerCase(), fromTx, includeTypeInh, includeCache, includeDeleted);

      URI uri = uriProvider.getEncodedURI(String.format("oseex/branch/%s/artifact/search/v1", branch.getGuid()), null);

      WebResource resource = clientProvider.createResource(uri);
      SearchResult searchResult = null;
      try {
         searchResult =
            resource.accept(MediaType.APPLICATION_JSON_TYPE).type(MediaType.APPLICATION_JSON_TYPE).post(
               SearchResult.class, params);
      } catch (Exception ex) {
         OseeExceptions.wrapAndThrow(ex);
      }
      return searchResult;
   }

}
