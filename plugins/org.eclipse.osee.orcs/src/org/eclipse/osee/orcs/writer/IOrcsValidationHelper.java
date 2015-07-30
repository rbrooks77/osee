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
package org.eclipse.osee.orcs.writer;

/**
 * @author Donald G. Dunne
 */
public interface IOrcsValidationHelper {

   boolean isBranchExists(long branchUuid);

   boolean isUserExists(String userId);

   boolean isArtifactTypeExist(long artifactTypeUuid);

   boolean isRelationTypeExist(long relationTypeUuid);

   boolean isAttributeTypeExists(long attributeTypeUuid);

   public boolean isArtifactExists(long branchUuid, long artifactUuid);

   boolean isAttributeTypeExists(String attributeTypeName);

}
