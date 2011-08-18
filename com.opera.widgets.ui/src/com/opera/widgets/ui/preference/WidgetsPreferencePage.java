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

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.opera.widgets.ui.WidgetsActivator;

public class WidgetsPreferencePage extends FieldEditorPreferencePage implements
	IWorkbenchPreferencePage {

    @Override
    public void init(IWorkbench workbench) {
	setPreferenceStore(WidgetsActivator.getDefault().getPreferenceStore());
	setDescription(Messages.WidgetsPreferencePage_PreferencePageChoiceLabel);

    }

    @Override
    protected void createFieldEditors() {
    }

}
