
/*
 * generated by Xtext
 */
 
package org.eclipse.osee.framework.ui;

import org.eclipse.xtext.ui.guice.AbstractGuiceAwareExecutableExtensionFactory;
import org.osgi.framework.Bundle;

import com.google.inject.Injector;

/**
 *@generated
 */
public class OseeTypesExecutableExtensionFactory extends AbstractGuiceAwareExecutableExtensionFactory {

	@Override
	protected Bundle getBundle() {
		return org.eclipse.osee.framework.internal.InternalTypesActivator.getInstance().getBundle();
	}
	
	@Override
	protected Injector getInjector() {
		return org.eclipse.osee.framework.internal.InternalTypesActivator.getInstance().getInjector("org.eclipse.osee.framework.OseeTypes");
	}
	
}
