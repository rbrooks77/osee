/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.data;

import org.eclipse.osee.framework.jdk.core.type.BaseId;

/**
 * @author Ryan D. Brooks
 */
public final class ValueKind extends BaseId {
   public static final ValueKind Value = new ValueKind(1L);
   public static final ValueKind Id = new ValueKind(2L);
   public static final ValueKind Rationale = new ValueKind(3L);
   public static final ValueKind GammaId = new ValueKind(4L);
   public static final ValueKind TxId = new ValueKind(5L);
   /**
    * when combined with a single attribute type (typically CoreAttributeTypes.Name) this will result in ArtifactTokens
    */
   public static final ValueKind[] ValueAndId = new ValueKind[] {Value, Id};

   private ValueKind(Long id) {
      super(id);
   }
}