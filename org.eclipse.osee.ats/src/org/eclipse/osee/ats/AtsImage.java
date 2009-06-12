/*******************************************************************************
 * Copyright (c) 2009 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osee.framework.ui.skynet.ImageManager;
import org.eclipse.osee.framework.ui.skynet.OseeImage;

/**
 * @author Ryan D. Brooks
 */
public enum AtsImage implements OseeImage {
   RELEASED("orangeR_8_8.gif"),
   NEXT("yellowN_8_8.gif"),
   FAVORITE("favorite.gif"),
   SUBSCRIBED("subscribed.gif"),
   GLOBE("globe.gif"),
   REPORT("report.gif"),
   TASK("task.gif"),
   ACTION("action.gif"),
   ACTIONABLE_ITEM("AI.gif");

   private final String fileName;

   private AtsImage(String fileName) {
      this.fileName = fileName;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.OseeImage#createImageDescriptor()
    */
   @Override
   public ImageDescriptor createImageDescriptor() {
      return ImageManager.createImageDescriptor(AtsPlugin.PLUGIN_ID, "images", fileName);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.OseeImage#getImageKey()
    */
   @Override
   public String getImageKey() {
      return fileName;
   }
}