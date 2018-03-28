package io.blesmol.launch.emf.provider;

import java.util.Map;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EPackage.Registry;
import org.eclipse.emf.ecore.resource.Resource.Factory;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

import io.blesmol.launch.emf.provider.api.LaunchEmfProviderApi;

// FIXME: mostly copy-pasta from netty emf project; consider merging / depending upon
/*
 * A component factory requires the following to be registered,
 * per OSGi R6 Cmpn §112.5.5:
 * 
 * "Using the component properties specified by the component description,
 * all the component's references are satisfied. A reference is satisfied
 * when the reference specifies optional cardinality or there is at least
 * one target service for the reference."
 */
@Component(name = LaunchEmfProviderApi.COMPONENT_NAME, factory = LaunchEmfProviderApi.COMPONENT_FACTORY, service = ResourceSet.class)
public class LauncherResourceSetProvider extends ResourceSetImpl {

	private volatile String toString;

	private volatile URIConverter converter;
	private volatile EPackage.Registry ePackageRegistry;
	private volatile Factory.Registry factoryRegistry;
	
	private static final Object adapterFactoryLock = new Object();

	@Reference(target = LaunchEmfProviderApi.COMPONENT_TARGET, policy = ReferencePolicy.DYNAMIC, policyOption = ReferencePolicyOption.GREEDY)
	@Override
	public void setURIConverter(URIConverter uriConverter) {
		this.converter = uriConverter;
	}

	void unsetURIConverter(URIConverter converter) {
		this.converter = null;
	}

	@Override
	public URIConverter getURIConverter() {
		return this.converter;
	}

	@Reference(target = LaunchEmfProviderApi.COMPONENT_TARGET, policy = ReferencePolicy.DYNAMIC, policyOption = ReferencePolicyOption.GREEDY)
	@Override
	public void setPackageRegistry(EPackage.Registry packageRegistry) {
		this.ePackageRegistry = packageRegistry;
	}

	void unsetPackageRegistry(EPackage.Registry packageRegistry) {
		this.ePackageRegistry = null;
	}

	@Override
	public Registry getPackageRegistry() {
		return ePackageRegistry;
	}

	@Reference(target = LaunchEmfProviderApi.COMPONENT_TARGET, policy = ReferencePolicy.DYNAMIC, policyOption = ReferencePolicyOption.GREEDY)
	@Override
	public void setResourceFactoryRegistry(Factory.Registry factoryRegistry) {
		this.factoryRegistry = factoryRegistry;
	}

	void unsetResourceFactoryRegistry(Factory.Registry factoryRegistry) {
		this.factoryRegistry = null;
	}

	@Override
	public Factory.Registry getResourceFactoryRegistry() {
		return factoryRegistry;
	}

	@Reference(target = LaunchEmfProviderApi.COMPONENT_TARGET, policy = ReferencePolicy.DYNAMIC, policyOption = ReferencePolicyOption.GREEDY, cardinality = ReferenceCardinality.AT_LEAST_ONE)
	public void setAdapterFactory(AdapterFactory adapterFactory) {

		synchronized (adapterFactoryLock) {
			this.getAdapterFactories().add(adapterFactory);
		}
	}

	void unsetAdapterFactory(AdapterFactory adapterFactory) {
		synchronized (adapterFactoryLock) {
			this.getAdapterFactories().remove(adapterFactory);
		}
	}

	// called via ComponentFactory.newInstance(Dictionary)
	@Activate
	void activate(Map<String, Object> properties) {
		this.toString = (String) properties.getOrDefault(Constants.SERVICE_PID, super.toString());
	}

	@Deactivate
	void deactivate(Map<String, Object> properties) {

	}

	@Override
	public String toString() {
		return toString;
	}

}
