/*
 * Created on Jul 20, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.access;

import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;

public interface IAccessControllable {
   public Branch getAccessControlBranch();

   public PermissionEnum getUserPermission(Artifact subject, PermissionEnum permission);
}
