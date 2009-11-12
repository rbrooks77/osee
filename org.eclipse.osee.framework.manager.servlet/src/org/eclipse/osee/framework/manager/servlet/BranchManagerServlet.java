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
package org.eclipse.osee.framework.manager.servlet;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.logging.Level;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.osee.framework.core.IDataTranslationService;
import org.eclipse.osee.framework.core.data.BranchCommitData;
import org.eclipse.osee.framework.core.enums.Function;
import org.eclipse.osee.framework.core.server.OseeHttpServlet;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.manager.servlet.function.ChangeReportFunction;
import org.eclipse.osee.framework.manager.servlet.function.CreateBranchFunction;

/**
 * @author Andrew M Finkbeiner
 */
public class BranchManagerServlet extends OseeHttpServlet {

   private static final long serialVersionUID = 226986283540461526L;

   @Override
   protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
      try {
         Function function = Function.fromString(req.getParameter("function"));
         switch (function) {
            case BRANCH_COMMIT:
               commitBranch(req, resp);
               break;
            case CREATEFULLBRANCH:
               new CreateBranchFunction().processRequest(req, resp);
               break;
            case CHANGE_REPORT:
               new ChangeReportFunction().processRequest(req, resp);
               break;
            default:
               throw new UnsupportedOperationException();
         }
      } catch (Exception ex) {
         OseeLog.log(MasterServletActivator.class, Level.SEVERE, String.format("Branch servlet request error: [%s]",
               req.toString()), ex);
         resp.setStatus(HttpURLConnection.HTTP_INTERNAL_ERROR);
         resp.setContentType("text/plain");
         resp.getWriter().write(Lib.exceptionToString(ex));
      }
      resp.getWriter().flush();
      resp.getWriter().close();
   }

   private void commitBranch(HttpServletRequest req, HttpServletResponse resp) throws Exception {
      PropertyStore propertyStore = new PropertyStore();
      propertyStore.load(req.getInputStream());

      IDataTranslationService service = MasterServletActivator.getInstance().getTranslationService();
      BranchCommitData data = service.convert(propertyStore, BranchCommitData.class);
      IStatus status =
            MasterServletActivator.getInstance().getBranchCommit().commitBranch(new NullProgressMonitor(), data);
      if (status.isOK()) {
         resp.setStatus(HttpServletResponse.SC_ACCEPTED);
         resp.setContentType("text/plain");
         resp.getWriter().write(
               String.format("Commit of [%s] into [%s] was successful.", data.getSourceBranch(),
                     data.getDestinationBranch()));
      } else {
         resp.setStatus(HttpURLConnection.HTTP_INTERNAL_ERROR);
         resp.setContentType("text/plain");
         resp.getWriter().write("Unknown Error during branch creation.");
      }
   }
}
