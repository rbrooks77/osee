/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.db.internal.loader.data;

import java.util.Date;
import org.eclipse.osee.framework.core.data.ApplicabilityId;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.RelationTypeId;
import org.eclipse.osee.framework.core.data.RelationalConstants;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.BranchArchivedState;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.enums.TransactionDetailsType;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.orcs.core.ds.ArtifactData;
import org.eclipse.osee.orcs.core.ds.AttributeData;
import org.eclipse.osee.orcs.core.ds.BranchData;
import org.eclipse.osee.orcs.core.ds.DataProxy;
import org.eclipse.osee.orcs.core.ds.RelationData;
import org.eclipse.osee.orcs.core.ds.TupleData;
import org.eclipse.osee.orcs.core.ds.TxOrcsData;
import org.eclipse.osee.orcs.core.ds.VersionData;
import org.eclipse.osee.orcs.db.internal.OrcsObjectFactory;
import org.eclipse.osee.orcs.db.internal.loader.ProxyDataFactory;

/**
 * @author Roberto E. Escobar
 */
public class OrcsObjectFactoryImpl implements OrcsObjectFactory {

   private final ProxyDataFactory proxyFactory;

   public OrcsObjectFactoryImpl(ProxyDataFactory proxyFactory) {
      super();
      this.proxyFactory = proxyFactory;
   }

   @Override
   public VersionData createVersion(BranchId branchId, TransactionId txId, long gamma, boolean historical) {
      return createVersion(branchId, txId, gamma, historical, TransactionId.SENTINEL);
   }

   @Override
   public VersionData createDefaultVersionData() {
      // @formatter:off
      return createVersion(
         BranchId.SENTINEL,
         TransactionId.SENTINEL,
         RelationalConstants.GAMMA_SENTINEL,
         RelationalConstants.IS_HISTORICAL_DEFAULT,
         TransactionId.SENTINEL);
      // @formatter:on
   }

   @Override
   public VersionData createCopy(VersionData other) {
      // @formatter:off
      return createVersion(
         other.getBranch(),
         other.getTransactionId(),
         other.getGammaId(),
         other.isHistorical(),
         other.getStripeId());
      // @formatter:on
   }

   private VersionData createVersion(BranchId branchId, TransactionId txId, long gamma, boolean historical, TransactionId stripeId) {
      VersionData version = new VersionDataImpl();
      version.setBranch(branchId);
      version.setTransactionId(txId);
      version.setGammaId(gamma);
      version.setHistorical(historical);
      version.setStripeId(stripeId);
      return version;
   }

   @Override
   public ArtifactData createArtifactData(VersionData version, Integer localId, long typeUuid, ModificationType modType, String guid, ApplicabilityId applicId) throws OseeCoreException {
      return createArtifactFromRow(version, localId, typeUuid, modType, typeUuid, modType, guid, applicId);
   }

   @Override
   public ArtifactData createArtifactData(VersionData version, int localId, IArtifactType type, ModificationType modType, String guid, ApplicabilityId applicId) {
      long typeUuid = type.getGuid();
      return createArtifactFromRow(version, localId, typeUuid, modType, typeUuid, modType, guid, applicId);
   }

   @Override
   public ArtifactData createCopy(ArtifactData source) {
      VersionData newVersion = createCopy(source.getVersion());
      return createArtifactFromRow(newVersion, source.getLocalId(), source.getTypeUuid(), source.getModType(),
         source.getBaseTypeUuid(), source.getBaseModType(), source.getGuid(), source.getApplicabilityId());
   }

   @Override
   public AttributeData createAttributeData(VersionData version, Integer localId, AttributeTypeId attributeType, ModificationType modType, int artifactId, Object value, String uri, ApplicabilityId applicId) throws OseeCoreException {
      Long typeId = attributeType.getId();
      DataProxy proxy = proxyFactory.createProxy(typeId, value, uri);
      return createAttributeFromRow(version, localId, typeId, modType, typeId, modType, artifactId, proxy, applicId);
   }

