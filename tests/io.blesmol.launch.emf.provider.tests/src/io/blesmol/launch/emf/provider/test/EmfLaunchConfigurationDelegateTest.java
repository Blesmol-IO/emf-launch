package io.blesmol.launch.emf.provider.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.ArgumentCaptor;
import org.osgi.framework.BundleContext;

import io.blesmol.launch.emf.impl.EmfLaunchConfigurationDelegate;
import io.blesmol.launch.emf.provider.api.LaunchEmfProviderApi;

public class EmfLaunchConfigurationDelegateTest {

	public static class TestEmfLaunchConfigurationDelegate extends EmfLaunchConfigurationDelegate {

		public ILaunch getLaunch() {
			return this.launch;
		}

		public TestEmfLaunchConfigurationDelegate(ResourceSet rs) {
			this.rs = rs;
		}

		@Override
		protected ResourceSet resourceSet(ILaunchConfiguration configuration, String mode) throws CoreException {
			return this.rs;
		}
	};

	@Rule
	public TemporaryFolder tempFolder = new TemporaryFolder();

	@Test
	public void shouldLoadResource() throws Exception {
		// Mocks
		final BundleContext context = mock(BundleContext.class);
		final File tempFile = tempFolder.newFile();
		when(context.getDataFile(any())).thenReturn(tempFile);

		final ILaunch mockLaunch = mock(ILaunch.class);
		final ArgumentCaptor<IDebugTarget> targetArgument = ArgumentCaptor.forClass(IDebugTarget.class);

		final ILaunchConfiguration configuration = mock(ILaunchConfiguration.class);
		when(configuration.getAttribute(any(), anyString())).thenReturn(LaunchEmfProviderApi.DEFAULT_URI);

		// Resource set
		final ResourceSetImpl rs = new ResourceSetImpl();

		// uri converter
		TestURIConverter converter = new TestURIConverter();
		converter.activate(context);
		rs.setURIConverter(converter);

		// epackage
		rs.setPackageRegistry(new TestEPackageRegistry());

		// resource factory
		TestResourceFactoryRegistry rfRegistry = new TestResourceFactoryRegistry();
		rfRegistry.activate();
		rs.setResourceFactoryRegistry(rfRegistry);

		// adapter factory
		TestAdapterFactory af = new TestAdapterFactory();
		rs.getAdapterFactories().add(af);

		// Launch delegate
		TestEmfLaunchConfigurationDelegate delegate = new TestEmfLaunchConfigurationDelegate(rs);

		// LaunchConfiguration#launch call order:
		// * getLaunch
		// * preLaunch
		// * buildForLaunch
		// * finalLaunchCheck
		// * launch

		// Verify
		String mode = ILaunchManager.DEBUG_MODE;
		ILaunch gottenLaunch = delegate.getLaunch(configuration, mode);
		assertTrue(gottenLaunch == null);

		assertTrue(delegate.preLaunchCheck(configuration, mode, null));

		assertFalse(delegate.buildForLaunch(configuration, mode, null));

		assertTrue(delegate.finalLaunchCheck(configuration, mode, null));

		delegate.launch(configuration, mode, mockLaunch, null);

		verify(mockLaunch).addDebugTarget(targetArgument.capture());

		IDebugTarget actualTarget = targetArgument.getValue();
		assertTrue(actualTarget instanceof TestVogellaDebugTarget);
		TestVogellaDebugTarget vogellaTarget = (TestVogellaDebugTarget) actualTarget;
		TestVogellaThread vogellaThread = vogellaTarget.getThreads()[0];

		final CountDownLatch latch = new CountDownLatch(1);
		final IDebugEventSetListener listener = new IDebugEventSetListener() {

			@Override
			public void handleDebugEvents(DebugEvent[] events) {
				DebugEvent event = events[0];
				if (event.getKind() != DebugEvent.SUSPEND)
					return;
				latch.countDown();
				System.out.println(event);
			}
		};

		vogellaThread.setListener(listener);
		vogellaThread.suspend();
		assertTrue(latch.await(100, TimeUnit.MILLISECONDS));
		// If below test fails, increment wait time above
		assertTrue(vogellaThread.isSuspended());
		TestVogellaStackFrame vogellaStackFrame = vogellaThread.getTopStackFrame();
		assertNotNull(vogellaStackFrame);
		vogellaTarget.dispose();
	}
}