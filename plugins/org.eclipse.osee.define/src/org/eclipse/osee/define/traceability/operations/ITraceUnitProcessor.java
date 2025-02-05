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
package org.eclipse.osee.define.traceability.operations;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.define.traceability.data.TraceUnit;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Roberto E. Escobar
 */
public interface ITraceUnitProcessor {

   public void initialize(IProgressMonitor monitor);

   public void onComplete(IProgressMonitor monitor) throws OseeCoreException;

   public void clear();

   public void process(IProgressMonitor monitor, TraceUnit testUnit) throws OseeCoreException;
}
