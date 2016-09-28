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
package org.eclipse.osee.framework.jdk.core.text.rules;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.jdk.core.text.Rule;
import org.eclipse.osee.framework.jdk.core.text.change.ChangeSet;

/**
 * @author Ryan D. Brooks
 */
public class StaticImport extends Rule {
   private static final Pattern importP = Pattern.compile("[\n]import ");
   private final Pattern staticP;
   private final Set<String> constants = new HashSet<>();
   private final String importPrefix;

   public StaticImport(String packageName, String staticClassName) {
      super(null); // don't change extension on resulting file (i.e. overwrite the original file)
      this.staticP = Pattern.compile("[^.](" + staticClassName + "\\.)(\\w+)");
      importPrefix = "import static " + packageName + "." + staticClassName + ".";
   }

   @Override
   public ChangeSet computeChanges(CharSequence seq) {
      constants.clear();
      Matcher staticM = staticP.matcher(seq);
      ChangeSet changeSet = new ChangeSet(seq);

      while (staticM.find()) {
         ruleWasApplicable = true;
         changeSet.delete(staticM.start(1), staticM.end(1));
         constants.add(staticM.group(2));
      }
      if (ruleWasApplicable) {
         Matcher importM = importP.matcher(seq);
         importM.find();
         int index = importM.start() + 1;
         for (String constant : constants) {
            changeSet.insertBefore(index, importPrefix + constant + ";\n");
         }
      }
      return changeSet;
   }

   public static void main(String[] args) throws IOException {
      new StaticImport("org.eclipse.osee.framework.core.enums", "RelationOrderBaseTypes").process(
         new File("c:/UserData/git/org.eclipse.osee/plugins/"));
   }
}