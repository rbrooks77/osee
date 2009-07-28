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

package org.eclipse.osee.framework.database.init;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.database.init.internal.DatabaseInitActivator;
import org.eclipse.osee.framework.plugin.core.util.ExtensionDefinedObjects;

/**
 * @author Andrew M. Finkbeiner
 */
public class GroupSelection {
   private static final GroupSelection instance = new GroupSelection();
   private final Map<String, List<String>> initGroups = new HashMap<String, List<String>>();

   private boolean wasInitialized;

   /**
    * @param initGroups
    */
   private GroupSelection() {
      super();
      wasInitialized = false;
   }

   public static GroupSelection getInstance() {
      instance.populateDbInitChoices();
      return instance;
   }

   private synchronized void populateDbInitChoices() {
      if (!wasInitialized) {
         wasInitialized = true;
         ExtensionDefinedObjects<IAddDbInitChoice> contributions =
               new ExtensionDefinedObjects<IAddDbInitChoice>(
                     DatabaseInitActivator.getBundle().getSymbolicName() + ".DatabaseInitializationConfiguration",
                     "DatabaseInitializationConfiguration", "classname");
         for (IAddDbInitChoice dbInitChoice : contributions.getObjects()) {
            dbInitChoice.addDbInitChoice(this);
         }
      }
   }

   public void addChoice(String listName, List<String> dbInitTasks, boolean bareBones) {
      List<String> initTasks = new ArrayList<String>();
      initTasks.add("org.eclipse.osee.framework.database.init.SkynetDbInit");
      dbInitTasks.addAll(0, initTasks);
      dbInitTasks.add("org.eclipse.osee.framework.database.init.PostDbUserCleanUp");
      dbInitTasks.add("org.eclipse.osee.framework.database.init.SkynetDbBranchDataImport");
      dbInitTasks.add("org.eclipse.osee.framework.database.init.PostDbInitializationProcess");
      initGroups.put(listName, dbInitTasks);
   }

   public List<String> getChoices() {
      List<String> choices = new ArrayList<String>(initGroups.keySet());
      Collections.sort(choices);
      return choices;
   }

   public List<String> getDbInitTasksByChoiceEntry(String choice) {
      return initGroups.get(choice);
   }
}