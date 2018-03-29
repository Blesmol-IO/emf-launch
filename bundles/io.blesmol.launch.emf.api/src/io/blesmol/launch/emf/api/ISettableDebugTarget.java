package io.blesmol.launch.emf.api;

import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IProcess;

public interface ISettableDebugTarget extends IDebugTarget {

	public void setLaunch(ILaunch launch);
	
	public void setProcess(IProcess process);
}
