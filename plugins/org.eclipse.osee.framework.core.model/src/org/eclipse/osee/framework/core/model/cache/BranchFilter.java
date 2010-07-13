/*
 * Created on Apr 1, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.core.model.cache;

import org.eclipse.osee.framework.core.enums.BranchArchivedState;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.IBasicArtifact;

/**
 * @author Ryan D. Brooks
 */
public class BranchFilter {
   private final BranchArchivedState archivedState;
   private final BranchType[] branchTypes;
   private IBasicArtifact<?> associatedArtifact;

   private BranchState[] branchStates;

   private BranchState[] negatedBranchStates;

   public BranchFilter(BranchArchivedState archivedState, BranchType... branchTypes) {
      super();
      this.archivedState = archivedState;
      this.branchTypes = branchTypes;
   }

   public boolean matches(Branch branch) throws OseeCoreException {
      if (associatedArtifact != null && !branch.getAssociatedArtifactId().equals(associatedArtifact.getArtId())) {
         return false;
      }
      if (archivedState != null && !branch.getArchiveState().matches(archivedState)) {
         return false;
      }
      if (branchTypes != null && !branch.getBranchType().isOfType(branchTypes)) {
         return false;
      }
      if (branchStates != null && !branch.getBranchState().matches(branchStates)) {
         return false;
      }
      if (negatedBranchStates != null && branch.getBranchState().matches(negatedBranchStates)) {
         return false;
      }
      return true;
   }

   public void setAssociatedArtifact(IBasicArtifact<?> associatedArtifact) {
      this.associatedArtifact = associatedArtifact;
   }

   public void setBranchStates(BranchState... branchStates) {
      this.branchStates = branchStates;
   }

   public void setNegatedBranchStates(BranchState... negatedBranchStates) {
      this.negatedBranchStates = negatedBranchStates;
   }
}