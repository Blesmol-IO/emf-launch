package io.blesmol.launch.emf.provider.test;

import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IMemoryBlock;
import org.eclipse.debug.core.model.IProcess;

import io.blesmol.launch.emf.api.ISettableDebugTarget;

public class TestVogellaDebugTarget extends TestDebugElement implements ISettableDebugTarget {

	private IProcess process;
	private ILaunch launch;
	private TestVogellaThread thread;

	public TestVogellaDebugTarget() {
		super(null);
		this.thread = new TestVogellaThread(this, "vogella");
		fireCreationEvent();
	}

	@Override
	public TestVogellaDebugTarget getDebugTarget() {
		return this;
	}

	public void setProcess(IProcess process) {
		this.process = process;
	}

	@Override
	public IProcess getProcess() {
		return this.process;
	}

	public void setLaunch(ILaunch launch) {
		this.launch = launch;
	}

	@Override
	public ILaunch getLaunch() {
		return launch;
	}

	@Override
	public boolean canTerminate() {
		return thread.canTerminate();
	}

	@Override
	public boolean isTerminated() {
		return thread.isTerminated();
	}

	@Override
	public void terminate() throws DebugException {
		thread.terminate();
	}

	@Override
	public boolean canResume() {
		return thread.canResume();
	}

	@Override
	public boolean canSuspend() {
		return thread.canSuspend();
	}

	@Override
	public boolean isSuspended() {
		return thread.isSuspended();
	}

	@Override
	public void resume() throws DebugException {
		thread.resume();
	}

	@Override
	public void suspend() throws DebugException {
		thread.suspend();
	}

	@Override
	public void breakpointAdded(IBreakpoint breakpoint) {
	}

	@Override
	public void breakpointRemoved(IBreakpoint breakpoint, IMarkerDelta delta) {
	}

	@Override
	public void breakpointChanged(IBreakpoint breakpoint, IMarkerDelta delta) {
	}

	@Override
	public boolean canDisconnect() {
		return false;
	}

	@Override
	public void disconnect() throws DebugException {
	}

	@Override
	public boolean isDisconnected() {
		return false;
	}

	@Override
	public boolean supportsStorageRetrieval() {
		return false;
	}

	@Override
	public IMemoryBlock getMemoryBlock(long startAddress, long length) throws DebugException {
		return null;
	}

	@Override
	public TestVogellaThread[] getThreads() throws DebugException {
		if (isTerminated()) {
			return new TestVogellaThread[0];
		}
		return new TestVogellaThread[] { thread };
	}

	@Override
	public boolean hasThreads() throws DebugException {
		return true;
	}

	@Override
	public String getName() throws DebugException {
		return "Example Debug target";
	}

	@Override
	public boolean supportsBreakpoint(IBreakpoint breakpoint) {
		return false;
	}

	public void dispose() {
		fireTerminateEvent();
	}

}
