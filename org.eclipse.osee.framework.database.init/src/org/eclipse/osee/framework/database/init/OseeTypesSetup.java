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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeWrappedException;
import org.eclipse.osee.framework.database.init.internal.DatabaseInitActivator;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.ExtensionDefinedObjects;
import org.eclipse.osee.framework.plugin.core.util.ExtensionPoints;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.importing.IOseeTypesHandler;
import org.osgi.framework.Bundle;
import org.xml.sax.SAXException;

/**
 * This class provides necessary functionality for branches to be loaded with SkynetDbTypes through their extension
 * points. Creation, adding skynet types and initializing a new branch should be done through
 * BranchManager.createRootBranch.
 * 
 * @author Andrew M. Finkbeiner
 * @author Donald G. Dunne
 * @see BranchManager#createTopLevelBranch(String, String, String, Collection, boolean)
 */
public class OseeTypesSetup {
   private static final String DECLARING_PLUGIN_ID = "org.eclipse.osee.framework.skynet.core";
   private static final String OSEE_TYPES_ELEMENT = "OseeTypes";
   private static final String OSEE_TYPES_EXTENSION_ID = DECLARING_PLUGIN_ID + "." + OSEE_TYPES_ELEMENT;

   private static final String OSEE_TYPES_HANDLER = "OseeTypesHandler";
   private static final String OSEE_TYPES_HANDLER_EXTENSION_ID = DECLARING_PLUGIN_ID + "." + OSEE_TYPES_HANDLER;

   private final ExtensionDefinedObjects<IOseeTypesHandler> extensionObjects;

   public OseeTypesSetup() {
      extensionObjects =
            new ExtensionDefinedObjects<IOseeTypesHandler>(OSEE_TYPES_HANDLER_EXTENSION_ID, OSEE_TYPES_HANDLER,
                  "classname");
   }

   public void execute(Collection<String> uniqueIdsToImport) throws OseeCoreException {
      try {
         executeTypesImport(ExtensionPoints.getExtensionsByUniqueId(OSEE_TYPES_EXTENSION_ID, uniqueIdsToImport));
      } catch (IOException ex) {
         throw new OseeWrappedException(ex);
      } catch (SAXException ex) {
         throw new OseeWrappedException(ex);
      }
   }

   private void executeTypesImport(List<IExtension> extensionIds) throws IOException, SAXException, OseeCoreException {
      String userHome = System.getProperty("user.home");
      File file = new File(userHome, "oseetypes.osee");
      Writer writer = null;
      try {
         writer = new FileWriter(file);
         for (IExtension extension : extensionIds) {
            IConfigurationElement[] elements = extension.getConfigurationElements();
            for (IConfigurationElement el : elements) {
               if (el.getName().equals("OseeTypes")) {
                  String resource = el.getAttribute("resource");
                  Bundle bundle = Platform.getBundle(el.getContributor().getName());
                  URL url = bundle.getEntry(resource);
                  OseeLog.log(DatabaseInitActivator.class, Level.INFO, String.format("Importing [%s] from [%s]",
                        resource, url != null ? url.getPath() : "url was null"));
                  String oseeTypeFragment = Lib.inputStreamToString(url.openStream());
                  oseeTypeFragment = oseeTypeFragment.replaceAll("import\\s+\"", "// import \"");
                  writer.write("\n");
                  writer.write("//////////////     ");
                  writer.write(extension.getUniqueIdentifier());
                  writer.write("\n");
                  writer.write("\n");
                  writer.write(oseeTypeFragment);
               } else {
                  throw new OseeArgumentException("expecting a single xml element called OseeTypes");
               }
            }
         }
      } finally {
         if (writer != null) {
            writer.close();
         }
      }

      URL url = file.toURI().toURL();
      IOseeTypesHandler handler = getHandler(file.getAbsolutePath(), url);
      if (handler != null) {
         handler.execute(new NullProgressMonitor(), null, url);
      } else {
         OseeLog.log(DatabaseInitActivator.class, Level.SEVERE, String.format(
               "Unable to find handler for [%s] - handlers - %s", file.getAbsolutePath(),
               this.extensionObjects.getObjects()));
      }
      file.delete();
   }

   private IOseeTypesHandler getHandler(String resource, URL url) {
      IOseeTypesHandler toReturn = null;
      for (IOseeTypesHandler handler : extensionObjects.getObjects()) {
         if (handler.isApplicable(resource, url)) {
            toReturn = handler;
            break;
         }
      }
      return toReturn;
   }

}
