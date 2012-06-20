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
package org.eclipse.osee.ats.actions.wizard;

import java.util.Collection;
import org.eclipse.osee.ats.core.model.IAtsActionableItem;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;

/**
 * Allows the New Action wizard to be extended with custom widgets when the appropriate Actionable Items are selected
 * 
 * @author Donald G. Dunne
 */
public interface IAtsWizardItem {

   /**
    * Add the desired XWidget declarations to the stringbuffer. Selected AIAs are provided so validation can be done to
    * determine what (if any) widgets should be added. eg. <XWidget displayName=\"Description\" height=\"80\"
    * required=\"true\" xwidgetType=\"XText\" fill=\"Vertically\" \"/>");
    */
   void getWizardXWidgetExtensions(Collection<IAtsActionableItem> aias, StringBuffer stringBuffer) throws Exception;

   /**
    * @return true if widgets will be added based on selected aias
    */
   boolean hasWizardXWidgetExtensions(Collection<IAtsActionableItem> aias) throws Exception;

   /**
    * Determine if Action is valid to create based on wizard data entered. hasWizardXWidgetExtenstions will be called to
    * determine if this method should be called.
    * 
    * @return result of validation. if true, action will be created; if not, error will popup and action will not be
    * created
    */
   Result isActionValidToCreate(Collection<IAtsActionableItem> aias, NewActionWizard wizard);

   /**
    * Callback with created action upon completion and creation of the action and it's workflows.
    * hasWizardXWidgetExtenstions will be called to determine if this method should be called.
    */
   void wizardCompleted(Artifact actionArt, NewActionWizard wizard, SkynetTransaction transaction) throws Exception;

   /**
    * Validation that the data entered is valid and the wizard can be finished. This will be called after every
    * character is entered, so extensive processing should not be performed during this check. Extensive processing can
    * be performed during isActionValidToCreate(). hasWizardXWidgetExtenstions will be called to determine if this
    * method should be called.
    * 
    * @return true if widget data entered is valid
    */
   Result isWizardXWidgetsComplete(NewActionWizard wizard);

}
