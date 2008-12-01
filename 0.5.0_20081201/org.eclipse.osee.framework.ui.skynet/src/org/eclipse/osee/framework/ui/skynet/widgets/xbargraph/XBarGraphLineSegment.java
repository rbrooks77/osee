/*
 * Created on Nov 5, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.widgets.xbargraph;

import org.eclipse.swt.SWT;

/**
 * @author Donald G. Dunne
 */
public class XBarGraphLineSegment {
   public static int DEFAULT_RED_FOREGROUND = SWT.COLOR_RED;
   public static int DEFAULT_RED_BACKGROUND = SWT.COLOR_YELLOW;
   public static int DEFAULT_GREEN_FOREGROUND = SWT.COLOR_GREEN;
   public static int DEFAULT_GREEN_BACKGROUND = SWT.COLOR_YELLOW;
   String name;
   int foreground;
   int background;
   long value;

   public XBarGraphLineSegment(String name, long value) {
      this(name, DEFAULT_GREEN_FOREGROUND, DEFAULT_GREEN_BACKGROUND, value);
   }

   public XBarGraphLineSegment(String name, int color, long value) {
      this(name, color, color, value);
   }

   public XBarGraphLineSegment(String name, int foreground, int background, long value) {
      this.name = name;
      this.foreground = foreground;
      this.background = background;
      this.value = value;
   }

   public static XBarGraphLineSegment getPercentSegment(String name, long value) {
      return new XBarGraphLineSegment(value + "%", DEFAULT_GREEN_FOREGROUND, DEFAULT_GREEN_BACKGROUND, value);
   }

   public static XBarGraphLineSegment getPercentSegment(String name, int color, long value) {
      return new XBarGraphLineSegment(value + "%", color, color, value);
   }

}
