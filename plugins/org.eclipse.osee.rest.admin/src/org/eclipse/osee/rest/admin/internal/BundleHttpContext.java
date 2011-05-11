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
package org.eclipse.osee.rest.admin.internal;

import java.net.URL;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.osgi.framework.Bundle;
import org.osgi.service.http.HttpContext;

/**
 * @author Roberto E. Escobar
 */
public class BundleHttpContext implements HttpContext {

   private final Bundle bundle;

   public BundleHttpContext(Bundle bundle) {
      this.bundle = bundle;
   }

   @Override
   public URL getResource(String name) {
      return bundle.getEntry(name);
   }

   @Override
   public String getMimeType(String name) {
      return null;
   }

   @Override
   public boolean handleSecurity(HttpServletRequest request, HttpServletResponse response) {
      // Assume the container has already performed authentication
      return true;
   }

}