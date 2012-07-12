/*
 * Created on Mar 2, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.client.workflow;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.ats.core.client.util.UsersByIds;
import org.eclipse.osee.ats.core.model.impl.WorkStateImpl;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Donald G. Dunne
 */
public class AtsWorkStateFactory {

   public static Pattern storagePattern = Pattern.compile("^(.*?);(.*?);(.*?);(.*?)$");

   public static String toXml(StateManager stateMgr, String stateName) throws OseeCoreException {
      StringBuffer sb = new StringBuffer(stateName);
      sb.append(";");
      sb.append(org.eclipse.osee.ats.core.client.util.UsersByIds.getStorageString(stateMgr.getAssignees(stateName)));
      sb.append(";");
      double hoursSpent = stateMgr.getHoursSpent(stateName);
      if (hoursSpent > 0) {
         sb.append(stateMgr.getHoursSpentStr(stateName));
      }
      sb.append(";");
      int percentComplete = stateMgr.getPercentComplete(stateName);
      if (percentComplete > 0) {
         sb.append(percentComplete);
      }
      return sb.toString();
   }

   public static WorkStateImpl getFromXml(String xml) throws OseeCoreException {
      WorkStateImpl state = new WorkStateImpl("Unknown");
      if (Strings.isValid(xml)) {
         Matcher m = storagePattern.matcher(xml);
         if (m.find()) {
            state.setName(m.group(1));
            if (!m.group(3).equals("")) {
               state.setHoursSpent(new Float(m.group(3)).doubleValue());
            }
            if (!m.group(4).equals("")) {
               state.setPercentComplete(Integer.valueOf(m.group(4)).intValue());
            }
            state.setAssignees(UsersByIds.getUsers(m.group(2)));
         } else {
            throw new OseeArgumentException("Can't unpack state data [%s]", xml);
         }
      }
      return state;
   }

}
