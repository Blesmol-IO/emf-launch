package io.blesmol.launch.emf.provider;

import java.util.Hashtable;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.osgi.service.component.ComponentFactory;
import org.osgi.service.component.ComponentInstance;

import io.blesmol.launch.emf.impl.EmfLaunchConfigurationDelegate;

/*
 * This class is instantiated via the Eclipse debugging environment and not OSGi directly.
 * However, it consumes and provides OSGi services.
 */
public class OsgiEmfLaunchConfigurationDelegate extends EmfLaunchConfigurationDelegate {

	private volatile ComponentInstance component;

	@Override
	protected ResourceSet resourceSet(ILaunchConfiguration configuration, String mode) throws CoreException {
		ComponentFactory rsFactory = ProviderActivator.INSTANCE.getResourceSetFactory();
		component = rsFactory.newInstance(new Hashtable<>(configuration.getAttributes()));
		return (ResourceSet) component.getInstance();
	}

	@Override
	protected void doDeactivate() {
		component.dispose();
	}

}
