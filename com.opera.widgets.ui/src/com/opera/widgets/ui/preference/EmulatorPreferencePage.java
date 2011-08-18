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

package com.opera.widgets.ui.preference;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.opera.widgets.core.PreferenceManager;
import com.opera.widgets.core.WidgetsCore;
import com.opera.widgets.ui.WidgetsActivator;

public class EmulatorPreferencePage extends PreferencePage implements
	IWorkbenchPreferencePage {

    private Text fPathText;

    @Override
    public void init(IWorkbench workbench) {
	setPreferenceStore(WidgetsActivator.getDefault().getPreferenceStore());
    }

    @Override
    protected Control createContents(Composite parent) {

	Composite composite = new Composite(parent, SWT.LEFT);
	composite.setLayoutData(new GridData(GridData.FILL_BOTH));
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
	    }
	});

	GridData gd = new GridData(GridData.FILL_HORIZONTAL);
	fPathText.setLayoutData(gd);

	fPathText.setText(getPreferenceStore().getString(
		WidgetsCore.EMULATOR_PATH_PREFERENCE));
	return null;
    }

    @Override
    public boolean performOk() {
	getPreferenceStore().setValue(WidgetsCore.EMULATOR_PATH_PREFERENCE,
		fPathText.getText());
	return super.performOk();
    }

    @Override
    protected void performDefaults() {
	PreferenceManager preferenceManager = WidgetsCore.getDefault()
		.getPreferenceManager();
	preferenceManager.setToDefault(WidgetsCore.EMULATOR_PATH_PREFERENCE);
	fPathText.setText("");
	super.performDefaults();
    }
}
