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
package org.eclipse.osee.disposition.rest;

import java.io.File;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.disposition.model.DispoItem;
import org.eclipse.osee.disposition.model.OperationReport;

/**
 * @author Angel Avila
 */
public interface DispoImporterApi {

   public abstract List<DispoItem> importDirectory(Map<String, DispoItem> exisitingItems, File filesDir, OperationReport report);

}
