/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.jdk.core.type;

/**
 * @author Ryan D. Brooks
 */
public class CaseInsensitiveString implements CharSequence {
   private final String originalString;
   private final String upperCaseString;

   public CaseInsensitiveString(String string) {
      this.originalString = string;
      upperCaseString = string.toUpperCase();
   }

   @Override
   public String toString() {
      return originalString;
   }

   @Override
   public int hashCode() {
      return ((upperCaseString == null) ? 0 : upperCaseString.hashCode());
   }

   @Override
   public boolean equals(Object obj) {
      if (obj instanceof CaseInsensitiveString) {
         CaseInsensitiveString other = ((CaseInsensitiveString) obj);
         if (upperCaseString == null) {
            return other.upperCaseString == null;
         } else {
            return upperCaseString.equals(other.upperCaseString);
         }
      } else if (obj instanceof String) {
         return upperCaseString.equalsIgnoreCase(((String) obj));
      }
      return false;
   }

   @Override
   public int length() {
      return originalString == null ? 0 : originalString.length();
   }

   @Override
   public char charAt(int index) {
      return originalString.charAt(index);
   }

   @Override
   public CharSequence subSequence(int beginIndex, int endIndex) {
      return originalString.subSequence(beginIndex, endIndex);
   }
}