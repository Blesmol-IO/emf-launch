package io.blesmol.launch.emf.provider.api;

import org.osgi.service.component.ComponentConstants;

public interface LaunchEmfProviderApi {

	String COMPONENT_FACTORY = "org.eclipse.debug.core";
	String COMPONENT_NAME = "org.eclipse.emf.ecore.resource.ResourceSet";

	String COMPONENT_FACTORY_TARGET = "(&" + "(" + ComponentConstants.COMPONENT_FACTORY + "=" + COMPONENT_FACTORY + ")"
			+ "(" + ComponentConstants.COMPONENT_NAME + "=" + COMPONENT_NAME + ")" + ")";

	String COMPONENT_TARGET = "(org.eclipse.emf.common.notify.type=" + COMPONENT_FACTORY + ")";

	String DEFAULT_URI = "blesmol://proxy@127.0.0.1:8080/";
}
