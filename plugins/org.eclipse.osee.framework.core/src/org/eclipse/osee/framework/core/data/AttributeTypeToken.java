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
package org.eclipse.osee.framework.core.data;

import java.io.InputStream;
import java.util.Date;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.framework.jdk.core.type.FullyNamed;
import org.eclipse.osee.framework.jdk.core.type.HasDescription;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.type.Named;
import org.eclipse.osee.framework.jdk.core.type.NamedId;
import org.eclipse.osee.framework.jdk.core.type.NamedIdBase;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;

/**
 * @author Ryan D. Brooks
 */
public interface AttributeTypeToken<T> extends AttributeTypeId, FullyNamed, HasDescription, NamedId {
   AttributeTypeToken<Object> SENTINEL = valueOf(Id.SENTINEL, Named.SENTINEL);

   String getMediaType();

   String getDefaultValue();

   Class<?> getValueType();

   default boolean isEnumerated() {
      return getValueType().equals(Enum.class);
   }

   default boolean isBooleanType() {
      return getValueType().equals(Enum.class);
   }

   default boolean isIntegerType() {
      return getValueType().equals(Integer.class);
   }

   default boolean isDateType() {
      return getValueType().equals(Date.class);
   }

   default boolean isFloatingType() {
      return getValueType().equals(Float.class);
   }

   default boolean isStringType() {
      return getValueType().equals(String.class);
   }

   default boolean isLongType() {
      return getValueType().equals(Long.class);
   }

   default boolean isArtifactIdType() {
      return getValueType().equals(ArtifactId.class);
   }

   public static <T> AttributeTypeToken<T> cast(AttributeTypeToken<?> attributeType, Class<T> clazz) {
      if (attributeType.getValueType().equals(clazz)) {
         return (AttributeTypeToken<T>) attributeType;
      }
      throw new OseeArgumentException("Attribute type %s has value type %s not %s", attributeType,
         attributeType.getValueType(), clazz.getName());
   }

   public static <T> AttributeTypeToken<T> valueOf(String id) {
      return valueOf(Long.valueOf(id), Named.SENTINEL);
   }

   public static <T> AttributeTypeToken<T> valueOf(int id, String name) {
      return valueOf(Long.valueOf(id), name, "", null, MediaType.TEXT_PLAIN);
   }

   public static <T> AttributeTypeToken<T> valueOf(Long id, String name) {
      return valueOf(id, name, "", null, MediaType.TEXT_PLAIN);
   }

   public static <T> AttributeTypeToken<T> valueOf(int id, String name, String description) {
      return valueOf(Long.valueOf(id), name, description, null, MediaType.TEXT_PLAIN);
   }

   public static <T> AttributeTypeToken<T> valueOf(Long id, String name, String description) {
      return valueOf(id, name, description, null, MediaType.TEXT_PLAIN);
   }

   public static <T> AttributeTypeToken<T> valueOf(Long id, String name, String description, Class<?> valueType, String mediaType) {
      final class AttributeTypeImpl extends NamedIdBase implements AttributeTypeToken<T> {
         private final String description;
         private final String mediaType;
         private final Class<?> valueType;

         public AttributeTypeImpl(Long txId, String name, String description, Class<?> valueType, String mediaType) {
            super(txId, name);
            this.description = description;
            this.mediaType = mediaType;
            this.valueType = valueType;
         }

         @Override
         public String getDescription() {
            return description;
         }

         @Override
         public String getMediaType() {
            return mediaType;
         }

         @Override
         public String getDefaultValue() {
            return null;
         }

         @Override
         public Class<?> getValueType() {
            return valueType;
         }
      }
      return new AttributeTypeImpl(id, name, description, valueType, mediaType);
   }

   public static AttributeTypeToken<String> createEnum(Long id, String name, String mediaType, String description, String... enumNames) {
      return valueOf(id, name, description, Enum.class, mediaType);
   }

   public static AttributeTypeToken<String> createString(Long id, String name, String mediaType, String description) {
      return valueOf(id, name, description, String.class, mediaType);
   }

   public static AttributeTypeToken<Boolean> createBoolean(Long id, String name, String mediaType, String description) {
      return valueOf(id, name, description, Boolean.class, mediaType);
   }

   public static AttributeTypeToken<Date> createDate(Long id, String name, String mediaType, String description) {
      return valueOf(id, name, description, String.class, mediaType);
   }

   public static AttributeTypeToken<ArtifactId> createArtifactId(Long id, String name, String mediaType, String description) {
      return valueOf(id, name, description, ArtifactId.class, mediaType);
   }

   public static AttributeTypeToken<BranchId> createBranchId(Long id, String name, String mediaType, String description) {
      return valueOf(id, name, description, BranchId.class, mediaType);
   }

   public static AttributeTypeToken<InputStream> createInputStream(Long id, String name, String mediaType, String description) {
      return valueOf(id, name, description, InputStream.class, mediaType);
   }

   public static AttributeTypeToken<Integer> createInteger(Long id, String name, String mediaType, String description) {
      return valueOf(id, name, description, Integer.class, mediaType);
   }
}