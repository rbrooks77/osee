/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.impl.internal.convert;

import java.util.logging.Level;
import org.eclipse.osee.ats.impl.internal.util.AtsUtilServer;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.util.XResultData;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.AttributeReadable;
import org.eclipse.osee.orcs.data.BranchReadable;
import org.eclipse.osee.orcs.search.QueryFactory;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;

/**
 * @author Megumi Telles
 */
public class ConvertFavoriteBranchGuidToUuid extends AbstractConvertGuidToUuid {

   private int numChanges = 0;

   public ConvertFavoriteBranchGuidToUuid(IOseeDatabaseService dbService, OrcsApi orcsApi) {
      super(dbService, orcsApi);
   }

   @Override
   public String getName() {
      return "FavoriteBranchGuidToUuid";
   }

   @Override
   public String getDescription() {
      StringBuffer data = new StringBuffer();
      data.append("ConvertFavoriteBranchGuidToUuid (required conversion)\n\n");
      data.append("Necessary for upgrading from OSEE 0.16.2 to 0.17.0\n");
      data.append("-- Converts a User's Favorite Branch Guid(s) to Uuid(s).\n\n");
      data.append("NOTE: This operation can be run multiple times\n");
      return data.toString();
   }

   @Override
   public void run(XResultData data, boolean reportOnly) {
      if (reportOnly) {
         data.log("REPORT ONLY - Changes not persisted\n");
      }
      try {
         QueryFactory queryFactory = getOrcsApi().getQueryFactory(null);
         TransactionBuilder tx = createTransactionBuilder();
         for (ArtifactReadable art : getUsersFavoriteBranch(queryFactory)) {
            convertAttributeToUuid(data, reportOnly, tx, art, art.getAttributes(CoreAttributeTypes.FavoriteBranch));
         }
         if (reportOnly) {
            data.log("\n" + numChanges + " Need to be Changed");
         } else {
            data.log("\n" + numChanges + " Changes Persisted");
            if (numChanges > 0) {
               tx.commit();
            }
         }
         numChanges = 0;
      } catch (OseeCoreException ex) {
         OseeLog.log(this.getClass(), Level.SEVERE, "Exception occurred while trying to convert branch guid to uuid",
            ex);
      }
   }

   private void convertAttributeToUuid(XResultData data, boolean reportOnly, TransactionBuilder tx, ArtifactReadable art, ResultSet<? extends AttributeReadable<Object>> favBranchAttrValues) throws OseeCoreException {
      for (AttributeReadable<Object> attr : favBranchAttrValues) {
         String value = attr.toString();
         if (GUID.isValid(value)) {
            convert(data, reportOnly, tx, art, attr, value);
         } else {
            data.logWithFormat(
               "Not a guid attribute value.  Actual value [%s] for artifact type [%s] name [%s] id [%s] NOT converted to uuid.\n \n",
               value, art.getArtifactType(), art.getName(), art.getGuid());
         }
      }
   }

   private void convert(XResultData data, boolean reportOnly, TransactionBuilder tx, ArtifactReadable art, AttributeReadable<Object> attr, String value) throws OseeCoreException {
      BranchReadable branch = null;
      try {
         branch = getBranch(value);
      } catch (OseeCoreException ex) {
         OseeLog.log(this.getClass(), Level.WARNING, "No Branch found with value: " + value);
      }
      if (branch != null) {
         addUuid(data, reportOnly, tx, art, attr, branch);
      } else {
         removeAttrForNonExistentBranch(data, reportOnly, tx, art, attr, value);
      }
   }

   private void addUuid(XResultData data, boolean reportOnly, TransactionBuilder tx, ArtifactReadable art, AttributeReadable<Object> attr, BranchReadable branch) throws OseeCoreException {
      numChanges++;
      Long branchUuid = branch.getUuid();
      data.logWithFormat("Adding uuid attribute of value %d to artifact type [%s] name [%s] id [%s]\n", branchUuid,
         art.getArtifactType(), art.getName(), art.getGuid());
      if (!reportOnly) {
         try {
            tx.setAttributeById(art, attr, String.valueOf(branchUuid));
         } catch (OseeCoreException ex) {
            data.logErrorWithFormat(
               "Error building transaction for convert to uuid attribute of value %d for artifact type [%s] name [%s] id [%s]\n",
               branch.getUuid(), art.getArtifactType(), art.getName(), art.getGuid());
         }
      }
   }

   private void removeAttrForNonExistentBranch(XResultData data, boolean reportOnly, TransactionBuilder tx, ArtifactReadable art, AttributeReadable<Object> attr, String value) throws OseeCoreException {
      try {
         data.logWithFormat("No Branch found with value [%s]. Recommend removing attribute.\n", value);
         if (!reportOnly) {
            tx.deleteByAttributeId(art, attr);
         }
      } catch (OseeCoreException ex) {
         data.logErrorWithFormat("Error building transaction to remove guid [%s] for branch that no longer exists\n",
            value);
      }
   }

   private ResultSet<ArtifactReadable> getUsersFavoriteBranch(QueryFactory queryFactory) throws OseeCoreException {
      return queryFactory.fromBranch(AtsUtilServer.getAtsBranch()).andTypeEquals(CoreArtifactTypes.User).andExists(
         CoreAttributeTypes.FavoriteBranch).getResults();
   }

}
