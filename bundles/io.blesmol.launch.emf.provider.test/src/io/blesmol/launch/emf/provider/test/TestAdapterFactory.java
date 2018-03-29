package io.blesmol.launch.emf.provider.test;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;

import io.blesmol.launch.emf.api.ISettableDebugTarget;

/*
 * Use service.ranking to possibly ensure this adapter is chosen first
 */
@Component(service = AdapterFactory.class, immediate = true, property = {
		"org.eclipse.emf.common.notify.type=org.eclipse.debug.core",
		Constants.SERVICE_RANKING + "=" + Integer.MAX_VALUE })
public class TestAdapterFactory extends AdapterFactoryImpl {

	@Override
	public boolean isFactoryForType(Object type) {
		return (type == ISettableDebugTarget.class) ? true : super.isFactoryForType(type);
	}

	@Override
	public Adapter adapt(Notifier target, Object type) {
		return new TestVogellaDebugTarget();
	}
}
