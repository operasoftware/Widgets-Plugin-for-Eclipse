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

package com.opera.widgets.ui.wizard;

import org.eclipse.osgi.util.NLS;

public class ProjectNewWizardMessages extends NLS {
	private static final String BUNDLE_NAME = "com.opera.widgets.ui.messages"; //$NON-NLS-1$
	public static String ProjectNewWizard_WACProjectName;
	public static String ProjectNewWizard_CreateW3CWidgetProject;
	public static String ProjectNewWizard_ErrorOccuredDesc;
	public static String ProjectNewWizard_ErrorOccuredTitle;
	public static String ProjectNewWizard_W3CProjectName;
	public static String ProjectNewWizard_WizardDescription;
	public static String WidgetImportWizard_ErrorOccuredDesc;
	public static String WidgetImportWizard_ErrorOccuredTitle;
	public static String WidgetImportWizard_PageTitle;
	public static String WidgetImportWizard_Title;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, ProjectNewWizardMessages.class);
	}

	private ProjectNewWizardMessages() {
	}
}
