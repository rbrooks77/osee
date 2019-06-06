/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.dbHealth;

import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.GammaId;

public class LocalRelationLink {
   public int modType;
   public BranchId branch;
   public Long transIdForArtifactDeletion;
   public GammaId gammaId;
   public Long relTransId;
   public Long relLinkId;
   public Long aArtId;
   public Long bArtId;
   public Long commitTrans;

   public LocalRelationLink(Long relLinkId, GammaId gammaId, Long transactionId, BranchId branch, Long aArtId, Long bArtId, Long transIdForArtifactDeletion, Long commitTrans, Integer modType) {
      this.aArtId = aArtId;
      this.bArtId = bArtId;
      this.branch = branch;
      this.gammaId = gammaId;
      this.relLinkId = relLinkId;
      this.relTransId = transactionId;
      this.transIdForArtifactDeletion = transIdForArtifactDeletion;
      this.commitTrans = commitTrans;
      this.modType = modType;
   }

   @Override
   public String toString() {
      return String.format("%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s", gammaId, relTransId, relLinkId, branch, aArtId,
         bArtId, transIdForArtifactDeletion, commitTrans, modType);
   }
}