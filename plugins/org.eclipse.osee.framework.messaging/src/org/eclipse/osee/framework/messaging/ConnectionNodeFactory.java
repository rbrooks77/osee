/*
 * Created on Jan 15, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.messaging;


/**
 * @author Roberto E. Escobar
 */
public interface ConnectionNodeFactory {

   ConnectionNode create(NodeInfo nodeInfo);
   
}
