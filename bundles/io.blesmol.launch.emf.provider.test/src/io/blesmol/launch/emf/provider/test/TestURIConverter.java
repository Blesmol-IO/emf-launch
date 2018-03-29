package io.blesmol.launch.emf.provider.test;

import java.io.File;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.emf.ecore.resource.impl.BinaryResourceImpl;
import org.eclipse.emf.ecore.resource.impl.ExtensibleURIConverterImpl;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

@Component(service = URIConverter.class, immediate = true, property = {
		"org.eclipse.emf.common.notify.type=org.eclipse.debug.core" })
public class TestURIConverter extends ExtensibleURIConverterImpl {

	@Activate
	void activate(BundleContext context) throws Exception {
		File resourceFile = context.getDataFile("testResource.bin");
		URI uri = URI.createFileURI(resourceFile.getAbsolutePath());
		BinaryResourceImpl resource = new BinaryResourceImpl(uri);
		resource.save(null);
		getURIMap().put(URI.createURI("blesmol://proxy@127.0.0.1:8080/"), uri);
	}
}
