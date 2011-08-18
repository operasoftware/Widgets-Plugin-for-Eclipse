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

package com.opera.widgets.ui.editor.validation;

import java.util.List;

import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Text;

import com.opera.widgets.core.widget.Preference;
import com.opera.widgets.ui.editor.dialog.PreferenceDetailsPage;

/**
 * Validator of name of widget's preference.
 * 
 * @author Michal Borek
 * 
 */
public class PreferenceNameValidator {

	List<Preference> preferences;
	private Text control;

	private ModifyListener modifyListener;
	private PreferenceDetailsPage preferenceDetailsPage;
	private String previousValue;
	public PreferenceNameValidator(PreferenceDetailsPage preferenceDetailsPage,
			Text control, List<Preference> preferences, String previousValue) {
		this.preferences = preferences;
		this.control = control;
		this.previousValue = previousValue;
		this.preferenceDetailsPage = preferenceDetailsPage;
		initialize();
	}

	protected boolean validateControl() {
		String string = control.getText();
		if (string.isEmpty()) {
			return false;
		}
		if(previousValue != null && !previousValue.isEmpty() && string.equals(previousValue)){
			return true;
		}
		for (Preference p : preferences) {
			if (p.getName().equals(string)) {
				return false;
			}
		}
		return true;
	}

	protected String getMessage() {
		return Messages.PreferenceNameValidator_PreferenceErrorNameExists;
	}

	private void createListeners() {
		modifyListener = new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				validate();
			}
		};

	}

	protected void validate() {
		boolean valid = validateControl();
		preferenceDetailsPage.setPageComplete(valid);
		if (!valid) {
			if (control.getText().isEmpty()) {
				preferenceDetailsPage
						.setErrorMessage(Messages.PreferenceNameValidator_PreferenceErrorEmptyName);
			} else {
				preferenceDetailsPage.setErrorMessage(getMessage());
			}
		}

	}

	protected void initialize() {
		createListeners();
		addListeners();
	}

	private void addListeners() {
		control.addModifyListener(modifyListener);

	}
}
