/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.account.rest.model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.codehaus.jackson.JsonNode;
import org.eclipse.osee.framework.core.util.JsonUtil;

/**
 * @author Angel Avila
 */
public class AccountWebPreferences {

   Map<String, Link> linksMap = new HashMap<>();

   public AccountWebPreferences() {

   }

   public AccountWebPreferences(Map<String, String> teamToPreferences) {
      for (String team : teamToPreferences.keySet()) {
         initPreferences(teamToPreferences.get(team), team);
      }
   }

   private void initPreferences(String string, String team) {
      JsonNode links = JsonUtil.readTree(string).get("links");

      for (Iterator<JsonNode> linkIterator = links.getElements(); linkIterator.hasNext();) {
         JsonNode linkNode = linkIterator.next();
         Link link = new Link();
         if (linkNode.has("name")) {
            link.setName(linkNode.get("name").asText());
         }
         if (linkNode.has("url")) {
            link.setUrl(linkNode.get("url").asText());
         }
         if (linkNode.has("tags")) {
            linkNode.get("tags").forEach(node -> link.addTag(node.asText()));
         }
         link.setTeam(team);
         link.setId(linkNode.get("id").asText());
         linksMap.put(link.getId(), link);
      }
   }

   public AccountWebPreferences(String jsonString, String team) {
      initPreferences(jsonString, team);
   }

   public Map<String, Link> getLinks() {
      return linksMap;
   }

   public void setLinks(Map<String, Link> links) {
      this.linksMap = links;
   }
}