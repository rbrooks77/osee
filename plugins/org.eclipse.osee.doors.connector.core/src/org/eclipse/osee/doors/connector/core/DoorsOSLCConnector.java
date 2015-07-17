/*
 * Copyright (c) 2012 Robert Bosch Engineering and Business Solutions Ltd India. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Eclipse Public License v1.0 which accompanies
 * this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.osee.doors.connector.core;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.doors.connector.core.oauth.DWAOAuthService;
import org.eclipse.ui.PlatformUI;
import org.scribe.model.Token;

/**
 * Handler class to authenticate and get the Doors response
 *
 * @author Chandan Bandemutt
 */
public class DoorsOSLCConnector {

   private static Token accessToken;
   /**
   *
   */
   public static DWAOAuthService service1;

   /**
    * Method to authenticate
    * 
    * @param service : ICTeamOAuthService
    * @param name : username
    * @param password : password
    * @return access Token
    */
   public DoorsArtifact getAuthentication(final DWAOAuthService service, final String name, final String password) {
      DoorsOSLCConnector.service1 = service;
      DoorsArtifact parse = null;
      Token requestToken1 = DoorsOSLCConnector.service1.getRequestToken();
      String authorizeURL = DoorsOSLCConnector.service1.getAuthorizeURL(requestToken1);
      DoorsOSLCConnector.service1.doAuthentication(authorizeURL, requestToken1, name, password);
      accessToken = DoorsOSLCConnector.service1.getAccessToken(requestToken1);
      if (accessToken != null) {
         ServiceProviderCatalogReader catalogReader = new ServiceProviderCatalogReader();
         try {
            parse = catalogReader.parse(service1.getResourceUrl());
         } catch (Exception e) {
            e.printStackTrace();
         }
      } else {
         MessageDialog.openError(PlatformUI.getWorkbench().getDisplay().getActiveShell(), "Error",
            "Failed to obtain valid authentication response");
      }
      return parse;
   }

   /**
    * @param path : OSLC URL to get the response
    * @param queryString : url to get the response
    * @return : returns the response
    */
   public String getCatalogResponse(final String path, final String queryString) {
      String response = null;
      if (DoorsOSLCConnector.accessToken != null) {
         response = DoorsOSLCConnector.service1.getResponse(DoorsOSLCConnector.accessToken, path, queryString);
      }
      return response;
   }
}
