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

import org.eclipse.jface.wizard.Wizard;

import com.opera.widgets.core.widget.Preference;

/**
 * Wizard used to add or modify widget's preference elements.
 * 
 * @author Michal Borek
 */
public class PreferenceWizard extends Wizard {

	protected PreferenceDetailsPage preferenceDetailsPage;
	protected Preference preference;
	protected List<Preference> preferences;

	public PreferenceWizard(List<Preference> preferences) {
		preference = new Preference();
		this.preferences = preferences;
	}

	public PreferenceWizard(Preference preference, List<Preference> preferences) {
		this.preference = preference;
		this.preferences = preferences;
	}

	@Override
	public boolean performFinish() {
		preference.setName(preferenceDetailsPage.getName());
		preference.setReadonly(preferenceDetailsPage.getReadOnly());
		preference.setValue(preferenceDetailsPage.getValue());
		return true;
	}

	@Override
	public void addPages() {
		preferenceDetailsPage = new PreferenceDetailsPage(
				"preferenceDetails", preference, preferences); //$NON-NLS-1$
		addPage(preferenceDetailsPage);
	}

	public Preference getPreference() {
		return preference;
	}

}
