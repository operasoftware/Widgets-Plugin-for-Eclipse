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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.opera.widgets.core.PreferenceManager;
import com.opera.widgets.core.WidgetsCore;
import com.opera.widgets.ui.WidgetsActivator;

public class ExportPreferencePage extends PreferencePage implements
	IWorkbenchPreferencePage {

    private List fIgnoreList;

    @Override
    public void init(IWorkbench workbench) {
	setPreferenceStore(WidgetsActivator.getDefault().getPreferenceStore());
    }

    @Override
    protected Control createContents(Composite parent) {

	Composite top = new Composite(parent, SWT.LEFT);
	top.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	top.setLayout(new GridLayout(2, false));

	GridData gd = new GridData(GridData.FILL_HORIZONTAL);
	gd.horizontalSpan = 2;
	Label listLabel = new Label(top, SWT.NONE);
	listLabel.setText(Messages.ExportPreferencePage_IgnoredFileLabel);
	listLabel.setLayoutData(gd);

	gd = new GridData(GridData.FILL_HORIZONTAL);
	gd.horizontalSpan = 2;
	gd.verticalSpan = 5;
	gd.heightHint = 200;
	fIgnoreList = new List(top, SWT.BORDER | SWT.V_SCROLL);
	fIgnoreList.setItems(WidgetsActivator.getDefault()
		.getIgnoredExportElements());
	fIgnoreList.setLayoutData(gd);

	gd = new GridData();
	gd.widthHint = 100;

	Button addButton = new Button(top, SWT.PUSH);
	addButton.setText(Messages.ExportPreferencePage_AddButton);
	addButton.setLayoutData(gd);
	gd = new GridData(GridData.FILL_HORIZONTAL);
	final Text addField = new Text(top, SWT.BORDER);
	addField.setLayoutData(gd);
	addButton.addSelectionListener(new SelectionAdapter() {
	    @Override
	    public void widgetSelected(SelectionEvent e) {
		String text = addField.getText().trim();
		if (!text.isEmpty()) {
		    fIgnoreList.add(text);
		    addField.setText(""); //$NON-NLS-1$
		    fIgnoreList.redraw();
		}
	    }

	});
	gd = new GridData();
	gd.widthHint = 100;
	Button removeButton = new Button(top, SWT.PUSH);
	removeButton.setText(Messages.ExportPreferencePage_RemoveButton);
	removeButton.setLayoutData(gd);
	removeButton.addSelectionListener(new SelectionAdapter() {
	    @Override
	    public void widgetSelected(SelectionEvent e) {
		if (fIgnoreList.getSelectionIndex() > -1) {
		    fIgnoreList.remove(fIgnoreList.getSelectionIndex());
		}
	    }
	});
	return null;
    }

    @Override
    public boolean performOk() {
	PreferenceManager preferenceManager = WidgetsCore.getDefault()
		.getPreferenceManager();
	String[] items = fIgnoreList.getItems();
	StringBuffer sb = new StringBuffer();
	sb.append(items[0]);
	for (int i = 1; i < items.length; i++) {
	    sb.append(";"); //$NON-NLS-1$
	    sb.append(items[i]);
	}
	preferenceManager.setString(WidgetsCore.IGNORE_EXPORT_PREFERENCE,
		sb.toString());

	return super.performOk();
    }

    @Override
    protected void performDefaults() {
	PreferenceManager preferenceManager = WidgetsCore.getDefault()
		.getPreferenceManager();
	preferenceManager.setToDefault(WidgetsCore.IGNORE_EXPORT_PREFERENCE);
	fIgnoreList.setItems(WidgetsActivator.getDefault()
		.getIgnoredExportElements());
	super.performDefaults();
    }
}
