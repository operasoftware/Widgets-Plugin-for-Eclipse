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

package com.opera.widgets.ui.wizard.export;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "com.opera.widgets.ui.wizard.export.messages"; //$NON-NLS-1$
	public static String WidgetExportPage_BrowseLabel;
	public static String WidgetExportPage_ChooseProjectLabel;
	public static String WidgetExportPage_Description;
	public static String WidgetExportPage_DestinationLabel;
	public static String WidgetExportPage_OptionsLabel;
	public static String WidgetExportPage_WidgetSelectionLabel;
	public static String WidgetExportWizard_ConfirmReplaceDesc;
	public static String WidgetExportWizard_ConfirmReplaceTitle;
	public static String WidgetExportWizard_ErrorOccuredDesc;
	public static String WidgetExportWizard_ErrorOccuredTitle;
	public static String WidgetExportWizard_ExportWidgetPackage;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
