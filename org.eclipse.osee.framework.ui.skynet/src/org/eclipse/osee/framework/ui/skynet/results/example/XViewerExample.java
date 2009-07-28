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
package org.eclipse.osee.framework.ui.skynet.results.example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn.SortDataType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.results.IResultsEditorProvider;
import org.eclipse.osee.framework.ui.skynet.results.IResultsEditorTab;
import org.eclipse.osee.framework.ui.skynet.results.ResultsEditor;
import org.eclipse.osee.framework.ui.skynet.results.table.IResultsXViewerRow;
import org.eclipse.osee.framework.ui.skynet.results.table.ResultsEditorTableTab;
import org.eclipse.osee.framework.ui.skynet.results.table.ResultsXViewerRow;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.swt.SWT;

/**
 * @author Donald G. Dunne
 */
public class XViewerExample extends XNavigateItemAction {

   public static String TITLE = "XViewer Example";
   private static enum Columns {
      Date, String1, String2;
   };

   /**
    * @param parent
    */
   public XViewerExample(XNavigateItem parent) {
      super(parent, TITLE, FrameworkImage.ADMIN);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItemAction#run(org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateComposite.TableLoadOption[])
    */
   @Override
   public void run(TableLoadOption... tableLoadOptions) throws Exception {
      ResultsEditor.open(new IResultsEditorProvider() {

         @Override
         public String getEditorName() throws OseeCoreException {
            return TITLE;
         }

         @Override
         public List<IResultsEditorTab> getResultsEditorTabs() throws OseeCoreException {
            List<IResultsXViewerRow> rows = new ArrayList<IResultsXViewerRow>();
            List<IResultsXViewerRow> bigRows = new ArrayList<IResultsXViewerRow>();
            for (int x = 0; x < 50000; x++) {
               if (x < 15000) {
                  rows.add(new ResultsXViewerRow(new String[] {"Date " + x, "hello", "world"}));
               }
               bigRows.add(new ResultsXViewerRow(new String[] {"Date " + x, "hello", "world"}));
            }
            List<XViewerColumn> columns =
                  Arrays.asList(new XViewerColumn(Columns.Date.name(), Columns.Date.name(), 80, SWT.LEFT, true,
                        SortDataType.String, false, ""), new XViewerColumn(Columns.String1.name(),
                        Columns.String1.name(), 80, SWT.LEFT, true, SortDataType.Integer, false, ""),
                        new XViewerColumn(Columns.String2.name(), Columns.String2.name(), 80, SWT.LEFT, true,
                              SortDataType.Integer, false, ""));
            List<IResultsEditorTab> tabs = new ArrayList<IResultsEditorTab>();
            tabs.add(new ResultsEditorTableTab("15,000 entries", columns, rows));
            tabs.add(new ResultsEditorTableTab("50,000 entries", columns, bigRows));
            return tabs;
         }

      });
   }

}
