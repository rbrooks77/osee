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
package org.eclipse.osee.framework.database;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.osee.framework.database.initialize.LaunchOseeDbConfigClient;
import org.eclipse.osee.framework.db.connection.core.OseeApplicationServer;

public class ConfigDatabase implements IApplication {

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.equinox.app.IApplication#start(org.eclipse.equinox.app.IApplicationContext)
    */
   public Object start(IApplicationContext context) throws Exception {
      if (!OseeApplicationServer.isApplicationServerAlive()) {
         System.out.println(String.format("We cannot establish a connection to the Application server [%s]",
               OseeApplicationServer.getOseeApplicationServer()));
         return EXIT_OK;
      }
      LaunchOseeDbConfigClient.main(null);
      // keepAlive = new Object();
      // synchronized (keepAlive) {
      // try {
      // keepAlive.wait();
      // } catch (InterruptedException e) {
      // e.printStackTrace();
      // }
      // }
      return EXIT_OK;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.equinox.app.IApplication#stop()
    */
   public void stop() {
   }

}
