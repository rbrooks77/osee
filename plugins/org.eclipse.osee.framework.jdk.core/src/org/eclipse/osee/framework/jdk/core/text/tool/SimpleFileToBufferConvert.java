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
package org.eclipse.osee.framework.jdk.core.text.tool;

import java.io.File;
import java.io.IOException;
import org.eclipse.osee.framework.jdk.core.text.FileToBufferConvert;
import org.eclipse.osee.framework.jdk.core.util.Lib;

/**
 * @author Ryan D. Brooks
 */
public class SimpleFileToBufferConvert implements FileToBufferConvert {

   @Override
   public CharSequence fileToCharSequence(File file) throws IOException {
      return Lib.fileToCharBuffer(file);
   }
}