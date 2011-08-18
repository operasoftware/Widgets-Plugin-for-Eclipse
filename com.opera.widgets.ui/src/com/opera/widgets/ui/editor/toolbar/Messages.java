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

package com.opera.widgets.ui.editor.toolbar;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
    private static final String BUNDLE_NAME = "com.opera.widgets.ui.editor.toolbar.messages"; //$NON-NLS-1$
    public static String LanguageControlContribution_AddButtonLabel;
    public static String LanguageControlContribution_AddNewLanguageLabel;
    public static String LanguageControlContribution_ChangeLanguageSection;
    public static String LanguageControlContribution_TypeNewLanguageLabel;
    static {
	// initialize resource bundle
	NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}
