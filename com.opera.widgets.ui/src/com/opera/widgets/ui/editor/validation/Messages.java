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

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "com.opera.widgets.ui.editor.validation.messages"; //$NON-NLS-1$
	public static String EmailValidator_InvalidEmail;
	public static String ExistingFileValidator_FileDoesntExistsErrorDesc;
	public static String IconValidator_IconNotFoundError;
	public static String LinkValidator_InvalidLink;
	public static String PositiveIntegerValidator_InvalidNumber;
	public static String PreferenceNameValidator_PreferenceErrorEmptyName;
	public static String PreferenceNameValidator_PreferenceErrorNameExists;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
