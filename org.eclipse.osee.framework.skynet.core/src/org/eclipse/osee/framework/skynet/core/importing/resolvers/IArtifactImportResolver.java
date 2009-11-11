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
package org.eclipse.osee.framework.skynet.core.importing.resolvers;

import org.eclipse.osee.framework.core.data.Branch;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.importing.RoughArtifact;

/**
 * Used during imports that ask for artifact re-use to resolve the Artifact to be used for a particular RoughArtifact
 * 
 * @author Robert A. Fisher
 */
public interface IArtifactImportResolver {

   public Artifact resolve(RoughArtifact roughArtifact, Branch branch) throws OseeCoreException;
}
