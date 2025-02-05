//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.6
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Any modifications to this file will be lost upon recompilation of the source schema.
// Generated on: 2012.09.14 at 05:06:42 PM MST
//

package org.eclipse.osee.framework.messaging.event.res.msgs;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.eclipse.osee.framework.messaging.event.res.RemoteEvent;

/**
 * <p>
 * Java class for RemoteBroadcastEvent1 complex type.
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RemoteBroadcastEvent1">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="eventTypeGuid" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="userIds" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded"/>
 *         &lt;element name="message" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="networkSender" type="{}RemoteNetworkSender1"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RemoteBroadcastEvent1", propOrder = {"eventTypeGuid", "userIds", "message", "networkSender"})
public class RemoteBroadcastEvent1 extends RemoteEvent {

   @XmlElement(required = true)
   protected String eventTypeGuid;
   @XmlElement(required = true)
   protected List<String> userIds;
   @XmlElement(required = true)
   protected String message;
   @XmlElement(required = true)
   protected RemoteNetworkSender1 networkSender;

   /**
    * Gets the value of the eventTypeGuid property.
    * 
    * @return possible object is {@link String }
    */
   public String getEventTypeGuid() {
      return eventTypeGuid;
   }

   /**
    * Sets the value of the eventTypeGuid property.
    * 
    * @param value allowed object is {@link String }
    */
   public void setEventTypeGuid(String value) {
      this.eventTypeGuid = value;
   }

   /**
    * Gets the value of the userIds property.
    * <p>
    * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to
    * the returned list will be present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for
    * the userIds property.
    * <p>
    * For example, to add a new item, do as follows:
    * 
    * <pre>
    * getUserIds().add(newItem);
    * </pre>
    * <p>
    * Objects of the following type(s) are allowed in the list {@link String }
    */
   public List<String> getUserIds() {
      if (userIds == null) {
         userIds = new ArrayList<>();
      }
      return this.userIds;
   }

   /**
    * Gets the value of the message property.
    * 
    * @return possible object is {@link String }
    */
   public String getMessage() {
      return message;
   }

   /**
    * Sets the value of the message property.
    * 
    * @param value allowed object is {@link String }
    */
   public void setMessage(String value) {
      this.message = value;
   }

   /**
    * Gets the value of the networkSender property.
    * 
    * @return possible object is {@link RemoteNetworkSender1 }
    */
   @Override
   public RemoteNetworkSender1 getNetworkSender() {
      return networkSender;
   }

   /**
    * Sets the value of the networkSender property.
    * 
    * @param value allowed object is {@link RemoteNetworkSender1 }
    */
   public void setNetworkSender(RemoteNetworkSender1 value) {
      this.networkSender = value;
   }

}
