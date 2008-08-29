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
package org.eclipse.osee.framework.server.admin.branch;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.branch.management.ImportOptions;
import org.eclipse.osee.framework.resource.common.io.Files;
import org.eclipse.osee.framework.resource.management.Options;
import org.eclipse.osee.framework.resource.management.ResourceLocator;
import org.eclipse.osee.framework.server.admin.Activator;
import org.eclipse.osee.framework.server.admin.BaseCmdWorker;

/**
 * @author Roberto E. Escobar
 */
public class BranchImportWorker extends BaseCmdWorker {

   private boolean isValidArg(String arg) {
      return arg != null && arg.length() > 0;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.server.admin.BaseCmdWorker#doWork(long)
    */
   @Override
   protected void doWork(long startTime) throws Exception {
      Options options = new Options();
      String arg = null;
      int count = 0;

      List<Integer> branchIds = new ArrayList<Integer>();
      List<File> importFiles = new ArrayList<File>();
      do {
         arg = getCommandInterpreter().nextArgument();
         if (isValidArg(arg)) {
            if (arg.equals("-excludeBaselineTxs")) {
               options.put(ImportOptions.EXCLUDE_BASELINE_TXS.name(), true);
            } else if (arg.equals("-clean")) {
               options.put(ImportOptions.CLEAN_BEFORE_IMPORT.name(), true);
            } else if (arg.equals("-allAsRootBranches")) {
               options.put(ImportOptions.ALL_AS_ROOT_BRANCHES.name(), true);
            } else if (count == 0 && !arg.startsWith("-")) {
               importFiles.add(new File(arg));
            } else {
               branchIds.add(new Integer(arg));
            }
            count++;
         }
      } while (isValidArg(arg));

      if (importFiles.isEmpty()) {
         throw new IllegalArgumentException("Files to import were not specified");
      }

      for (File file : importFiles) {
         if (file == null || !file.exists() || !file.canRead()) {
            throw new IllegalArgumentException(String.format("File was not accessible: [%s]", file));
         } else if (!Files.getExtension(file.getAbsolutePath()).equals("zip")) {
            throw new IllegalArgumentException(String.format("Invalid File: [%s]", file));
         }
      }

      for (File fileToImport : importFiles) {
         Activator.getInstance().getBranchExchange().importBranch(new ResourceLocator(fileToImport.toURI()), options,
               branchIds);
      }
   }
}
