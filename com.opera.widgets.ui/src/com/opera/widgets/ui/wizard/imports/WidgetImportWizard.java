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

import java.io.File;
import java.net.URI;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;

import com.opera.widgets.core.tools.ZipMaker;
import com.opera.widgets.ui.CreateWidgetProjectSupport;
import com.opera.widgets.ui.PluginImages;
import com.opera.widgets.ui.wizard.ProjectNewWizardMessages;

/**
 * Wizard for widget package import
 * 
 * @author Michal Borek
 */
public class WidgetImportWizard extends Wizard implements IExportWizard {

    private WidgetImportPage importPage;
    private WidgetCreateProjectPage projectPage;
    private String fNatureId;

    public WidgetImportWizard(String natureId) {
	fNatureId = natureId;
	setDefaultPageImageDescriptor(PluginImages.WIDGET_64PX);
    }

    @Override
    public void init(IWorkbench workbench, IStructuredSelection selection) {
	importPage = new WidgetImportPage(
		ProjectNewWizardMessages.WidgetImportWizard_Title, null);
	projectPage = new WidgetCreateProjectPage(
		ProjectNewWizardMessages.WidgetImportWizard_PageTitle);
	projectPage
		.setTitle(ProjectNewWizardMessages.WidgetImportWizard_PageTitle);

    }

    @Override
    public void addPages() {
	addPage(importPage);
	addPage(projectPage);
    }

    @Override
    public boolean performFinish() {
	File sourceFile = new File(importPage.getSourceValue());
	if (sourceFile.exists()) {
	    String projectName = projectPage.getProjectName();
	    URI location = null;
	    if (!projectPage.useDefaults()) {
		location = projectPage.getLocationURI();
	    }

	    try {
		IProject newProject = (new CreateWidgetProjectSupport(fNatureId))
			.createProject(projectName, location, false);
		File destinationPath = new File(newProject
			.getFolder("src").getRawLocationURI()); //$NON-NLS-1$
		ZipMaker.unzip(new File(importPage.getSourceValue()),
			destinationPath);
		newProject.refreshLocal(IResource.DEPTH_INFINITE, null);
		newProject.touch(null);
	    } catch (Exception e) {
		printErrorMessage(e);
		return false;
	    }
	    return true;
	}

	return false;
    }

    protected void printErrorMessage(Exception e) {
	MessageDialog.openError(this.getShell(), ProjectNewWizardMessages.WidgetImportWizard_ErrorOccuredTitle,
		ProjectNewWizardMessages.WidgetImportWizard_ErrorOccuredDesc + e.getMessage());

    }

}
