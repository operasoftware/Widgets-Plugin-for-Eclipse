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

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "com.opera.widgets.ui.editor.dialog.messages"; //$NON-NLS-1$
	public static String AccessDetailsPage_OriginLabel;
	public static String AccessDetailsPage_SubdomainsLabel;
	public static String AccessDetailsPage_Title;
	public static String IconDetailsPage_AddIconTitle;
	public static String IconDetailsPage_BrowseLabel;
	public static String IconDetailsPage_ChooseIconLabel;
	public static String IconDetailsPage_Description;
	public static String IconDetailsPage_FilesystemButtonLabel;
	public static String IconDetailsPage_HeightLabel;
	public static String IconDetailsPage_IconExistsErrorDesc;
	public static String IconDetailsPage_IconExistsErrorTitle;
	public static String IconDetailsPage_IconLabel;
	public static String IconDetailsPage_WidthLabel;
	public static String PreferenceDetailsPage_NameLabel;
	public static String PreferenceDetailsPage_PreferencesLabel;
	public static String PreferenceDetailsPage_ReadOnlyLabel;
	public static String PreferenceDetailsPage_ValueLabel;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
