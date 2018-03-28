package io.blesmol.launch.emf.provider.test;

import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.emf.ecore.resource.impl.ExtensibleURIConverterImpl;
import org.osgi.service.component.annotations.Component;

@Component(service = URIConverter.class, immediate = true, property = {
		"org.eclipse.emf.common.notify.type=org.eclipse.debug.core" })
public class TestURIConverter extends ExtensibleURIConverterImpl {

}
