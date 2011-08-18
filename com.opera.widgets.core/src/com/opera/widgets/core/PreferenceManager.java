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
package com.opera.widgets.core;

import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;

public class PreferenceManager {

    private IEclipsePreferences fDefaultPrefs;
    private IEclipsePreferences fInstancePrefs;

    public PreferenceManager() {
	fDefaultPrefs = DefaultScope.INSTANCE.getNode(WidgetsCore.PLUGIN_ID);
	fInstancePrefs = InstanceScope.INSTANCE.getNode(WidgetsCore.PLUGIN_ID);
    }

    public String getString(String name) {
	return fInstancePrefs.get(name, fDefaultPrefs.get(name, null));

    }

    public void setString(String name, String value) {
	fInstancePrefs.put(name, value);
    }

    public void setToDefault(String name) {
	setString(name, fInstancePrefs.get(name, null));
    }

}
