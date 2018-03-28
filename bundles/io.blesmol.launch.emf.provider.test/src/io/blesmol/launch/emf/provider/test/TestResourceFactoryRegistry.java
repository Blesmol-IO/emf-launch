package io.blesmol.launch.emf.provider.test;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceFactoryRegistryImpl;
import org.osgi.service.component.annotations.Component;

@Component(service = Resource.Factory.Registry.class, immediate = true, property = {
		"org.eclipse.emf.common.notify.type=org.eclipse.debug.core" })
public class TestResourceFactoryRegistry extends ResourceFactoryRegistryImpl {

}
