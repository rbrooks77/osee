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
package org.eclipse.osee.framework.skynet.core.artifact;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.logging.Level;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.db.connection.DbTransaction;
import org.eclipse.osee.framework.db.connection.OseeConnection;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.db.connection.exception.OseeStateException;
import org.eclipse.osee.framework.db.connection.exception.OseeWrappedException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeType;
import org.eclipse.osee.framework.skynet.core.attribute.providers.IAttributeDataProvider;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.internal.Activator;

/**
 * @author Ryan D. Brooks
 */
public abstract class Attribute<T> {
   private AttributeType attributeType;
   private Artifact artifact;
   private IAttributeDataProvider attributeDataProvider;
   private int attrId;
   private int gammaId;
   private boolean dirty;
   private ModificationType modificationType;

   void internalInitialize(AttributeType attributeType, Artifact artifact, ModificationType modificationType, boolean markDirty, boolean setDefaultValue) throws OseeCoreException {
      this.attributeType = attributeType;
      this.artifact = artifact;
      this.modificationType = modificationType;

      try {
         Constructor<? extends IAttributeDataProvider> providerConstructor =
               attributeType.getProviderAttributeClass().getConstructor(new Class[] {Attribute.class});
         attributeDataProvider = providerConstructor.newInstance(new Object[] {this});
      } catch (Exception ex) {
         throw new OseeWrappedException(ex);
      }

      if (setDefaultValue) {
         setToDefaultValue();
      }

      dirty = markDirty;
      if (dirty) {
         // Kick Local Event
         try {
            OseeEventManager.kickArtifactModifiedEvent(this, ArtifactModType.Changed, artifact);
         } catch (Exception ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }
      uponInitialize();
   }

   /**
    * Base implementation does nothing. Subclasses may override to do setup that depends on the attribute state data.
    * 
    * @throws OseeCoreException
    */
   protected void uponInitialize() throws OseeCoreException {
   }

   public void internalInitialize(AttributeType attributeType, Artifact artifact, ModificationType modificationType, int attributeId, int gammaId, boolean markDirty) throws OseeCoreException {
      internalInitialize(attributeType, artifact, modificationType, markDirty, false);
      this.attrId = attributeId;
      this.gammaId = gammaId;
   }

   public void setValue(T value) throws OseeCoreException {
      if (attributeType.getName().equals("Name") && !value.equals(getValue())) {
         // Confirm artifact is fit to rename
         for (IArtifactCheck check : ArtifactChecks.getArtifactChecks()) {
            IStatus result = check.isRenamable(Arrays.asList(artifact));
            if (!result.isOK()) {
               throw new OseeCoreException(result.getMessage());
            }
         }
      }

      if (subClassSetValue(value)) {
         markAsChanged(ModificationType.MODIFIED);
      }
   }

   public boolean setFromString(String value) throws OseeCoreException {
      if (attributeType.getName().equals("Name") && !value.equals(getValue())) {
         // Confirm artifact is fit to rename
         for (IArtifactCheck check : ArtifactChecks.getArtifactChecks()) {
            IStatus result = check.isRenamable(Arrays.asList(artifact));
            if (!result.isOK()) {
               throw new OseeCoreException(result.getMessage());
            }
         }
      }

      boolean response = subClassSetValue(convertStringToValue(value));
      if (response) {
         markAsChanged(ModificationType.MODIFIED);
      }
      return response;
   }

   protected abstract T convertStringToValue(String value) throws OseeCoreException;

   public final void resetToDefaultValue() throws OseeCoreException {
      modificationType = ModificationType.MODIFIED;
      setToDefaultValue();
   }

   private final void setToDefaultValue() throws OseeCoreException {
      String defaultValue = getAttributeType().getDefaultValue();
      if (defaultValue != null) {
         subClassSetValue(convertStringToValue(defaultValue));
      }
   }

   public boolean setValueFromInputStream(InputStream value) throws OseeCoreException {
      try {
         boolean response = setFromString(Lib.inputStreamToString(value));
         if (response) {
            markAsChanged(ModificationType.MODIFIED);
         }
         return response;
      } catch (IOException ex) {
         throw new OseeCoreException(ex);
      }
   }

   /**
    * Subclasses must provide an implementation of this method and in general should not override the other set value
    * methods
    * 
    * @param value
    * @throws OseeCoreException
    */
   protected abstract boolean subClassSetValue(T value) throws OseeCoreException;

   public abstract T getValue() throws OseeCoreException;

   public String getDisplayableString() throws OseeCoreException {
      return getAttributeDataProvider().getDisplayableString();
   }

   /*
    * (non-Javadoc)
    * 
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString() {
      StringBuilder builder = new StringBuilder();
      try {
         builder.append(getDisplayableString());
      } catch (OseeCoreException ex) {
         builder.append(Lib.exceptionToString(ex));
      }
      return builder.toString();
   }

   public IAttributeDataProvider getAttributeDataProvider() {
      return attributeDataProvider;
   }

   /**
    * @return <b>true</b> if this attribute is dirty
    */
   public boolean isDirty() {
      return dirty;
   }

   protected void markAsChanged(ModificationType modificationType) throws OseeStateException {
      dirty = true;
      this.modificationType = modificationType;

      if (modificationType != ModificationType.ARTIFACT_DELETED) {
         artifact.onAttributeModify();
      }

      // Kick Local Event
      try {
         OseeEventManager.kickArtifactModifiedEvent(this, ArtifactModType.Changed, artifact);
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   public void setNotDirty() {
      dirty = false;
   }

   public Artifact getArtifact() {
      return artifact;
   }

   /**
    * @return the attribute name/value description
    */
   public String getNameValueDescription() {
      return attributeType.getName() + ": " + toString();
   }

   /**
    * @return attributeType Attribute Type Information
    */
   public AttributeType getAttributeType() {
      return attributeType;
   }

   /**
    * Currently this method provides support for quasi attribute type inheritance
    * 
    * @param artifactType
    * @return whether this attribute's type or any of its super-types are the specified type
    */
   public boolean isOfType(String otherAttributeTypeName) {
      return attributeType.getName().equals(otherAttributeTypeName);
   }

   /**
    * Currently this method provides support for quasi attribute type inheritance
    * 
    * @param artifactType
    * @return whether this attribute's type or any of its super-types are the specified type
    */
   public boolean isOfType(AttributeType otherAttributeType) {
      return attributeType.equals(otherAttributeType);
   }

   public void resetModType() {
      this.modificationType = ModificationType.MODIFIED;
   }

   /**
    * Deletes the attribute
    * 
    * @throws OseeStateException
    */
   public final void setArtifactDeleted() throws OseeStateException {
      markAsChanged(ModificationType.ARTIFACT_DELETED);
   }

   /**
    * Deletes the attribute
    * 
    * @throws OseeStateException
    */
   public final void delete() throws OseeStateException {
      markAsChanged(ModificationType.DELETED);
   }

   public ModificationType getModificationType() {
      return modificationType;
   }

   public boolean canDelete() {
      try {
         return artifact.getAttributeCount(attributeType.getName()) > attributeType.getMinOccurrences();
      } catch (OseeCoreException ex) {
         return false;
      }
   }

   /**
    * Purges the attribute from the database.
    */
   public void purge() throws OseeCoreException {
      getAttributeDataProvider().purge();
   }

   public void markAsPurged() {
      modificationType = ModificationType.DELETED;
      dirty = false;
   }

   /**
    * @return true if in data store
    */
   public boolean isInDb() {
      return getGammaId() > 0;
   }

   /**
    * @return Returns the attrId.
    */
   public int getAttrId() {
      return attrId;
   }

   public int getGammaId() {
      return gammaId;
   }

   public void internalSetGammaId(int gammaId) {
      this.gammaId = gammaId;
   }

   public void internalSetAttributeId(int attrId) {
      this.attrId = attrId;
   }

   /**
    * @return the deleted
    */
   public boolean isDeleted() {
      return modificationType.isDeleted();
   }

   public void revert() throws OseeCoreException {
      DbTransaction dbTransaction = new DbTransaction() {
         @Override
         protected void handleTxWork(OseeConnection connection) throws OseeCoreException {
            ArtifactPersistenceManager.revertAttribute(connection, Attribute.this);
         }
      };
      dbTransaction.execute();
   }

   /**
    * @param modificationType the modificationType to set
    */
   public void internalSetModificationType(ModificationType modificationType) {
      this.modificationType = modificationType;
   }
}