   @Override
   public AttributeData createCopy(AttributeData source) throws OseeCoreException {
      VersionData newVersion = createCopy(source.getVersion());
      long typeId = source.getTypeUuid();
      DataProxy sourceProxy = source.getDataProxy();
      DataProxy newProxy = proxyFactory.createProxy(typeId, sourceProxy.getData());
      return createAttributeFromRow(newVersion, source.getLocalId(), typeId, source.getModType(),
         source.getBaseTypeUuid(), source.getBaseModType(), source.getArtifactId(), newProxy,
         source.getApplicabilityId());
   }

   @Override
   public AttributeData createAttributeData(VersionData version, Integer localId, AttributeTypeId attributeType, ModificationType modType, int artId, ApplicabilityId applicId) throws OseeCoreException {
      long typeId = attributeType.getId();
      DataProxy proxy = proxyFactory.createProxy(typeId, "", "");
      return createAttributeFromRow(version, localId, typeId, modType, typeId, modType, artId, proxy, applicId);
   }

   @Override
   public RelationData createRelationData(VersionData version, Integer localId, long typeId, ModificationType modType, int aArtId, int bArtId, String rationale, ApplicabilityId applicId) throws OseeCoreException {
      return createRelationData(version, localId, typeId, modType, typeId, modType, aArtId, bArtId, rationale,
         applicId);
   }

   @Override
   public RelationData createRelationData(VersionData version, Integer localId, RelationTypeId type, ModificationType modType, int aArtId, int bArtId, String rationale, ApplicabilityId applicId) {
      long typeId = type.getId();
      return createRelationData(version, localId, typeId, modType, typeId, modType, aArtId, bArtId, rationale,
         applicId);
   }

   private ArtifactData createArtifactFromRow(VersionData version, int localId, long localTypeID, ModificationType modType, long baseLocalTypeID, ModificationType baseModType, String guid, ApplicabilityId applicId) {
      ArtifactData data = new ArtifactDataImpl(version);
      data.setLocalId(localId);
      data.setTypeUuid(localTypeID);
      data.setBaseTypeUuid(baseLocalTypeID);
      data.setModType(modType);
      data.setBaseModType(baseModType);
      data.setGuid(guid);
      data.setApplicabilityId(applicId);
      return data;
   }

   private AttributeData createAttributeFromRow(VersionData version, int localId, long localTypeID, ModificationType modType, long baseLocalTypeID, ModificationType baseModType, int artifactId, DataProxy proxy, ApplicabilityId applicId) {
      AttributeData data = new AttributeDataImpl(version);
      data.setLocalId(localId);
      data.setTypeUuid(localTypeID);
      data.setBaseTypeUuid(baseLocalTypeID);
      data.setModType(modType);
      data.setBaseModType(baseModType);
      data.setArtifactId(artifactId);
      data.setDataProxy(proxy);
      data.setApplicabilityId(applicId);
      return data;
   }

   private RelationData createRelationData(VersionData version, int localId, long localTypeID, ModificationType modType, long baseLocalTypeID, ModificationType baseModType, int aArtId, int bArtId, String rationale, ApplicabilityId applicId) {
      RelationData data = new RelationDataImpl(version);
      data.setLocalId(localId);
      data.setTypeUuid(localTypeID);
      data.setBaseTypeUuid(baseLocalTypeID);
      data.setModType(modType);
      data.setBaseModType(baseModType);
      data.setArtIdA(aArtId);
      data.setArtIdB(bArtId);
      Conditions.assertNotNull(rationale,
         "rationale can't be null for RelationData localId [%s], localTypeId [%s], aArtId [%s], bArtId", localId,
         localTypeID, aArtId, bArtId);
      data.setRationale(rationale);
      data.setApplicabilityId(applicId);
      return data;
   }

   @Override
   public RelationData createCopy(RelationData source) {
      VersionData newVersion = createCopy(source.getVersion());
      return createRelationData(newVersion, source.getLocalId(), source.getTypeUuid(), source.getModType(),
         source.getBaseTypeUuid(), source.getBaseModType(), source.getArtIdA(), source.getArtIdB(),
         source.getRationale(), source.getApplicabilityId());
   }

