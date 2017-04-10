/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.jdk.core.util.io.xml;

import java.io.IOException;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Ryan D. Brooks
 */
public final class TextSheetWriter extends AbstractSheetWriter {
   private Appendable out;
   private boolean wasDataAdded;
   private final String lineSeparator;
   private int columnCount;

   public TextSheetWriter() {
      lineSeparator = System.getProperty("line.separator", "\r\n");
   }

   public boolean hasData() {
      return wasDataAdded;
   }

   @Override
   protected void startRow() {
      // do nothing
   }

   @Override
   protected void writeCellText(Object data, int cellIndex) throws IOException {
      if (data instanceof String) {
         String dataStr = (String) data;
         if (Strings.isValid(dataStr)) {
            out.append(dataStr);
         }
         if (cellIndex < columnCount - 1) {
            out.append("\t");
         }
         wasDataAdded = true;
      }
   }

   @Override
   protected void writeEndRow() throws IOException {
      out.append(lineSeparator);
      wasDataAdded = true;
   }

   @Override
   public void endSheet() {
      out = null;
   }

   @Override
   public void endWorkbook() {
      // do nothing
   }

   @Override
   public void startSheet(String worksheetName, int columnCount) {
      this.columnCount = columnCount;
   }

   @Override
   public void startSheet(String worksheetName, int columnCount, Appendable out) {
      this.out = out;
   }

   @Override
   public void setActiveSheet(int sheetNum) {
      //
   }
}
