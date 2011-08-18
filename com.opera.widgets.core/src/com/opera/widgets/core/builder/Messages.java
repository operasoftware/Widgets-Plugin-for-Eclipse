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

package com.opera.widgets.core.builder;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
    private static final String BUNDLE_NAME = "com.opera.widgets.core.builder.messages"; //$NON-NLS-1$
    public static String ConfigConsistencyChecker_ErrorDeletingMarkers1;
    public static String ConfigConsistencyChecker_ErrorDeletingMarkersConfigDoesntExist;
    public static String ConfigFileErrorReporter_ContentSrcEmpty;
    public static String ConfigFileErrorReporter_IconNotFound;
    public static String ConfigFileErrorReporter_StartFileDoesntExist;
    public static String ConfigFileErrorReporter_StartFileNotFound;
    public static String ConfigFileErrorReporter_ValidatingWidgetConfigurationTitle1;
    public static String XMLErrorReporter_ErrorDeletingMarkerDesc;
    public static String XMLErrorReporter_ErrorDeletingMarkerTitle;
    static {
	// initialize resource bundle
	NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}
