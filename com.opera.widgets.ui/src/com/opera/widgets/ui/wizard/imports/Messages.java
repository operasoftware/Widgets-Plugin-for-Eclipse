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

package com.opera.widgets.ui.wizard.imports;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "com.opera.widgets.ui.wizard.imports.messages"; //$NON-NLS-1$
	public static String WidgetCreateProjectPage_BrowseButtonLabel;
	public static String WidgetCreateProjectPage_ContentDirectoryEmptyError;
	public static String WidgetCreateProjectPage_InvalidProjectContentsDirectory;
	public static String WidgetCreateProjectPage_LocationPathError;
	public static String WidgetCreateProjectPage_ProjectContentsLabel;
	public static String WidgetCreateProjectPage_ProjectExistsError;
	public static String WidgetCreateProjectPage_ProjectLocationEmptyError;
	public static String WidgetCreateProjectPage_ProjectNameLabel;
	public static String WidgetCreateProjectPage_SameLocationError;
	public static String WidgetCreateProjectPage_SelectProjectLocationLabel;
	public static String WidgetCreateProjectPage_UseDefaultLocationLabel;
	public static String WidgetImportPage_Browse;
	public static String WidgetImportPage_Description;
	public static String WidgetImportPage_InvalidWidgetConfiguration;
	public static String WidgetImportPage_IOException;
	public static String WidgetImportPage_WidgetFileSelectionLabel;
	public static String WidgetImportPage_WidgetToImportLabel;
	public static String WidgetImportPage_ZipException;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