   @Override
   public BranchData createBranchData(BranchId branch, BranchType branchType, String name, BranchId parentBranch, TransactionId baseTransaction, TransactionId sourceTransaction, BranchArchivedState archiveState, BranchState branchState, ArtifactId associatedArtifact, boolean inheritAccessControl) {
      BranchData data = new BranchDataImpl(branch, name);
      data.setArchiveState(archiveState);
      data.setAssociatedArtifact(associatedArtifact);
      data.setBaseTransaction(baseTransaction);
      data.setBranchState(branchState);
      data.setBranchType(branchType);
      data.setParentBranch(parentBranch);
      data.setSourceTransaction(sourceTransaction);
      data.setInheritAccessControl(inheritAccessControl);
      return data;
   }

   @Override
   public BranchData createCopy(BranchData source) {
      return createBranchData(BranchId.create(source.getId(), source.getViewId()), source.getBranchType(),
         source.getName(), source.getParentBranch(), source.getBaseTransaction(), source.getSourceTransaction(),
         source.getArchiveState(), source.getBranchState(), source.getAssociatedArtifact(),
         source.isInheritAccessControl());
   }

   @Override
   public TxOrcsData createTxData(Long localId, TransactionDetailsType type, Date date, String comment, BranchId branch, ArtifactId author, ArtifactId commitArt, Long buildId) {
      TxOrcsData data = new TransactionDataImpl(localId);
      data.setTxType(type);
      data.setDate(date);
      data.setComment(comment);
      data.setBranch(branch);
      data.setAuthor(author);
      data.setCommitArt(commitArt);
      data.setBuildId(buildId);
      return data;
   }

   @Override
   public TxOrcsData createCopy(TxOrcsData source) {
      return createTxData(source.getId(), source.getTxType(), source.getDate(), source.getComment(), source.getBranch(),
         source.getAuthor(), source.getCommitArt(), source.getBuildId());
   }

   @Override
   public TupleData createTuple2Data(VersionData version, BranchId branchId, Long tupleType, Long element1, Long element2) throws OseeCoreException {
      TupleData data = new TupleDataImpl(version);
      data.setBaseModType(ModificationType.NEW);
      data.setModType(ModificationType.NEW);
      data.setApplicabilityId(ApplicabilityId.BASE);
      data.setTupleType(tupleType);
      data.setElement1(element1);
      data.setElement2(element2);
      data.getVersion().setGammaId(Lib.generateUuid());
      data.getVersion().setBranch(branchId);
      return data;
   }

   @Override
   public TupleData createTuple3Data(VersionData version, BranchId branchId, Long tupleType, Long e1, Long e2, Long e3) throws OseeCoreException {
      TupleData data = new TupleDataImpl(version);
      data.setBaseModType(ModificationType.NEW);
      data.setModType(ModificationType.NEW);
      data.setApplicabilityId(ApplicabilityId.BASE);
      data.setTupleType(tupleType);
      data.setElement1(e1);
      data.setElement2(e2);
      data.setElement3(e3);
      data.getVersion().setGammaId(Lib.generateUuid());
      data.getVersion().setBranch(branchId);
      return data;
   }

   @Override
   public TupleData createTuple4Data(VersionData version, BranchId branchId, Long tupleType, Long e1, Long e2, Long e3, Long e4) throws OseeCoreException {
      TupleData data = new TupleDataImpl(version);
      data.setBaseModType(ModificationType.NEW);
      data.setModType(ModificationType.NEW);
      data.setApplicabilityId(ApplicabilityId.BASE);
      data.setTupleType(tupleType);
      data.setElement1(e1);
      data.setElement2(e2);
      data.setElement3(e3);
      data.setElement4(e4);
      data.getVersion().setGammaId(Lib.generateUuid());
      data.getVersion().setBranch(branchId);
      return data;
   }

}
