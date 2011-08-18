/*******************************************************************************
 * Copyright (c) 2010-2011 Opera Software.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Opera Software - initial API and implementation
 *******************************************************************************/

package com.opera.widgets.ui.launcher;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class WidgetTab extends AbstractLaunchConfigurationTab {

    private Text fPathText;

    @Override
    public void createControl(Composite parent) {
	Composite composite = new Composite(parent, SWT.NONE);
	composite.setLayout(new GridLayout(3, false));
	Label label = new Label(composite, SWT.None);
	label.setText("Widget Emulator path:");
	fPathText = new Text(composite, SWT.BORDER);

	Button browseButton = new Button(composite, SWT.PUSH);
	browseButton.setText("Browse");
	browseButton.addSelectionListener(new SelectionAdapter() {
	    @Override
	    public void widgetSelected(SelectionEvent e) {
		DirectoryDialog fileDialog = new DirectoryDialog(getShell());
		String fileName = fileDialog.open();
		fPathText.setText(fileName);
		WidgetTab.this.setDirty(true);
		scheduleUpdateJob();
		updateLaunchConfigurationDialog();
	    }
	});

	GridData gd = new GridData(GridData.FILL_HORIZONTAL);
	fPathText.setLayoutData(gd);
	setControl(composite);
	Dialog.applyDialogFont(composite);
    }

    @Override
    public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {

    }

    @Override
    public void initializeFrom(ILaunchConfiguration configuration) {
	try {
	    fPathText.setText(configuration.getAttribute("emulator_path", ""));
	} catch (CoreException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    @Override
    public void performApply(ILaunchConfigurationWorkingCopy configuration) {
	configuration.setAttribute("emulator_path", fPathText.getText());
    }

    @Override
    public String getName() {
	return Messages.WidgetTab_Widget;
    }
}
