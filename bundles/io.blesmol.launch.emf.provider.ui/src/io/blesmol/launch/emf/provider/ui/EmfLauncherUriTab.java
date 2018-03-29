package io.blesmol.launch.emf.provider.ui;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import io.blesmol.launch.emf.api.LaunchEmfApi;
import io.blesmol.launch.emf.provider.api.LaunchEmfProviderApi;

public class EmfLauncherUriTab extends AbstractLaunchConfigurationTab {

	private Text text;

	// TODO: replace w/ parsley
	@Override
	public void createControl(Composite parent) {
		Composite comp = new Group(parent, SWT.BORDER);
		setControl(comp);

		GridLayoutFactory.swtDefaults().numColumns(2).applyTo(comp);

		Label label = new Label(comp, SWT.NONE);
		label.setText("Resource URI:");
		GridDataFactory.swtDefaults().applyTo(label);

		text = new Text(comp, SWT.BORDER);
		text.setMessage("Resource URI");
		GridDataFactory.fillDefaults().grab(true, false).applyTo(text);

	}

	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
	}

	@Override
	public void initializeFrom(ILaunchConfiguration configuration) {
		try {
			String uriText = configuration.getAttribute(LaunchEmfApi.LaunchAttributes.URI, LaunchEmfProviderApi.DEFAULT_URI);
			text.setText(uriText);
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		configuration.setAttribute(LaunchEmfApi.LaunchAttributes.URI, text.getText());
	}

	@Override
	public String getName() {
		return "URI";
	}

}
