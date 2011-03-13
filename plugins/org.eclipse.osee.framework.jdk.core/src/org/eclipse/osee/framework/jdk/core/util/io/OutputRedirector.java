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
package org.eclipse.osee.framework.jdk.core.util.io;

import java.io.BufferedReader;
import java.io.Writer;
import java.util.concurrent.Callable;

/**
 * @author Ryan D. Brooks
 */
public final class OutputRedirector implements Callable<Long> {
   private final Writer output;
   private final BufferedReader input;

   public OutputRedirector(Writer output, BufferedReader input) {
      this.output = output;
      this.input = input;
   }

   @Override
   public Long call() throws Exception {
      long totalBytes = 0;
      try {
         final char[] buffer = new char[4096];
         int size;
         while ((size = input.read(buffer)) != -1) {
            totalBytes += size;
            output.write(buffer, 0, size);
            output.flush();
         }
      } finally {
         input.close();
      }
      return totalBytes;
   }
}