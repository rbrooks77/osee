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
package org.eclipse.osee.framework.ui.skynet.search;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.jdk.core.util.StringFormat;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;

/**
 * @author Roberto E. Escobar
 */
final class RemoteArtifactSearch extends AbstractArtifactSearchQuery {
   private final String queryString;
   private final String[] attributeTypeNames;
   private final boolean includeDeleted;
   private final boolean matchWordOrder;
   private final Branch branch;

   RemoteArtifactSearch(String queryString, Branch branch, boolean includeDeleted, boolean matchWordOrder, String... attributeTypeNames) {
      this.branch = branch;
      this.includeDeleted = includeDeleted;
      this.attributeTypeNames = attributeTypeNames;
      this.queryString = queryString;
      this.matchWordOrder = matchWordOrder;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.search.AbstractArtifactSearchQuery#getArtifacts()
    */
   @Override
   public Collection<Artifact> getArtifacts() throws Exception {
      return ArtifactQuery.getArtifactsFromAttributeWithKeywords(branch, queryString, matchWordOrder, includeDeleted,
            attributeTypeNames);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.search.AbstractArtifactSearchQuery#getCriteriaLabel()
    */
   @Override
   public String getCriteriaLabel() {
      List<String> optionsList = new ArrayList<String>();
      if (includeDeleted) {
         optionsList.add("Include Deleted");
      }

      if (matchWordOrder) {
         optionsList.add("Match Word Order");
      }

      if (attributeTypeNames != null && attributeTypeNames.length > 0) {
         optionsList.add(String.format("Attribute Type Filter:%s", Arrays.deepToString(attributeTypeNames)));
      }

      String options = String.format(" - Options:[%s]", StringFormat.listToValueSeparatedString(optionsList, "& "));
      return String.format("%s%s", queryString, optionsList.size() > 0 ? options : "");
   }
}
