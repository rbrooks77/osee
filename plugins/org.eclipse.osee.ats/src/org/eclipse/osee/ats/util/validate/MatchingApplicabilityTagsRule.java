/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.util.validate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.util.WordCoreUtil;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.utility.ApplicabilityUtility;
import org.eclipse.osee.framework.skynet.core.word.WordUtil;

/**
 * @author Morgan E. Cook
 */
public class MatchingApplicabilityTagsRule extends AbstractValidationRule {

   private HashCollection<String, String> validFeatureValues;
   private HashSet<String> validConfigurations;

   @Override
   protected ValidationResult validate(Artifact artToValidate, IProgressMonitor monitor) throws OseeCoreException {
      Collection<String> errorMessages = new ArrayList<>();
      String wordml = artToValidate.getSoleAttributeValue(CoreAttributeTypes.WordTemplateContent, "");

      if (validFeatureValues == null) {
         validFeatureValues = ApplicabilityUtility.getValidFeatureValuesForBranch(artToValidate.getBranch());
      }

      if (validConfigurations == null) {
         validConfigurations = WordUtil.getValidConfigurations(artToValidate.getBranch());
      }

      boolean validationPassed = true;
      if (!validFeatureValues.isEmpty()) {
         validationPassed = !WordCoreUtil.areApplicabilityTagsInvalid(wordml, artToValidate.getBranch(),
            validFeatureValues, validConfigurations);
         if (!validationPassed) {
            errorMessages.add(String.format(
               "Validation Failed. The following artifact has invalid feature values and/or mismatching start and end applicability tags: " //
                  + "Artifact Id: [%s], Artifact Name: [%s]",
               artToValidate.getId(), artToValidate.getSafeName()));
         }
      }

      return new ValidationResult(errorMessages, validationPassed);
   }

   @Override
   public String getRuleDescription() {
      return "<b>Applicability Check: </b>" + "Ensure applicability tags are valid in the artifact(s)";
   }

   @Override
   public String getRuleTitle() {
      return "Applicability Check:";
   }
}
