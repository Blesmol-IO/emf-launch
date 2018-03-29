package io.blesmol.launch.emf.provider.test;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.BinaryResourceImpl;
import org.eclipse.emf.ecore.resource.impl.ResourceFactoryRegistryImpl;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

@Component(service = Resource.Factory.Registry.class, immediate = true, property = {
		TestResourceFactoryRegistry.NOTIFY_TYPE })
public class TestResourceFactoryRegistry extends ResourceFactoryRegistryImpl {

	protected static final String NOTIFY_TYPE = "org.eclipse.emf.common.notify.type=org.eclipse.debug.core";

	@Activate
	void activate() {
		extensionToFactoryMap.put(DEFAULT_EXTENSION, new BinaryResourceFactory());
	}

	public class BinaryResourceFactory implements Resource.Factory {

		@Override
		public Resource createResource(URI uri) {
			return new BinaryResourceImpl(uri);
		}
	}
}
