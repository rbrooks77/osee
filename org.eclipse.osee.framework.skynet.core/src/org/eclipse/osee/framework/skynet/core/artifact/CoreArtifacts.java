/*******************************************************************************
 * Copyright (c) 2009 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.artifact;

import org.eclipse.osee.framework.core.data.IOseeType;

/**
 * @author Ryan D. Brooks
 */
public enum CoreArtifacts implements IOseeType {
   AbstractSoftwareRequirement("Abstract Software Requirement", "ABNAYPwV6H4EkjQ3+QQA"),
   AbstractTestResult("Abstract Test Result", "ATkaanWmHH3PkhGNVjwA"),
   CodeUnit("Code Unit", "AAMFDkEh216dzK1mTZgA"),
   IndirectSoftwareRequirement("Indirect Software Requirement", "AAMFDiC7HRQMqr5S0QwA"),
   Requirement("Requirement", "ABM_vxEEowY+8i2_q9gA"),
   SoftwareRequirementDrawing("Software Requirement Drawing", "ABNClhgUfwj6A3EAArQA"),
   SubsystemRequirement("Subsystem Requirement", "AAMFDiN9KiAkhuLqOhQA"),
   TestUnit("Test Unit", "ABM2d6uxUw66aSdo0LwA"),
   TestPlanElement("Test Plan Element", "ATi_kUpvPBiW2upYC_wA"),
   TestProcedure("Test Procedure", "AAMFDjsjiGhoWpqM4PQA"),
   TestResultWML("Test Result WML", "ATk6NKFFmD_zg1b_eaQA"),
   TestResultNative("Test Result Native", "ATkaanWmHH3PkhGNVjwA"),
   User("User", "AAMFDhmr+Dqqe5pn3kAA");

   private final String name;
   private final String guid;

   private CoreArtifacts(String name, String guid) {
      this.name = name;
      this.guid = guid;
   }

   public String getName() {
      return this.name;
   }

   public String getGuid() {
      return guid;
   }
}
