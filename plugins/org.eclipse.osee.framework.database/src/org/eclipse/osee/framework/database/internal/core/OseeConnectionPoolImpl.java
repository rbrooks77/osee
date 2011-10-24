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
package org.eclipse.osee.framework.database.internal.core;

import java.sql.Connection;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.framework.database.core.IConnectionFactory;
import org.eclipse.osee.framework.database.core.OseeConnection;
import org.eclipse.osee.framework.database.internal.Activator;
import org.eclipse.osee.framework.logging.OseeLog;

public class OseeConnectionPoolImpl {
   private static final int MAX_CONNECTIONS_PER_CLIENT = Math.max(8, 2 * Runtime.getRuntime().availableProcessors());
   private final List<OseeConnectionImpl> connections = new CopyOnWriteArrayList<OseeConnectionImpl>();
   private final String dbUrl;
   private final Properties properties;
   private final IConnectionFactory connectionFactory;

   public OseeConnectionPoolImpl(IConnectionFactory connectionFactory, String dbUrl, Properties properties) {
      this.connectionFactory = connectionFactory;
      this.dbUrl = dbUrl;
      this.properties = properties;
   }

   public synchronized boolean hasOpenConnection() {
      return connections.size() > 0;
   }

   /**
    * at a minimum this should be called on jvm shutdown
    */
   public synchronized void closeConnections() {
      for (OseeConnection connection : connections) {
         connection.close();
      }
      connections.clear();
   }

   synchronized void removeConnection(OseeConnection conn) {
      connections.remove(conn);
   }

   public synchronized OseeConnectionImpl getConnection() throws OseeCoreException {
      for (OseeConnectionImpl connection : connections) {
         if (connection.lease()) {
            return connection;
         }
      }

      if (connections.size() >= MAX_CONNECTIONS_PER_CLIENT) {
         throw new OseeDataStoreException(
            "This client has reached the maximum number of allowed simultaneous database connections of %d.",
            MAX_CONNECTIONS_PER_CLIENT);
      }
      OseeConnectionImpl connection = getOseeConnection();
      connections.add(connection);
      OseeLog.logf(Activator.class, Level.INFO, "DbConnection: [%s] - [%d]", dbUrl, connections.size());
      return connection;
   }

   private OseeConnectionImpl getOseeConnection() throws OseeCoreException {
      try {
         Connection connection = connectionFactory.getConnection(properties, dbUrl);
         connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
         return new OseeConnectionImpl(connection, this);
      } catch (Exception ex) {
         OseeExceptions.wrapAndThrow(ex);
         return null; // unreachable since wrapAndThrow() always throws an exception
      }
   }

   synchronized void returnConnection(OseeConnectionImpl connection) {
      try {
         if (connection.isClosed()) {
            removeConnection(connection);
         } else {
            connection.expireLease();
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
         removeConnection(connection);
      }
   }

   synchronized void releaseUneededConnections() throws OseeCoreException {
      for (OseeConnectionImpl connection : connections) {
         if (connection.isStale()) {
            connection.destroy();
         }
      }
   }
}