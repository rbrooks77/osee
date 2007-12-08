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
package org.eclipse.osee.framework.messaging.event.skynet;

/**
 * @author Jeff C. Phillips
 */
public class RemoteDeletedBranchEvent implements ISkynetEvent {
   private static final long serialVersionUID = 4651718722048388742L;
   private int branchId;
   private int author;

   public RemoteDeletedBranchEvent(int branchId, int author) {
      this.branchId = branchId;
      this.author = author;
   }

   public int getTransactionId() {
      return 0;
   }

   public int getBranchId() {
      return branchId;
   }

   /* (non-Javadoc)
    * @see java.lang.Comparable#compareTo(T)
    */
   public int compareTo(Object o) {
      return 0;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.messaging.event.skynet.ISkynetEvent#getAuthor()
    */
   public int getAuthor() {
      return author;
   }
}
