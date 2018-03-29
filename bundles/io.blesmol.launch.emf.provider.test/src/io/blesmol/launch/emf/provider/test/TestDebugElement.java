package io.blesmol.launch.emf.provider.test;

import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.model.DebugElement;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.notify.impl.AdapterImpl;

import io.blesmol.launch.emf.api.ISettableDebugTarget;

public class TestDebugElement extends DebugElement implements Adapter {

	private Adapter delegate;
	// Testing
	private IDebugEventSetListener listener = new IDebugEventSetListener() {
		@Override
		public void handleDebugEvents(DebugEvent[] events) {
		}
	};

	public TestDebugElement(IDebugTarget target) {
		super(target);
		delegate = new AdapterImpl();
	}

	@Override
	public String getModelIdentifier() {
		return "io.blesmol.launch.emf.provider.test";
	}

	@Override
	public void notifyChanged(Notification notification) {
		delegate.notifyChanged(notification);
	}

	@Override
	public Notifier getTarget() {
		return delegate.getTarget();
	}

	@Override
	public void setTarget(Notifier newTarget) {
		delegate.setTarget(newTarget);
	}

	@Override
	public boolean isAdapterForType(Object type) {
		return (type == ISettableDebugTarget.class) ? true : delegate.isAdapterForType(type);
	}

	@Override
	public void fireEvent(DebugEvent event) {
		try {
			super.fireEvent(event);
		} catch (NullPointerException npe) {
			// not running in osgi, most likely testing
			listener.handleDebugEvents(new DebugEvent[] { event });
		}
	}

	public void setListener(IDebugEventSetListener listener) {
		this.listener = listener;
	}
}
