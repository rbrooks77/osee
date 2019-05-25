/*******************************************************************************
 * Copyright (c) 2019 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.search;

import java.util.HashMap;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;

/**
 * @author Ryan D. Brooks
 */
public class ArtifactQuerySelection<R> {
   private final R receiver;
   private final Supplier<R> receiverSupplier;
   private final HashMap<AttributeTypeToken<?>, BiConsumer<R, ?>> atts = new HashMap<>();

   public ArtifactQuerySelection(Supplier<R> receiverSupplier) {
      this.receiverSupplier = receiverSupplier;
      this.receiver = null;
   }

   public ArtifactQuerySelection(R receiver) {
      this.receiverSupplier = null;
      this.receiver = receiver;
   }

   public <T> void selectAtt(AttributeTypeToken<T> attributeType, BiConsumer<R, T> consumer) {
      atts.put(attributeType, consumer);
   }

   public <T> void accept(AttributeTypeToken<T> attributeType, T value) {
      BiConsumer<R, T> consumer = (BiConsumer<R, T>) atts.get(attributeType);
      consumer.accept(getReceiver(), value);
   }

   private R getReceiver() {
      return receiver == null ? receiverSupplier.get() : receiver;
   }

   public <T> void selectArtifactId(BiConsumer<R, ArtifactId> consumer) {

   }

   public <T> void selectArtifactType(BiConsumer<R, ArtifactTypeToken> consumer) {

   }
}