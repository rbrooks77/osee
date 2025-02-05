/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse  License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api;

import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.api.agile.IAgileService;
import org.eclipse.osee.ats.api.column.IAtsColumnService;
import org.eclipse.osee.ats.api.config.AtsConfigurations;
import org.eclipse.osee.ats.api.config.IAtsCache;
import org.eclipse.osee.ats.api.config.IAtsConfigurationProvider;
import org.eclipse.osee.ats.api.config.IWorkDefinitionStringProvider;
import org.eclipse.osee.ats.api.ev.IAtsEarnedValueService;
import org.eclipse.osee.ats.api.ev.IAtsEarnedValueServiceProvider;
import org.eclipse.osee.ats.api.notify.AtsNotificationCollector;
import org.eclipse.osee.ats.api.program.IAtsProgramService;
import org.eclipse.osee.ats.api.query.IAtsQueryService;
import org.eclipse.osee.ats.api.query.IAtsSearchDataProvider;
import org.eclipse.osee.ats.api.review.IAtsReviewService;
import org.eclipse.osee.ats.api.task.IAtsTaskService;
import org.eclipse.osee.ats.api.team.ChangeType;
import org.eclipse.osee.ats.api.team.IAtsConfigItemFactory;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinitionService;
import org.eclipse.osee.ats.api.team.IAtsWorkItemFactory;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.user.IAtsUserService;
import org.eclipse.osee.ats.api.util.IArtifactResolver;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.util.IAtsStoreService;
import org.eclipse.osee.ats.api.util.ISequenceProvider;
import org.eclipse.osee.ats.api.version.IAtsVersionService;
import org.eclipse.osee.ats.api.version.IVersionFactory;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinitionAdmin;
import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinitionService;
import org.eclipse.osee.ats.api.workdef.IAttributeResolver;
import org.eclipse.osee.ats.api.workdef.IRelationResolver;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.api.workflow.IAtsActionFactory;
import org.eclipse.osee.ats.api.workflow.IAtsBranchService;
import org.eclipse.osee.ats.api.workflow.IAtsImplementerService;
import org.eclipse.osee.ats.api.workflow.IAtsWorkItemServiceProvider;
import org.eclipse.osee.ats.api.workflow.ITeamWorkflowProvidersLazy;
import org.eclipse.osee.ats.api.workflow.log.IAtsLogFactory;
import org.eclipse.osee.ats.api.workflow.state.IAtsStateFactory;
import org.eclipse.osee.ats.api.workflow.state.IAtsWorkStateFactory;
import org.eclipse.osee.ats.api.workflow.transition.ITransitionListener;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.logger.Log;

/**
 * @author Donald G. Dunne
 */
public interface IAtsServices extends IAtsEarnedValueServiceProvider, IAtsConfigurationProvider, IWorkDefinitionStringProvider, IAtsWorkItemServiceProvider {

   IOseeBranch getAtsBranch();

   IRelationResolver getRelationResolver();

   IAttributeResolver getAttributeResolver();

   IAtsUserService getUserService();

   IAtsReviewService getReviewService();

   IAtsBranchService getBranchService();

   IAtsWorkDefinitionService getWorkDefService();

   IAtsVersionService getVersionService();

   ArtifactToken getArtifact(Long uuid);

   ArtifactToken getArtifact(ArtifactId artifact);

   ArtifactToken getArtifact(IAtsObject atsObject);

   void setChangeType(IAtsObject atsObject, ChangeType changeType, IAtsChangeSet changes);

   ChangeType getChangeType(IAtsAction fromAction);

   String getAtsId(ArtifactId artifact);

   String getAtsId(IAtsObject atsObject);

   Collection<IArtifactType> getArtifactTypes();

   IAtsWorkItemFactory getWorkItemFactory();

   ArtifactToken getArtifactById(String id);

   IAtsConfigItemFactory getConfigItemFactory();

   IAtsStoreService getStoreService();

   <A extends IAtsConfigObject> A getSoleByUuid(long uuid, Class<A> clazz);

   Collection<ITransitionListener> getTransitionListeners();

   void clearImplementersCache(IAtsWorkItem workItem);

   IArtifactResolver getArtifactResolver();

   IAtsTaskService getTaskService();

   ArtifactToken getArtifactByName(IArtifactType artifactType, String name);

   ArtifactToken getArtifactByGuid(String guid);

   IAtsProgramService getProgramService();

   IAtsQueryService getQueryService();

   @Override
   IAtsEarnedValueService getEarnedValueService();

   @Override
   AtsConfigurations getConfigurations();

   IAtsEarnedValueServiceProvider getEarnedValueServiceProvider();

   IAtsImplementerService getImplementerService();

   IAtsColumnService getColumnService();

   IAtsWorkDefinitionAdmin getWorkDefinitionAdmin();

   IAtsCache getCache();

   ISequenceProvider getSequenceProvider();

   IAtsActionFactory getActionFactory();

   /**
    * @param key - key of key/value config pair. equals sign not accepted
    */
   String getConfigValue(String key);

   Log getLogger();

   <T> T getConfigItem(ArtifactId artifactToken);

   <T> T getConfigItem(String guid);

   <T> T getConfigItem(Long uuid);

   void setConfigValue(String key, String value);

   IAtsChangeSet createChangeSet(String comment);

   IAtsChangeSet createChangeSet(String comment, IAtsUser user);

   void storeAtsBranch(BranchId branch, String name);

   List<IAtsSearchDataProvider> getSearchDataProviders();

   void invalidateAllCaches();

   void invalidateWorkDefinitionCache();

   ITeamWorkflowProvidersLazy getTeamWorkflowProviders();

   IVersionFactory getVersionFactory();

   IAtsStateFactory getStateFactory();

   IAtsWorkStateFactory getWorkStateFactory();

   IAtsLogFactory getLogFactory();

   IAtsTeamDefinitionService getTeamDefinitionService();

   void sendNotifications(AtsNotificationCollector notifications);

   Collection<ArtifactToken> getArtifacts(List<Long> ids);

   IAgileService getAgileService();

   ArtifactId getArtifactByAtsId(String id);
}
