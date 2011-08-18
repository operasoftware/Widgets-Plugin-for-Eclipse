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

import java.io.File;
import java.io.IOException;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;

import com.opera.widgets.core.tools.ZipMaker;
import com.opera.widgets.ui.PluginImages;
import com.opera.widgets.ui.WidgetsActivator;

/**
 * Wizard for widget package export
 * 
 * @author Michal Borek
 */
public class WidgetExportWizard extends Wizard implements IExportWizard {

    private static final String BUILD_FOLDER_NAME = "build"; //$NON-NLS-1$
    private static final String TEMP_FOLDER_NAME = "temp"; //$NON-NLS-1$
    private static final String WIDGET_SRC_FOLDER = "src"; //$NON-NLS-1$
    private WidgetExportPage fExportPage;
    private String[] fIgnoredFiles;

    public WidgetExportWizard() {
	setDefaultPageImageDescriptor(PluginImages.WIDGET_64PX);
    }

    @Override
    public void init(IWorkbench workbench, IStructuredSelection selection) {
	fExportPage = new WidgetExportPage(
		Messages.WidgetExportWizard_ExportWidgetPackage, selection);
	fIgnoredFiles = WidgetsActivator.getDefault()
		.getIgnoredExportElements();

    }

    @Override
    public void addPages() {
	addPage(fExportPage);
    }

    /**
     * TODO make an Operation (Eclipse's Job)
     */
    @Override
    public boolean performFinish() {
	fIgnoredFiles = WidgetsActivator.getDefault()
		.getIgnoredExportElements();

	File destinationFile = new File(fExportPage.getDestinationValue());

	if (destinationFile.exists()) {
	    if (!MessageDialog.openQuestion(getContainer().getShell(),
		    Messages.WidgetExportWizard_ConfirmReplaceTitle, NLS.bind(
			    Messages.WidgetExportWizard_ConfirmReplaceDesc,
			    destinationFile.getAbsolutePath()))) {
		return false;
	    }
	}
	IFolder buildFolder = fExportPage.getSelectedProject().getFolder(
		BUILD_FOLDER_NAME);

	try {
	    if (!buildFolder.exists()) {
		buildFolder.create(true, false, null);
	    }
	    IFolder tempFolder = buildFolder.getFolder(TEMP_FOLDER_NAME);
	    if (tempFolder.exists()) {
		tempFolder.delete(true, null);
	    }
	    tempFolder.create(true, true, null);

	    IFolder srcFolder = fExportPage.getSelectedProject().getFolder(
		    WIDGET_SRC_FOLDER);
	    copyValidResources(srcFolder, tempFolder);

	    ZipMaker.zipDirectory(tempFolder.getLocation().toFile(),
		    destinationFile);
	    tempFolder.delete(true, null);
	    return true;
	} catch (Exception e) {
	    e.printStackTrace();
	    printErrorDialog(e);
	}
	return false;
    }

    private void printErrorDialog(Exception e) {
	MessageDialog.openError(this.getShell(),
		Messages.WidgetExportWizard_ErrorOccuredTitle,
		Messages.WidgetExportWizard_ErrorOccuredDesc + e.getMessage());

    }

    /**
     * Copy resources without hidden files (.svn, .git etc.)
     * 
     * @param srcFolder
     * @param destFolder
     *            TODO move to core
     * @throws CoreException
     * @throws IOException
     */
    private void copyValidResources(IFolder srcFolder, IFolder destFolder)
	    throws CoreException, IOException {
	IResource[] members = srcFolder.members();
	for (IResource m : members) {
	    if (!isFileIgnored(m.getName())) {
		switch (m.getType()) {
		case IResource.FOLDER:
		    IFolder newLocation = destFolder.getFolder(m.getName());
		    newLocation.create(true, true, null);
		    copyValidResources((IFolder) m, newLocation);
		    break;
		case IResource.FILE:
		    m.copy(destFolder.getFile(m.getName()).getFullPath(), true,
			    null);
		    break;
		}
	    }
	}
    }

    private boolean isFileIgnored(String file) {
	for (String s : fIgnoredFiles) {
	    if (file.matches(s)) {
		return true;
	    }
	}
	return false;
    }
}
