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
package org.eclipse.osee.framework.search.engine.tagger;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.search.engine.attribute.AttributeData;
import org.eclipse.osee.framework.search.engine.internal.TagProcessor;
import org.eclipse.osee.framework.search.engine.utility.ITagCollector;
import org.eclipse.osee.framework.search.engine.utility.XmlTextInputStream;

/**
 * @author Roberto E. Escobar
 */
public class XmlAttributeTaggerProvider extends BaseAttributeTaggerProvider {

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.search.engine.attribute.IAttributeTagger#find(org.eclipse.osee.framework.search.engine.attribute.AttributeData, java.lang.String)
    */
   @Override
   public boolean find(AttributeData attributeData, String value) {
      boolean toReturn = false;
      if (Strings.isValid(value)) {
         value = value.toLowerCase();
         InputStream inputStream = null;
         try {
            inputStream = new BufferedInputStream(new XmlTextInputStream(getValueAsStream(attributeData)));
            int read = -1;
            int index = 0;
            int searchStrSize = value.length();
            while ((read = inputStream.read()) != -1) {
               char curr = Character.toLowerCase((char) read);
               char toCheck = value.charAt(index);
               if (toCheck == curr) {
                  index++;
                  if (index >= searchStrSize) {
                     toReturn = true;
                     break;
                  }
               } else {
                  index = 0;
               }
            }
         } catch (Exception ex) {
            OseeLog.log(XmlAttributeTaggerProvider.class, Level.SEVERE, ex.toString(), ex);
         } finally {
            if (inputStream != null) {
               try {
                  inputStream.close();
               } catch (IOException ex) {
                  OseeLog.log(XmlAttributeTaggerProvider.class, Level.SEVERE, ex.toString(), ex);
               }
            }
         }
      }
      return toReturn;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.search.engine.attribute.IAttributeTagger#tagIt(org.eclipse.osee.framework.search.engine.attribute.AttributeData, org.eclipse.osee.framework.search.engine.utility.ITagCollector)
    */
   @Override
   public void tagIt(AttributeData attributeData, ITagCollector collector) throws Exception {
      InputStream inputStream = null;
      try {
         inputStream = new XmlTextInputStream(getValueAsStream(attributeData));
         TagProcessor.collectFromInputStream(inputStream, collector);
      } finally {
         if (inputStream != null) {
            inputStream.close();
         }
      }
   }
}
