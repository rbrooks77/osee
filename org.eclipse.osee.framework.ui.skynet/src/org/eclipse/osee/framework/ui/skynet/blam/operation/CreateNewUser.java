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
package org.eclipse.osee.framework.ui.skynet.blam.operation;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.skynet.core.SkynetAuthentication;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.attribute.ConfigurationPersistenceManager;
import org.eclipse.osee.framework.skynet.core.relation.RelationSide;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.ArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.blam.BlamVariableMap;
import org.eclipse.osee.framework.ui.skynet.util.EmailGroupsAndUserGroups;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.util.EmailGroupsAndUserGroups.GroupType;
import org.eclipse.osee.framework.ui.skynet.widgets.XList.XListItem;

/**
 * @author Ryan D. Brooks
 */
public class CreateNewUser extends AbstractBlam {

   private final static List<String> attrNames =
         Arrays.asList(new String[] {"Company", "Company Title", "City", "State", "Phone"});
   private Set<Artifact> groupArts;

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation#runOperation(org.eclipse.osee.framework.ui.skynet.blam.BlamVariableMap, org.eclipse.osee.framework.skynet.core.artifact.Branch, org.eclipse.core.runtime.IProgressMonitor)
    */
   public void runOperation(BlamVariableMap variableMap, IProgressMonitor monitor) throws Exception {
      monitor.beginTask("Create New User", IProgressMonitor.UNKNOWN);

      User user =
            (User) ConfigurationPersistenceManager.getInstance().getArtifactSubtypeDescriptor(User.ARTIFACT_NAME).makeNewArtifact(
                  BranchPersistenceManager.getInstance().getAtsBranch());

      String name = variableMap.getString("Name (Last, First)");
      if (name.equals("")) {
         AWorkbench.popup("ERROR", "Must Enter Name");
         monitor.done();
         return;
      }
      user.setDescriptiveName(name);

      String userId = variableMap.getString("UserId (unique)");
      if (userId.equals("")) {
         AWorkbench.popup("ERROR", "Must Enter UserId");
         monitor.done();
         return;
      }
      try {
         User existingUser = SkynetAuthentication.getInstance().getUserById(userId);
         if (existingUser != null) {
            AWorkbench.popup("ERROR", "User with userId \"" + userId + "\" already exists.");
            monitor.done();
            return;
         }
      } catch (Exception ex) {
         AWorkbench.popup("ERROR", "Invalid UserId - " + ex.getLocalizedMessage());
         monitor.done();
         return;
      }
      user.setSoleStringAttributeValue("User Id", userId);

      String email = variableMap.getString("Email");
      if (email.equals("")) {
         AWorkbench.popup("ERROR", "Must Enter Email");
         monitor.done();
         return;
      }
      user.setSoleStringAttributeValue("Email", email);

      // Process string attribute names
      for (String attrName : attrNames) {
         String value = variableMap.getString(attrName);
         if (!value.equals("")) {
            user.setSoleStringAttributeValue(attrName, value);
         }
      }
      // Add user to selected User Group and Universal Group
      for (XListItem groupNameListItem : variableMap.getCollection(XListItem.class, "Groups")) {
         for (Artifact groupArt : groupArts) {
            if (groupNameListItem.getName().equals(groupArt.getDescriptiveName())) {
               if (groupArt.getArtifactTypeName().equals("Universal Group")) {
                  groupArt.relate(RelationSide.UNIVERSAL_GROUPING__MEMBERS, user);
               } else if (groupArt.getArtifactTypeName().equals("User Group")) {
                  groupArt.relate(RelationSide.Users_User, user);
               }
            }
         }
      }

      user.persist(true);
      ArtifactEditor.editArtifact(user);
      monitor.done();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation#getXWidgetXml()
    */
   public String getXWidgetsXml() {
      String widgetXml = "<xWidgets>" +
      //
      "<XWidget xwidgetType=\"XText\" displayName=\"Name (Last, First)\" required=\"true\"/>" +
      //
      "<XWidget xwidgetType=\"XText\" displayName=\"UserId (unique)\" required=\"true\"/>" +
      //
      "<XWidget xwidgetType=\"XText\" displayName=\"Email\" required=\"true\"/>" +
      //
      "<XWidget xwidgetType=\"XCheckBox\" displayName=\"Active\" required=\"true\" defaultValue=\"true\"/>";

      // Add all rest of attributes to fill
      for (String attrName : attrNames) {
         widgetXml += "<XWidget xwidgetType=\"XText\" displayName=\"" + attrName + "\"/>";
      }
      // Add groups to belong to
      try {
         groupArts =
               EmailGroupsAndUserGroups.getEmailGroupsAndUserGroups(
                     SkynetAuthentication.getInstance().getAuthenticatedUser(), GroupType.Both);
         String groupStr = "";
         for (Artifact art : groupArts) {
            groupStr += art.getDescriptiveName() + ",";
         }
         groupStr = groupStr.replaceFirst(",$", "");
         widgetXml += "<XWidget xwidgetType=\"XList(" + groupStr + ")\" displayName=\"Groups\"/>";
      } catch (SQLException ex) {
         OSEELog.logException(SkynetGuiPlugin.class, ex, true);
      }
      //
      widgetXml += "</xWidgets>";
      return widgetXml;
   }
}