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
package org.eclipse.osee.ats.editor;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.osgi.framework.Bundle;

/**
 * @author Donald G. Dunne
 */
public class AtsStateItems {

   private static List<IAtsStateItem> stateItems = new ArrayList<IAtsStateItem>();

   public List<IAtsStateItem> getStateItems(String stateId) throws OseeCoreException {
      loadAllStateItems();
      List<IAtsStateItem> items = new ArrayList<IAtsStateItem>();
      for (IAtsStateItem item : stateItems)
         if (item.getIds().contains(AtsStateItem.ALL_STATE_IDS) || item.getIds().contains(stateId)) items.add(item);
      return items;
   }

   public List<IAtsStateItem> getCurrentPageStateItems(SMAManager smaMgr) throws OseeCoreException {
      return getStateItems(smaMgr.getWorkPageDefinition().getId());
   }

   @SuppressWarnings( {"unchecked"})
   private void loadAllStateItems() {
      if (stateItems.size() > 0) return;
      IExtensionPoint point = Platform.getExtensionRegistry().getExtensionPoint("org.eclipse.osee.ats.AtsStateItem");
      if (point == null) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, "Can't access AtsStateItem extension point");
         return;
      }
      IExtension[] extensions = point.getExtensions();
      for (IExtension extension : extensions) {
         IConfigurationElement[] elements = extension.getConfigurationElements();
         String classname = null;
         String bundleName = null;
         for (IConfigurationElement el : elements) {
            if (el.getName().equals("AtsStateItem")) {
               classname = el.getAttribute("classname");
               bundleName = el.getContributor().getName();
               if (classname != null && bundleName != null) {
                  Bundle bundle = Platform.getBundle(bundleName);
                  try {
                     Class taskClass = bundle.loadClass(classname);
                     Object obj = taskClass.newInstance();
                     if (obj == null) {
                        OseeLog.log(AtsPlugin.class, Level.SEVERE,
                              "Error Instantiating AtsStateItem extension \"" + classname + "\"", null);
                     } else {
                        stateItems.add((IAtsStateItem) obj);
                     }
                  } catch (Exception ex) {
                     OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, "Error loading AtsStateItem extension", ex);
                  }
               }
            }
         }
      }
   }

   /**
    * @return the stateItems
    */
   public static List<IAtsStateItem> getAllStateItems() {
      return stateItems;
   }
}
