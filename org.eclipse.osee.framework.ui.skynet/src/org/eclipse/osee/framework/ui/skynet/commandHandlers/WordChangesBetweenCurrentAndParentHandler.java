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
package org.eclipse.osee.framework.ui.skynet.commandHandlers;

import java.util.List;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.access.AccessControlManager;
import org.eclipse.osee.framework.skynet.core.access.PermissionEnum;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.WordArtifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.skynet.core.change.ChangeType;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.eclipse.ui.PlatformUI;

/**
 * @author Paul K. Waldfogel
 * @author Jeff C. Phillips
 */
public class WordChangesBetweenCurrentAndParentHandler extends AbstractHandler {
   private Change change;

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.core.commands.AbstractHandler#execute(org.eclipse.core.commands.ExecutionEvent)
    */
   @Override
   public Object execute(ExecutionEvent event) throws ExecutionException {
      try {
         Artifact secondArtifact =
               ArtifactQuery.getHistoricalArtifactFromId(change.getArtifact().getArtId(),
                     change.getToTransactionId(), true);
         RendererManager.diffInJob(null, secondArtifact);
      } catch (OseeCoreException ex) {
         OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return null;
   }

   @Override
   public boolean isEnabled() {
      if (PlatformUI.getWorkbench().isClosing()) {
         return false;
      }

      boolean isEnabled = false;
      ISelectionProvider selectionProvider =
            AWorkbench.getActivePage().getActivePart().getSite().getSelectionProvider();

      if (selectionProvider != null && selectionProvider.getSelection() instanceof IStructuredSelection) {
         IStructuredSelection structuredSelection = (IStructuredSelection) selectionProvider.getSelection();
         List<Change> artifactChanges = Handlers.getArtifactChangesFromStructuredSelection(structuredSelection);

         if (artifactChanges.size() == 0) {
            return false;
         }

         change = artifactChanges.get(0);
         try {
            Artifact artifact = change.getArtifact();

            boolean readPermission = AccessControlManager.hasPermission(artifact, PermissionEnum.READ);
            boolean wordArtifactSelected = artifact.isOfType(WordArtifact.ARTIFACT_NAME);
            boolean modifiedWordArtifactSelected =
                  wordArtifactSelected && change.getModificationType() == ModificationType.MODIFIED;
            boolean conflictedWordArtifactSelected =
                  modifiedWordArtifactSelected && change.getChangeType() == ChangeType.CONFLICTING;
            isEnabled = readPermission && conflictedWordArtifactSelected;
         } catch (Exception ex) {
            OseeLog.log(getClass(), OseeLevel.SEVERE_POPUP, ex);
         }
      }
      return isEnabled;
   }
}
