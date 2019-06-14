/*******************************************************************************
 * Copyright (c) 2019 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.define.rest.operations;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author David W. Miller
 */
public class ArtifactValidationCheckOperation {
   private final List<ArtifactReadable> itemsToCheck;
   private final boolean stopOnFirstError;
   private final XResultData results;

   public ArtifactValidationCheckOperation(OrcsApi orcsApi, XResultData results, ArtifactReadable parentArtifact, boolean stopOnFirstError) {
      this.stopOnFirstError = stopOnFirstError;
      this.itemsToCheck = new ArrayList<>();
      itemsToCheck.add(parentArtifact);// performance problem here> parentArtifact.getDescendants();
      this.results = results;
   }

   public boolean isStopOnFirstError() {
      return stopOnFirstError;
   }

   public XResultData validate() {
      for (ArtifactReadable artifact : itemsToCheck) {
         boolean hasError = validateArt(artifact, results);
         if (isStopOnFirstError() && hasError) {
            break;
         }
      }
      return results;
   }

   private boolean validateArt(ArtifactReadable artifact, XResultData results) {
      return false; //TODO put the correct validation here
   }
}
