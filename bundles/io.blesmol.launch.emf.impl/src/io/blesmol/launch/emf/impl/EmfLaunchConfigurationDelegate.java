package io.blesmol.launch.emf.impl;

import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.ILaunchesListener2;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate2;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.edit.provider.IDisposable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.blesmol.launch.emf.api.ISettableDebugTarget;
import io.blesmol.launch.emf.api.LaunchEmfApi;

public abstract class EmfLaunchConfigurationDelegate implements ILaunchConfigurationDelegate2, ILaunchesListener2 {

	protected static final Logger logger = LoggerFactory.getLogger(EmfLaunchConfigurationDelegate.class);

	protected volatile ResourceSet rs;
	protected volatile URI uri;
	protected volatile Resource resource;
	protected volatile ILaunch launch;

	/**
	 * Have we been activated?
	 * 
	 * Subclasses should never set this atomic boolean; only get it
	 */
	protected final AtomicBoolean activated = new AtomicBoolean(false);

	/**
	 * Has our launch been terminated?
	 * 
	 * Subclasses should never set this atomic boolean; only get it
	 */
	protected final AtomicBoolean terminated = new AtomicBoolean(false);

	/**
	 * Have we been deactivated?
	 * 
	 * Subclasses should never set this atomic boolean; only get it
	 */
	protected final AtomicBoolean deactivated = new AtomicBoolean(false);

//	protected final ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();

	@SuppressWarnings("unchecked")
	protected <T> T adaptFirst(ResourceSet resourceSet, Resource resource, Class<T> cls) {
		Optional<AdapterFactory> maybeAdapterFactory = adapterFactories(resourceSet, cls).findFirst();
		if (maybeAdapterFactory.isPresent()) {
			return (T) maybeAdapterFactory.get().adapt(resource, cls);
		}
		return null;
	}

	/*
	 * Simulates EcoreUtil.getAdapterFactory
	 */
	protected <T> Stream<AdapterFactory> adapterFactories(ResourceSet rs, Class<T> cls) {
		return rs.getAdapterFactories().stream().filter((af) -> af.isFactoryForType(cls));
	}

	protected abstract ResourceSet resourceSet(ILaunchConfiguration configuration, String mode) throws CoreException;

	protected URI uri(ILaunchConfiguration configuration) throws CoreException {
		return URI.createURI(configuration.getAttribute(LaunchEmfApi.LaunchAttributes.URI, ""));
	}

	// LaunchConfiguration#launch call order:
	// * getLaunch
	// * preLaunch
	// * buildForLaunch
	// * finalLaunchCheck
	// * launch

	@Override
	public ILaunch getLaunch(ILaunchConfiguration configuration, String mode) throws CoreException {
		activate(configuration, mode);
		rs = resourceSet(configuration, mode);
		uri = uri(configuration);
		try {
			resource = rs.getResource(uri, true);
			return adaptFirst(rs, resource, ILaunch.class);
		} catch (Exception e) {		
			logger.warn("Could not adapt resource {} (rs {}) into an ILaunch", resource, rs);
		}
		return null;
	}

	@Override
	public boolean preLaunchCheck(ILaunchConfiguration configuration, String mode, IProgressMonitor monitor)
			throws CoreException {
		return rs != null && uri != null && resource != null;
	}

	@Override
	public boolean buildForLaunch(ILaunchConfiguration configuration, String mode, IProgressMonitor monitor)
			throws CoreException {
		return false;
	}

	@Override
	public boolean finalLaunchCheck(ILaunchConfiguration configuration, String mode, IProgressMonitor monitor)
			throws CoreException {
		return true;
	}

	@Override
	public void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor)
			throws CoreException {

		this.launch = launch;

		// By convention assumes service ranking order is set for adapter factories
		// First one should be the one that generates the primary
		// IProcess for the debug target
		adapterFactories(rs, IProcess.class).forEach(af -> {
			IProcess process = (IProcess) af.adapt(resource, IProcess.class);
			if (process != null) {
				launch.addProcess(process);
			}
		});

		if (mode.equals(ILaunchManager.DEBUG_MODE)) {
			ISettableDebugTarget target = adaptFirst(rs, resource, ISettableDebugTarget.class);
			if (target != null) {
				// By convention take the first process
				IProcess[] processes = launch.getProcesses();
				if (processes != null && processes.length > 0) {
					target.setProcess(processes[0]);
				}
				launch.addDebugTarget(target);
				target.setLaunch(launch);
			}
		}
	}

	// TODO: consider using
	@Override
	public void launchesAdded(ILaunch[] launches) {
	}

	@Override
	public void launchesChanged(ILaunch[] launches) {
	}

	@Override
	public void launchesRemoved(ILaunch[] launches) {
	}

	@Override
	public void launchesTerminated(ILaunch[] launches) {
		if (terminated.get())
			return;

		final ILaunch launch = this.launch;
		if (launch != null) {
			Arrays.stream(launches).filter(l -> l == launch).findFirst().ifPresent(l -> {
				terminated.set(true);
				deactivate();
			});
		}
	}

	/**
	 * Activate the subclass launch configuration delegate
	 */
	protected void doActivate(ILaunchConfiguration configuration, String mode) throws CoreException {
	};

	protected final void activate(ILaunchConfiguration configuration, String mode) throws CoreException {
		if (activated.getAndSet(true))
			return;

		doActivate(configuration, mode);
	}

	/**
	 * Deactivate the subclass launch configuration delegate
	 */
	protected void doDeactivate() {
	}

	protected final void deactivate() {
		if (deactivated.getAndSet(true))
			return;

		// Use canonical IDisposable to signal deactivation
		rs.getAdapterFactories().stream().filter(af -> af instanceof IDisposable).forEach(af -> {
			((IDisposable) af).dispose();
		});

		doDeactivate();
	}

}
