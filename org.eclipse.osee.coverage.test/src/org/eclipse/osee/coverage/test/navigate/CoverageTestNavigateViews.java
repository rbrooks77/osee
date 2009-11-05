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
package org.eclipse.osee.coverage.test.navigate;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.coverage.internal.Activator;
import org.eclipse.osee.coverage.navigate.ICoverageNavigateItem;
import org.eclipse.osee.coverage.test.import1.CoverageImport1TestNavigateItem;
import org.eclipse.osee.coverage.test.import1.CoveragePackage1Import1;
import org.eclipse.osee.coverage.test.import1.CoveragePackage1Import1B;
import org.eclipse.osee.coverage.test.import2.CoverageImport2TestNavigateItem;
import org.eclipse.osee.coverage.test.import2.CoveragePackage1Import2;
import org.eclipse.osee.coverage.test.import3.CoverageImport3TestNavigateItem;
import org.eclipse.osee.coverage.test.package1.CoveragePackage1;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItem;

/**
 * @author Donald G. Dunne
 */
public class CoverageTestNavigateViews implements ICoverageNavigateItem {

   public CoverageTestNavigateViews() {
      super();
   }

   public List<XNavigateItem> getNavigateItems() throws OseeCoreException {

      List<XNavigateItem> items = new ArrayList<XNavigateItem>();

      if (Activator.areOSEEServicesAvailable().isFalse()) {
         return items;
      }

      items.add(new CoverageImport1TestNavigateItem(null));
      items.add(new CoverageImport2TestNavigateItem(null));
      items.add(new CoverageImport3TestNavigateItem(null));
      items.add(new CoveragePackage1(null));
      items.add(new CoveragePackage1Import1(null));
      items.add(new CoveragePackage1Import1B(null));
      items.add(new CoveragePackage1Import2(null));

      return items;
   }

}
