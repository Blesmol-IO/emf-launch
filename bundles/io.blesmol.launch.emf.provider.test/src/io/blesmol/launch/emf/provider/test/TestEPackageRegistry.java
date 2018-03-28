package io.blesmol.launch.emf.provider.test;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.impl.EPackageRegistryImpl;
import org.osgi.service.component.annotations.Component;

@Component(service = EPackage.Registry.class, immediate = true, property = {
		"org.eclipse.emf.common.notify.type=org.eclipse.debug.core" })
public class TestEPackageRegistry extends EPackageRegistryImpl {

	private static final long serialVersionUID = 5055358134243349382L;

}
