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

package org.eclipse.osee.ats.util.Import;

import java.io.File;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Donald G. Dunne
 */
public class TaskImportJob extends Job {
   private final File file;
   private ExcelAtsTaskArtifactExtractor atsTaskExtractor;

   public TaskImportJob(File file, ExcelAtsTaskArtifactExtractor atsTaskExtractor) {
      super("Importing Tasks");
      this.file = file;
      this.atsTaskExtractor = atsTaskExtractor;
   }

   public IStatus run(final IProgressMonitor monitor) {
      IStatus toReturn = Status.CANCEL_STATUS;
      try {
         atsTaskExtractor.setMonitor(monitor);
         monitor.beginTask("Importing Tasks", 0);
         if (file != null && file.isFile()) {
            atsTaskExtractor.discoverArtifactAndRelationData(file, AtsUtil.getAtsBranch());
         } else {
            throw new OseeArgumentException("All files passed must be a file");
         }
         toReturn = Status.OK_STATUS;
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
         toReturn = new Status(Status.ERROR, AtsPlugin.PLUGIN_ID, -1, ex.getMessage(), ex);
      } finally {
         monitor.done();
      }
      return toReturn;
   }
}
