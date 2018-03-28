package io.blesmol.launch.emf.provider.test;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;
import org.osgi.service.component.annotations.Component;

@Component(service = AdapterFactory.class, immediate = true, property = {
		"org.eclipse.emf.common.notify.type=org.eclipse.debug.core" })
public class TestAdapterFactory extends AdapterFactoryImpl {

}
