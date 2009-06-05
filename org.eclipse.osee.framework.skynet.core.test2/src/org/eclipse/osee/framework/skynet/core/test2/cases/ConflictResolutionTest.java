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

package org.eclipse.osee.framework.skynet.core.test2.cases;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import java.util.Collection;
import org.eclipse.osee.framework.core.enums.ConflictStatus;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.framework.skynet.core.conflict.ArtifactConflict;
import org.eclipse.osee.framework.skynet.core.conflict.AttributeConflict;
import org.eclipse.osee.framework.skynet.core.conflict.Conflict;
import org.eclipse.osee.framework.skynet.core.conflict.RelationConflict;
import org.eclipse.osee.framework.skynet.core.revision.ConflictManagerInternal;
import org.eclipse.osee.framework.skynet.core.status.EmptyMonitor;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionIdManager;

/**
 * @author Theron Virgin
 */
public class ConflictResolutionTest {

   /**
    * @param name
    */
   public ConflictResolutionTest(String name) {
   }

   @org.junit.Test
public void testResolveConflicts() {
      SevereLoggingMonitor monitorLog = new SevereLoggingMonitor();
      OseeLog.registerLoggerListener(monitorLog);
      try {
         Collection<Conflict> conflicts =
               ConflictManagerInternal.getConflictsPerBranch(ConflictTestManager.getSourceBranch(),
                     ConflictTestManager.getDestBranch(), TransactionIdManager.getStartEndPoint(
                           ConflictTestManager.getSourceBranch()).getKey(), new EmptyMonitor());
         int whichChange = 1;

         for (Conflict conflict : conflicts) {
            if (conflict instanceof ArtifactConflict && ((ArtifactConflict) conflict).statusNotResolvable()) {
               ((ArtifactConflict) conflict).revertSourceArtifact();
            } else if (conflict instanceof AttributeConflict) {
               ConflictTestManager.resolveAttributeConflict((AttributeConflict) conflict);
               conflict.setStatus(ConflictStatus.RESOLVED);
            } else if (conflict instanceof RelationConflict) {
               fail("Relation Conflicts are not supported yet");
            }
            whichChange++;
         }

         conflicts =
               ConflictManagerInternal.getConflictsPerBranch(ConflictTestManager.getSourceBranch(),
                     ConflictTestManager.getDestBranch(), TransactionIdManager.getStartEndPoint(
                           ConflictTestManager.getSourceBranch()).getKey(), new EmptyMonitor());

         for (Conflict conflict : conflicts) {
            assertTrue(
                  "This conflict was not found to be resolved ArtId = " + conflict.getArtId() + " " + conflict.getSourceDisplayData(),
                  conflict.statusResolved() || conflict.statusInformational());

         }
      } catch (Exception ex) {
         fail(ex.getMessage());
      }
      assertTrue(String.format("%d SevereLogs during test.", monitorLog.getAllLogs().size()),
            monitorLog.getAllLogs().size() == 0);
   }
}
