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

package com.opera.widgets.core.preference;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

import com.opera.widgets.core.WidgetsCore;

public class WidgetPreferenceInitializer extends AbstractPreferenceInitializer {

    public WidgetPreferenceInitializer() {
    }

    @Override
    public void initializeDefaultPreferences() {

	IEclipsePreferences defaultPreferences = DefaultScope.INSTANCE
		.getNode(WidgetsCore.PLUGIN_ID);
	defaultPreferences.put(WidgetsCore.IGNORE_EXPORT_PREFERENCE,
		"\\..*;__MACOSX"); //$NON-NLS-1$

	defaultPreferences
		.put(WidgetsCore.EMULATOR_PATH_PREFERENCE, "/usr/bin");
    }
}
