/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.widgets.xHistory;

import org.eclipse.osee.framework.core.model.TransactionRecord;

/**
 * @author Donald G. Dunne
 */
public interface IHistoryTransactionProvider {

   public TransactionRecord getTransactionRecord(Long id);

   public void put(Long id, TransactionRecord transaction);

}
