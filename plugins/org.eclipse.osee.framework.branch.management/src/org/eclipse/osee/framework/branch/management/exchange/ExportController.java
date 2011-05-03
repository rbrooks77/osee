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
package org.eclipse.osee.framework.branch.management.exchange;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.osee.framework.branch.management.ExportOptions;
import org.eclipse.osee.framework.branch.management.IExchangeTaskListener;
import org.eclipse.osee.framework.branch.management.exchange.export.AbstractDbExportItem;
import org.eclipse.osee.framework.branch.management.exchange.export.AbstractExportItem;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.framework.core.server.ServerThreads;
import org.eclipse.osee.framework.database.core.DbTransaction;
import org.eclipse.osee.framework.database.core.ExportImportJoinQuery;
import org.eclipse.osee.framework.database.core.JoinUtility;
import org.eclipse.osee.framework.database.core.OseeConnection;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.resource.management.Options;

/**
 * @author Roberto E. Escobar
 */
final class ExportController extends DbTransaction implements IExchangeTaskListener {
   private static final String ZIP_EXTENSION = ".zip";

   private String exportName;
   private final Options options;
   private final List<Integer> branchIds;
   private ExportImportJoinQuery joinQuery;
   private ExecutorService executorService;
   private final List<String> errorList = new CopyOnWriteArrayList<String>();
   private final OseeServices oseeServices;

   ExportController(OseeServices oseeServices, String exportName, Options options, List<Integer> branchIds) throws OseeCoreException {
      if (branchIds.isEmpty()) {
         throw new OseeArgumentException("No branch selected for export.");
      }
      this.oseeServices = oseeServices;
      this.exportName = exportName;
      this.options = options;
      this.branchIds = branchIds;
      this.joinQuery = JoinUtility.createExportImportJoinQuery();
   }

   public String getExchangeFileName() {
      return this.exportName;
   }

   public void setExchangeFileName(String value) {
      this.exportName = value;
   }

   public int getExportQueryId() {
      return joinQuery != null ? joinQuery.getQueryId() : -1;
   }

   private void cleanUp(OseeConnection connection, List<AbstractExportItem> taskList) {
      for (AbstractExportItem exportItem : taskList) {
         exportItem.cleanUp();
      }
      try {
         if (joinQuery != null) {
            joinQuery.delete(connection);
            joinQuery = null;
         }
      } catch (OseeCoreException ex) {
         onException("Export Clean-Up", ex);
      }
      this.executorService.shutdown();
      this.executorService = null;
   }

   private File createTempFolder() {
      File rootDirectory = ExchangeUtil.createTempFolder();
      if (!Strings.isValid(getExchangeFileName())) {
         setExchangeFileName(rootDirectory.getName());
      }
      return rootDirectory;
   }

   private void setUp(OseeConnection connection, List<AbstractExportItem> taskList, File tempFolder) throws OseeCoreException {
      joinQuery = JoinUtility.createExportImportJoinQuery();
      for (int branchId : branchIds) {
         joinQuery.add((long) branchId, -1L);
      }
      joinQuery.store(connection);

      long maxTx =
         oseeServices.getDatabaseService().runPreparedQueryFetchObject(connection, -1L, ExchangeDb.GET_MAX_TX);
      long userMaxTx = ExchangeDb.getMaxTransaction(options);
      if (userMaxTx == Long.MIN_VALUE || userMaxTx > maxTx) {
         options.put(ExportOptions.MAX_TXS.name(), Long.toString(maxTx));
      }

      for (AbstractExportItem exportItem : taskList) {
         exportItem.setOptions(options);
         exportItem.setWriteLocation(tempFolder);
         if (exportItem instanceof AbstractDbExportItem) {
            AbstractDbExportItem exportItem2 = (AbstractDbExportItem) exportItem;
            exportItem2.setJoinQueryId(joinQuery.getQueryId());
            exportItem2.setConnection(connection);
         }
         exportItem.addExportListener(this);
      }

      executorService =
         Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(),
            ServerThreads.createNewThreadFactory("branch.export.worker"));
   }

   @Override
   protected void handleTxWork(OseeConnection connection) throws OseeCoreException {
      long startTime = System.currentTimeMillis();
      List<AbstractExportItem> taskList = ExchangeDb.createTaskList(oseeServices);
      try {
         File tempFolder = createTempFolder();
         setUp(connection, taskList, tempFolder);

         sendTasksToExecutor(taskList, tempFolder);

         String zipTargetName = getExchangeFileName() + ZIP_EXTENSION;
         if (this.options.getBoolean(ExportOptions.COMPRESS.name())) {
            OseeLog.log(this.getClass(), Level.INFO,
               String.format("Compressing Branch Export Data - [%s]", zipTargetName));
            File zipTarget = new File(tempFolder.getParent(), zipTargetName);
            Lib.compressDirectory(tempFolder, zipTarget.getAbsolutePath(), true);
            OseeLog.log(this.getClass(), Level.INFO,
               String.format("Deleting Branch Export Temp Folder - [%s]", tempFolder));
            Lib.deleteDir(tempFolder);
         } else {
            File target = new File(tempFolder.getParent(), getExchangeFileName());
            if (!target.equals(tempFolder)) {
               if (!tempFolder.renameTo(target)) {
                  OseeLog.format(this.getClass(), Level.INFO, "Unable to move [%s] to [%s]",
                     tempFolder.getAbsolutePath(), target.getAbsolutePath());
               }
            }
         }
      } catch (Exception ex) {
         OseeExceptions.wrapAndThrow(ex);
      } finally {
         cleanUp(connection, taskList);
      }
      OseeLog.log(
         this.getClass(),
         Level.INFO,
         String.format("Exported [%s] branch%s in [%s]", branchIds.size(), branchIds.size() != 1 ? "es" : "",
            Lib.getElapseString(startTime)));
   }

   private void sendTasksToExecutor(List<AbstractExportItem> taskList, final File exportFolder) throws InterruptedException, ExecutionException, OseeCoreException {
      List<Future<?>> futures = new ArrayList<Future<?>>();
      for (AbstractExportItem exportItem : taskList) {
         futures.add(this.executorService.submit(exportItem));
      }

      futures.add(this.executorService.submit(new Runnable() {
         @Override
         public void run() {
            try {
               oseeServices.getModelingService().exportOseeTypes(new NullProgressMonitor(),
                  new FileOutputStream(new File(exportFolder, "OseeModel.osee")));
            } catch (Exception ex) {
               onException("model export", ex);
            }
         }
      }));

      for (Future<?> future : futures) {
         future.get();
         if (!this.errorList.isEmpty()) {
            throw new OseeCoreException(errorList.toString());
         }
      }
   }

   @Override
   public void onException(String name, Throwable ex) {
      errorList.add(Lib.exceptionToString(ex));
   }

   @Override
   synchronized public void onExportItemCompleted(String name, long timeToProcess) {
      System.out.println(String.format("Exported: [%s] in [%s] ms", name, timeToProcess));
   }

   @Override
   public void onExportItemStarted(String name) {
   }
}
