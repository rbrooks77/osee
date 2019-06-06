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
package org.eclipse.osee.framework.core.model.type;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.data.UserId;
import org.eclipse.osee.framework.core.enums.DemoUsers;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.TransactionDetailsType;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Test Case for {@link TransactionRecord}
 *
 * @author Megumi Telles
 */
@RunWith(Parameterized.class)
public class TransactionRecordTest {

   private final TransactionRecord transaction;
   private final Long transactionNumber;
   private final BranchId branch;
   private final TransactionDetailsType txType;

   private final String comment;
   private final Date time;
   private final UserId author;
   private final Long commitArtId;

   public TransactionRecordTest(int transactionNumber, BranchId branch, String comment, Date time, UserId author, Long commitArtId, TransactionDetailsType txType) {
      this.transactionNumber = (long) transactionNumber;
      this.branch = branch;
      this.comment = comment;
      this.time = time;
      this.author = author;
      this.commitArtId = commitArtId;
      this.txType = txType;

      this.transaction =
         new TransactionRecord(this.transactionNumber, branch, comment, time, author, commitArtId, txType, 234L);
   }

   @Test
   public void getBranch() {
      Assert.assertEquals(branch, transaction.getBranch());
   }

   @Test
   public void getId() {
      Assert.assertEquals(transactionNumber, transaction.getId());
   }

   @Test
   public void getTxType() {
      Assert.assertEquals(txType, transaction.getTxType());
   }

   @Test
   public void testGetSetComment() {
      Assert.assertEquals(comment, transaction.getComment());

      transaction.setComment("test set comment");
      Assert.assertEquals("test set comment", transaction.getComment());

      transaction.setComment(comment);
   }

   @Test
   public void testGetSetDate() {
      Assert.assertEquals(time, transaction.getTimeStamp());

      Date anotherDate = new Date(11111111111L);
      transaction.setTimeStamp(anotherDate);
      Assert.assertEquals(anotherDate, transaction.getTimeStamp());

      transaction.setTimeStamp(time);
   }

   @Test
   public void testGetSetAuthor() {
      Assert.assertEquals(author, transaction.getAuthor());

      UserId otherAuthor = UserId.valueOf(author.getId() * 101);
      transaction.setAuthor(otherAuthor);
      Assert.assertEquals(otherAuthor, transaction.getAuthor());

      transaction.setAuthor(author);
   }

   @Test
   public void testGetSetCommit() {
      Assert.assertEquals(commitArtId, transaction.getCommitArtId());

      Long otherId = commitArtId * 333;
      transaction.setCommit(otherId);
      Assert.assertEquals(otherId, transaction.getCommitArtId());

      transaction.setCommit(commitArtId);
   }

   @Test
   public void testEqualsAndHashCode() {
      // Add some variation to tx2 so we are certain that only the txId is used in the equals method;
      TransactionRecord tx2 =
         new TransactionRecord(99L, CoreBranches.COMMON, "a", new Date(), 0L, 1L, TransactionDetailsType.Baselined);
      TransactionId tx1 = TransactionId.valueOf(tx2.getId());

      Assert.assertNotSame(tx1, tx2);
      Assert.assertEquals(tx1, tx2);
      Assert.assertEquals(tx2, tx1);
      Assert.assertEquals(tx1.hashCode(), tx2.hashCode());

      Assert.assertFalse(transaction.equals(tx1));
      Assert.assertFalse(transaction.equals(tx2));
      Assert.assertFalse(transaction.hashCode() == tx1.hashCode());
      Assert.assertFalse(transaction.hashCode() == tx2.hashCode());
   }

   @Test
   public void testAdaptable() {
      Assert.assertNull(transaction.getAdapter(null));
      Assert.assertSame(transaction, transaction.getAdapter(Object.class));
      Assert.assertSame(transaction, transaction.getAdapter(TransactionRecord.class));
   }

   @Test
   public void testToString() {
      Assert.assertEquals(String.valueOf(transactionNumber), transaction.toString());
   }

   @Parameters
   public static Collection<Object[]> getData() {
      Collection<Object[]> data = new ArrayList<>();
      for (int index = 1; index <= 2; index++) {
         int transactionNumber = index * 11;
         BranchId branch = BranchId.valueOf(index * 9L);
         String comment = GUID.create();
         Date time = new Date();
         Long commitArtId = index * 37L;
         TransactionDetailsType txType = TransactionDetailsType.valueOf(index % TransactionDetailsType.values.length);
         data.add(new Object[] {transactionNumber, branch, comment, time, DemoUsers.Joe_Smith, commitArtId, txType});
      }
      return data;
   }
}
