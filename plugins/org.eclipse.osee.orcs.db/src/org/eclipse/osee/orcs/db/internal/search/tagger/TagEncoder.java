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
package org.eclipse.osee.orcs.db.internal.search.tagger;

import java.util.Arrays;
import java.util.function.Consumer;

/**
 * @author Roberto E. Escobar
 */
public class TagEncoder {

   private static final char[] tagChars = new char[] {
      '0',
      '1',
      '2',
      '3',
      '4',
      '5',
      '6',
      '7',
      '8',
      '9',
      'a',
      'b',
      'c',
      'd',
      'e',
      'f',
      'g',
      'h',
      'i',
      'l',
      'm',
      'n',
      'o',
      'p',
      'r',
      's',
      't',
      'u',
      'v',
      'w',
      'y'};

   /**
    * Create a bit-packed tag that will fit in a 64-bit integer that can provide an extremely quick search mechanism for
    * for the first pass. The second pass will do a full text search to provide more exact matches. The tag will
    * represent up to 12 characters (5-bits per character). Longer search tags will be turned into consecutive search
    * tags
    */
   public void encode(String text, Consumer<Long> consumer) {
      int tagBitsPos = 0;
      long tagBits = 0;
      for (int index = 0; index < text.length(); index++) {
         char c = text.charAt(index);

         if (c == '\t' || c == '\n' || c == '\r' || tagBitsPos == 60) {
            if (tagBitsPos > 10) {
               consumer.accept(tagBits);
            }
            tagBits = 0;
            tagBitsPos = 0;
         } else {
            if (c >= 'A' && c <= 'Z') {
               c += 32;
            }
            int pos = Arrays.binarySearch(tagChars, c);
            if (pos < 0) {
               tagBits |= 0x3F << tagBitsPos;
            } else {
               tagBits |= pos << tagBitsPos;
            }
            tagBitsPos += 5;
         }
      }
      if (tagBits != 0) {
         consumer.accept(tagBits);
      }
   }

   public static final void main(String[] args) {
      TagEncoder tagEncoder = new TagEncoder();
      String tests[] = new String[] {
         "111j1",
         "111k1",
         "text",
         "11$1111,111.1111",
         "11-1111(111)1111",
         "ImportTraceUnitsTest2",
         "ImportTraceUnitsTest3"};
      for (String text : tests) {
         System.out.print(text + "   ");
         tagEncoder.encode(text, tag -> System.out.print(tag + "  "));
         System.out.println();
      }
   }
}