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
package org.eclipse.osee.framework.ui.skynet.dbHealth;

import java.util.HashSet;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;

/**
 * @author Theron Virgin
 */
public class AttributeTxCurrent extends DatabaseHealthTask {
   private HashSet<LocalTxData> multipleSet = null;
   private HashSet<Pair<Integer, Integer>> noneSet = null;

   public String getFixTaskName() {
      return "Fix TX_Current Attribute Errors";
   }

   public String getVerifyTaskName() {
      return "Check for TX_Current Attribute Errors";
   }

   public void run(VariableMap variableMap, IProgressMonitor monitor, Operation operation, StringBuilder builder, boolean showDetails) throws Exception {
      monitor.beginTask("Verify TX_Current Attribute Errors", 100);
      String[] columnHeaders = new String[] {"Count", "Attr id", "Branch id"};
      StringBuffer sbFull = new StringBuffer(AHTML.beginMultiColumnTable(100, 1));
      if (showDetails) {
         sbFull.append(AHTML.addHeaderRowMultiColumnTable(columnHeaders));
         sbFull.append(AHTML.addRowSpanMultiColumnTable("Attributes with no tx_current set", columnHeaders.length));
      }
      if (operation.equals(Operation.Verify) || noneSet == null) {
         noneSet = HealthHelper.getNoTxCurrentSet("attr_id", "osee_attribute", builder, " Attributes");
         monitor.worked(15);
         if (monitor.isCanceled()) return;
      }
      if (showDetails) {
         HealthHelper.dumpDataNone(sbFull, noneSet);
         columnHeaders = new String[] {"Count", "Attr id", "Branch id", "Num TX_Currents"};
         sbFull.append(AHTML.addHeaderRowMultiColumnTable(columnHeaders));
         sbFull.append(AHTML.addRowSpanMultiColumnTable("Attributes with multiple tx_currents set",
               columnHeaders.length));
      }
      if (operation.equals(Operation.Verify) || multipleSet == null) {
         //Multiple TX Currents Set
         multipleSet = HealthHelper.getMultipleTxCurrentSet("attr_id", "osee_attribute", builder, " Attributes");
      }
      if (showDetails) {
         HealthHelper.dumpDataMultiple(sbFull, multipleSet);
      }

      if (operation.equals(Operation.Fix)) {
         /** Duplicate TX_current Cleanup **/
         monitor.worked(10);
         monitor.subTask("Cleaning up multiple Tx_currents");
         HealthHelper.cleanMultipleTxCurrent("attr_id", "osee_attribute", builder, multipleSet);
         monitor.worked(20);
         monitor.subTask("Cleaning up multiple Tx_currents");
         HealthHelper.cleanNoTxCurrent("attr_id", "osee_attribute", builder, noneSet);
         multipleSet = null;
         noneSet = null;
      }

      if (showDetails) {
         HealthHelper.endTable(sbFull, getVerifyTaskName());
      }
   }

}
