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
package org.eclipse.osee.framework.skynet.core;

import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IArtifactToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTokens;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;

/**
 * @author Roberto E. Escobar
 */
public class OseeGroup {

   private Artifact groupArtifact;
   private final Map<IArtifactToken, Boolean> temporaryOverride = new HashMap<>();
   private final IArtifactToken token;

   public OseeGroup(IArtifactToken token) {
      this.token = token;
      this.groupArtifact = null;
   }

   /**
    * @return Returns the group.
    */
   public Artifact getGroupArtifact() throws OseeCoreException {
      checkGroupExists();
      return groupArtifact;
   }

   /**
    * This does not persist the newly created relation that is the callers responsibility.
    */
   public void addMember(User user) throws OseeCoreException {
      getGroupArtifact().addRelation(CoreRelationTypes.Users_User, user);
   }

   /**
    * Determines whether the user is a member of this group
    *
    * @param user to check
    * @return whether the user is a member of this group
    */
   public boolean isMember(User user) throws OseeCoreException {
      return isTemporaryOverride(user) || getGroupArtifact().isRelated(CoreRelationTypes.Users_User, user);
   }

   /**
    * Determines whether the current user is a member of this group
    *
    * @return whether the current user is a member of this group
    */
   public boolean isCurrentUserMember() throws OseeCoreException {
      return isMember(UserManager.getUser());
   }

   public boolean isTemporaryOverride(User user) {
      if (temporaryOverride.get(token) != null) {
         return temporaryOverride.get(token);
      }
      return false;
   }

   /**
    * Allow user to temporarily override admin
    */
   public void setTemporaryOverride(boolean member) {
      temporaryOverride.put(token, member);
   }

   public void removeTemporaryOverride() {
      temporaryOverride.remove(token);
   }

   private void checkGroupExists() throws OseeCoreException {
      if (groupArtifact == null) {
         groupArtifact = getOrCreateGroupArtifact(token);
      }
   }

   private Artifact getOrCreateGroupArtifact(IArtifactToken token) throws OseeCoreException {
      Artifact groupArtifact = null;
      try {
         groupArtifact = ArtifactQuery.getArtifactFromToken(token, COMMON);
      } catch (ArtifactDoesNotExist ex) {
         // do nothing
      }
      if (groupArtifact == null) {
         Artifact userGroupsFolder = getOrCreateUserGroupsFolder(COMMON);
         groupArtifact = ArtifactTypeManager.addArtifact(token, COMMON);
         userGroupsFolder.addChild(groupArtifact);
      }
      return groupArtifact;
   }

   private Artifact getOrCreateUserGroupsFolder(BranchId branch) throws OseeCoreException {
      Artifact usersGroupFolder = null;
      try {
         usersGroupFolder = ArtifactQuery.getArtifactFromId(CoreArtifactTokens.UserGroups.getUuid(), branch);
      } catch (ArtifactDoesNotExist ex) {
         // do nothing
      }
      if (usersGroupFolder == null) {
         Artifact root = OseeSystemArtifacts.getDefaultHierarchyRootArtifact(branch);
         if (root.hasChild(CoreArtifactTokens.UserGroups.getName())) {
            usersGroupFolder = root.getChild(CoreArtifactTokens.UserGroups.getName());
         } else {
            usersGroupFolder = ArtifactTypeManager.addArtifact(CoreArtifactTokens.UserGroups, branch);
            root.addChild(usersGroupFolder);
         }
      }
      return usersGroupFolder;
   }

   @Override
   public String toString() {
      return "OseeGroup [groupName=" + token.getName() + "]";
   }
}