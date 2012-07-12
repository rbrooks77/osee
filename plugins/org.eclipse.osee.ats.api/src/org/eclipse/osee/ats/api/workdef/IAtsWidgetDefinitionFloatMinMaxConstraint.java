/*
 * Created on Jun 21, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.api.workdef;

/**
 * @author Donald G. Dunne
 */
public interface IAtsWidgetDefinitionFloatMinMaxConstraint extends IAtsWidgetConstraint {

   public abstract void set(Double minValue, Double maxValue);

   public abstract Double getMinValue();

   public abstract Double getMaxValue();

}