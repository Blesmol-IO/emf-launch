package io.blesmol.launch.emf.provider;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.ComponentFactory;
import org.osgi.util.tracker.ServiceTracker;

import io.blesmol.launch.emf.provider.api.LaunchEmfProviderApi;

public class ProviderActivator implements BundleActivator {

	BundleContext context;
	static ProviderActivator INSTANCE;
	ServiceTracker<ComponentFactory, ComponentFactory> rsFactoryTracker;

	@Override
	public void start(BundleContext context) throws Exception {
		INSTANCE = this;
		this.context = context;
		rsFactoryTracker = new ServiceTracker<>(context, context.createFilter(LaunchEmfProviderApi.COMPONENT_FACTORY_TARGET), null);
		rsFactoryTracker.open();
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		rsFactoryTracker.close();
		INSTANCE = null;
		this.context = null;
	}
	
	public ComponentFactory getResourceSetFactory() {
		return rsFactoryTracker.getService();
	}
}
