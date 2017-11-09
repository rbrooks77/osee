/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.workdef;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Mark Joy
 */
@XmlRootElement
public class RunRuleResults {
   private final List<RuleResultData> resultsList = new ArrayList<>();

   public List<RuleResultData> getResultsList() {
      return this.resultsList;
   }

   public Collection<Long> getChangedWorkitemIds() {
      List<Long> idList = new ArrayList<>();
      for (RuleResultData result : this.resultsList) {
         idList.add(result.id);
      }
      return idList;
   }

   public void addChange(Long id, RuleResultsEnum changeType) {
      Boolean addNew = true;
      for (RuleResultData result : this.resultsList) {
         if (result.id == id) {
            if (!result.resultList.contains(changeType)) {
               result.resultList.add(changeType);
               addNew = false;
               break;
            }
         }
      }
      if (addNew) {
         RuleResultData newresult = new RuleResultData();
         newresult.id = id;
         newresult.resultList.add(changeType);
         this.resultsList.add(newresult);
      }
   }

}
