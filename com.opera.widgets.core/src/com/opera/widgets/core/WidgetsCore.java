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

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleActivator;

public class WidgetsCore extends Plugin implements BundleActivator {

    public static final String PLUGIN_ID = "com.opera.widgets.core"; //$NON-NLS-1$
    public static final String IGNORE_EXPORT_PREFERENCE = "ignoredFilesOnExport"; //$NON-NLS-1$
    public static final String EMULATOR_PATH_PREFERENCE = "emulatorPath"; //$NON-NLS-1$
    public static final String WIDGET_SRC_FOLDER = "src" + IPath.SEPARATOR; //$NON-NLS-1$
    public static final String[] LEGAL_FILTER_EXTENSIONS = { "*.gif;*.ico;*.jpeg;*.jpg;*.png;*.svg" }; //$NON-NLS-1$
    public static final String[] LEGAL_ICON_EXTENSIONS = {
	    "gif", "ico", "jpeg", "jpg", "png", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
	    "svg" }; //$NON-NLS-1$

    private static WidgetsCore plugin;
    private PreferenceManager fPreferenceManager;

    public WidgetsCore() {
	fPreferenceManager = new PreferenceManager();
	plugin = this;
    }

    public static WidgetsCore getDefault() {
	return plugin;
    }

    public PreferenceManager getPreferenceManager() {
	return fPreferenceManager;
    }
}
