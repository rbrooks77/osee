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
package org.eclipse.osee.framework.ui.skynet.results.table;

import java.util.Collection;
import java.util.List;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.ui.skynet.results.IResultsEditorTab;

/**
 * @author Donald G. Dunne
 */
public interface IResultsEditorTableTab extends IResultsEditorTab {

   public List<XViewerColumn> getTableColumns() throws OseeCoreException;

   public Collection<IResultsXViewerRow> getTableRows() throws OseeCoreException;

}
