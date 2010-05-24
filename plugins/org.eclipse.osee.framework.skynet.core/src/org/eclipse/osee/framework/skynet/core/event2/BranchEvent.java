package org.eclipse.osee.framework.skynet.core.event2;

import org.eclipse.osee.framework.skynet.core.event.BranchEventType;
import org.eclipse.osee.framework.skynet.core.event.msgs.NetworkSender;

public class BranchEvent extends FrameworkEvent {

   private String branchGuid;
   private BranchEventType eventType;
   private NetworkSender networkSender;

   /**
    * Gets the value of the branchGuid property.
    * 
    * @return possible object is {@link String }
    */
   public String getBranchGuid() {
      return branchGuid;
   }

   /**
    * Sets the value of the branchGuid property.
    * 
    * @param value allowed object is {@link String }
    */
   public void setBranchGuid(String value) {
      this.branchGuid = value;
   }

   /**
    * Gets the value of the networkSender property.
    * 
    * @return possible object is {@link NetworkSender }
    */
   public NetworkSender getNetworkSender() {
      return networkSender;
   }

   /**
    * Sets the value of the networkSender property.
    * 
    * @param value allowed object is {@link NetworkSender }
    */
   public void setNetworkSender(NetworkSender value) {
      this.networkSender = value;
   }

   public BranchEventType getEventType() {
      return eventType;
   }

   public void setEventType(BranchEventType eventType) {
      this.eventType = eventType;
   }

}
