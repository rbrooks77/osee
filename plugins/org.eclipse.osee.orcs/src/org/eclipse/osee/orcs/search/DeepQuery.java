/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.search;

import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.jdk.core.type.Pair;

/**
 * Queries that are not restricted by branch
 *
 * @author Ryan D. Brooks
 */
public interface DeepQuery {

   /**
    * Search criteria that finds a given artifact type using type inheritance
    */
   DeepQuery andIsOfType(ArtifactTypeId... artifactTypes);

   List<Pair<ArtifactId, String>> collect(AttributeTypeId attributeType);
}