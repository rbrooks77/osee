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
package org.eclipse.osee.framework.ui.skynet.widgets.dialog;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

public class CheckBoxDialog extends MessageDialog {

   private Button checkButton;
   boolean fillVertically = false;
   private String confirmationStatement;

   //Have to save off the value so it is available after the dialog is closed since checkButton will get disposed.
   private boolean checked = false;

   public CheckBoxDialog(Shell parentShell, String dialogTitle, Image dialogTitleImage, String dialogMessage, String confirmationStatement, int dialogImageType, int defaultIndex) {
      super(parentShell, dialogTitle, dialogTitleImage, dialogMessage, dialogImageType, new String[] {"OK", "Cancel"},
            defaultIndex);

      this.confirmationStatement = confirmationStatement;
      setBlockOnOpen(true);
   }

   protected Control createCustomArea(Composite parent) {
      Composite composite = new Composite(parent, SWT.NONE);
      composite.setLayout(new GridLayout(2, false));
      composite.setLayoutData(new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL));

      checkButton = new Button(composite, SWT.CHECK);
      checkButton.setText(confirmationStatement);
      checkButton.addSelectionListener(new SelectionAdapter() {

         @Override
         public void widgetSelected(SelectionEvent e) {
            checked = checkButton.getSelection();
            getButton(0).setEnabled(checked);
         }
      });

      return composite;
   }

   @Override
   protected void createButtonsForButtonBar(Composite parent) {
      super.createButtonsForButtonBar(parent);

      // Start off with OK disabled
      getButton(0).setEnabled(false);
   }

   public boolean isChecked() {
      return checked;
   }
}
