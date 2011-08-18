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

import java.net.URI;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;

import com.opera.widgets.ui.CreateWidgetProjectSupport;

/**
 * Wizard for new W3C Widget Project
 * 
 * @author Michal Borek
 * 
 */
public class ProjectNewWizard extends Wizard implements INewWizard {

    private WizardNewProjectCreationPage fPageOne;
    private TemplateChoosePage fTemplatePage;
    private String fProjectNature;

    public ProjectNewWizard(String projectNature) {
	fProjectNature = projectNature;

    }

    @Override
    public void init(IWorkbench workbench, IStructuredSelection selection) {

    }

    @Override
    public boolean performFinish() {

	String projectName = fPageOne.getProjectName();
	URI projectLocation = null;
	if (!fPageOne.useDefaults()) {
	    projectLocation = fPageOne.getLocationURI();
	}

	CreateWidgetProjectSupport projectSupport = new CreateWidgetProjectSupport(
		fProjectNature);
	try {
	    IProject newProject = projectSupport.createProject(projectName,
		    projectLocation, true);
	    // if (fTemplatePage.useTemplate()) {
	    // File destinationPath = new File(newProject
	    //			.getFolder("src").getRawLocationURI()); //$NON-NLS-1$
	    // URL templateUrl = WidgetsActivator
	    // .getDefault()
	    // .getBundle()
	    // .getEntry(
	    // "resources/templates/"
	    // + fTemplatePage.getTemplateFile());
	    // URI absoluteTemplateUrl = FileLocator.resolve(templateUrl)
	    // .toURI();
	    //
	    // ZipMaker.unzip(new File(absoluteTemplateUrl), destinationPath);
	    //
	    // }
	    newProject.refreshLocal(IResource.DEPTH_INFINITE, null);
	    newProject.touch(null);
	    return true;
	} catch (CoreException e) {
	    MessageDialog
		    .openError(
			    this.getShell(),
			    ProjectNewWizardMessages.ProjectNewWizard_ErrorOccuredTitle,
			    NLS.bind(
				    ProjectNewWizardMessages.ProjectNewWizard_ErrorOccuredDesc,
				    e.getMessage()));
	    return false;
	}
	// catch (IOException e) {
	// e.printStackTrace();
	// } catch (URISyntaxException e) {
	// e.printStackTrace();
	// }
	// return false;
    }

    @Override
    public void addPages() {
	super.addPages();

	fPageOne = new WizardNewProjectCreationPage(getWindowTitle());
	fPageOne.setTitle(getWindowTitle());
	fPageOne.setDescription(ProjectNewWizardMessages.ProjectNewWizard_WizardDescription);

	fTemplatePage = new TemplateChoosePage("Choose template");

	addPage(fPageOne);
	// addPage(fTemplatePage);
    }
}
