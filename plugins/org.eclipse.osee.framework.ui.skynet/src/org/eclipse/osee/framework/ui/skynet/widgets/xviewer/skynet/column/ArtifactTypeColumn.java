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
package org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column;

import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.nebula.widgets.xviewer.XViewerValueColumn;
import org.eclipse.nebula.widgets.xviewer.util.XViewerException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.ui.skynet.ArtifactImageManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;

/**
 * @author Donald G. Dunne
 */
public class ArtifactTypeColumn extends XViewerValueColumn {

   public static ArtifactTypeColumn instance = new ArtifactTypeColumn();

   public static ArtifactTypeColumn getInstance() {
      return instance;
   }

   public ArtifactTypeColumn(String id) {
      super(id, "Artifact Type", 150, SWT.LEFT, true, SortDataType.String, false, "Artifact Type");
   }

   public ArtifactTypeColumn() {
      super("framework.artifact.type." + "Artifact Type", "Artifact Type", 150, SWT.LEFT, true, SortDataType.String,
         false, "Artifact Type");
   }

   /**
    * XViewer uses copies of column definitions so originals that are registered are not corrupted. Classes extending
    * XViewerValueColumn MUST extend this constructor so the correct sub-class is created
    */
   @Override
   public ArtifactTypeColumn copy() {
      ArtifactTypeColumn newXCol = new ArtifactTypeColumn();
      super.copy(this, newXCol);
      return newXCol;
   }

   @Override
   public String getColumnText(Object element, XViewerColumn column, int columnIndex) throws XViewerException {
      if (element instanceof Artifact) {
         return ((Artifact) element).getArtifactTypeName();
      } else if (element instanceof Change) {
         return ((Change) element).getArtifactType().getName();
      } else if (element instanceof String) {
         return "";
      }
      return super.getColumnText(element, column, columnIndex);
   }

   @Override
   public Image getColumnImage(Object element, XViewerColumn column, int columnIndex) throws XViewerException {
      if (element instanceof Artifact) {
         return ArtifactImageManager.getImage((Artifact) element);
      }
      return super.getColumnImage(element, column, columnIndex);
   }
}
