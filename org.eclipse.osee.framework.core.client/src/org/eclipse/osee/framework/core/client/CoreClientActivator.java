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
package org.eclipse.osee.framework.core.client;

import org.eclipse.core.runtime.Plugin;
import org.eclipse.osee.framework.core.client.internal.ClientDatabaseProvider;
import org.eclipse.osee.framework.core.client.server.HttpServer;
import org.eclipse.osee.framework.database.core.IDatabaseInfoProvider;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * The activator class controls the plug-in life cycle
 */
public class CoreClientActivator extends Plugin {

   public static final String PLUGIN_ID = "org.eclipse.osee.framework.core.client";
   private static CoreClientActivator instance;
   private BundleContext context;
   private ServiceRegistration registration;

   public CoreClientActivator() {
      instance = this;
      context = null;
   }

   @Override
   public void start(BundleContext context) throws Exception {
      super.start(context);
      this.context = context;

      registration = context.registerService(IDatabaseInfoProvider.class.getName(), new ClientDatabaseProvider(), null);

      HttpServer.startServer(1);
   }

   @Override
   public void stop(BundleContext context) throws Exception {
      super.stop(context);
      registration.unregister();
      HttpServer.stopServer();

      ClientSessionManager.releaseSession();
      context = null;
   }

   public static CoreClientActivator getInstance() {
      return instance;
   }

   public static BundleContext getBundleContext() {
      return getInstance().context;
   }
}