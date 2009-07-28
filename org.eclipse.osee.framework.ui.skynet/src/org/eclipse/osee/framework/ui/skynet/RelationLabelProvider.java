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
package org.eclipse.osee.framework.ui.skynet;

import java.util.logging.Level;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;
import org.eclipse.osee.framework.skynet.core.relation.RelationType;
import org.eclipse.osee.framework.skynet.core.relation.RelationTypeSide;
import org.eclipse.swt.graphics.Image;

/**
 * @author Ryan D. Brooks
 */
public class RelationLabelProvider implements ITableLabelProvider, ILabelProvider {
   private Artifact artifact;

   public RelationLabelProvider(Artifact artifact) {
      this.artifact = artifact;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
    */
   public Image getColumnImage(Object element, int columnIndex) {
      if (element instanceof RelationType && columnIndex == 0) {
         return ImageManager.getImage(FrameworkImage.RELATION);
      } else if (element instanceof RelationLink && columnIndex == 0) {
         RelationLink relation = (RelationLink) element;
         try {
            return ImageManager.getImage(relation.getArtifactOnOtherSide(artifact));
         } catch (Exception ex) {
            OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
         }
      }
      return null;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
    */
   public String getColumnText(Object element, int columnIndex) {
      if (element instanceof RelationTypeSide && columnIndex == 0) {
         RelationTypeSide side = (RelationTypeSide) element;
         return side.getSideName() + " \"" + (side.isSideA() ? side.getRelationType().getAToBPhrasing() : side.getRelationType().getBToAPhrasing()) + "\" " + (side.isSideA() ? side.getRelationType().getSideBName() : side.getRelationType().getSideAName()) + " \"" + artifact.getName() + "\"";
      } else if (element instanceof RelationType) {
         if (columnIndex == 0) {
            return ((RelationType) element).getTypeName();
         }
      } else if (element instanceof RelationLink) {
         RelationLink link = (RelationLink) element;
         if (columnIndex == 0) {
            try {
               return link.getArtifactOnOtherSide(artifact).getName();
            } catch (OseeCoreException ex) {
               OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
            }
         } else if (columnIndex == 1) {
            return link.getRationale();
         }
      }
      return "";
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
    */
   public void addListener(ILabelProviderListener listener) {
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
    */
   public void dispose() {
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object,
    *      java.lang.String)
    */
   public boolean isLabelProperty(Object element, String property) {
      return true;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
    */
   public void removeListener(ILabelProviderListener listener) {
   }

   public Image getImage(Object element) {
      return getColumnImage(element, 0);
   }

   public String getText(Object element) {
      return getColumnText(element, 0);
   }

   /**
    * @param artifact The artifact to set.
    */
   public void setArtifact(Artifact artifact) {
      this.artifact = artifact;
   }
}