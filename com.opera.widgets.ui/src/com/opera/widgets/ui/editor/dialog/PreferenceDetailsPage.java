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

package com.opera.widgets.ui.editor.dialog;

import java.util.List;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.opera.widgets.core.widget.Preference;
import com.opera.widgets.ui.editor.validation.PreferenceNameValidator;

/**
 * Page of {@link PreferenceWizard}
 * 
 * @author Michal Borek
 * @see PreferenceWizard
 */
public class PreferenceDetailsPage extends WizardPage {

	private Text name;
	private Text value;
	private Button readOnly;
	private Preference preference;
	private List<Preference> preferences;

	protected PreferenceDetailsPage(String pageName, Preference preference,
			List<Preference> preferences) {
		super(pageName);
		this.preference = preference;
		this.preferences = preferences;
		setTitle(Messages.PreferenceDetailsPage_PreferencesLabel);
		setPageComplete(false);
	}

	@Override
	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		composite.setLayout(layout);
		setControl(composite);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		new Label(composite, SWT.NONE).setText(Messages.PreferenceDetailsPage_NameLabel);
		name = new Text(composite, SWT.NONE);
		new PreferenceNameValidator(this, name, preferences, preference.getName());
		name.setLayoutData(gd);
		if (preference.getName() != null) {
			name.setText(preference.getName());
		}
		
		new Label(composite, SWT.NONE).setText(Messages.PreferenceDetailsPage_ValueLabel);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		value = new Text(composite, SWT.NONE);
		value.setLayoutData(gd);
		if (preference.getValue() != null) {
			value.setText(preference.getValue());
		}

		new Label(composite, SWT.NONE).setText(Messages.PreferenceDetailsPage_ReadOnlyLabel);
		readOnly = new Button(composite, SWT.CHECK);
		readOnly.setSelection(preference.isReadonly());
	}

	public String getName() {
		return name.getText();
	}

	public boolean getReadOnly() {
		return readOnly.getSelection();
	}

	public String getValue() {
		return value.getText();
	}
}