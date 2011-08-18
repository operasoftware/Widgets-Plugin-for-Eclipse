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

package com.opera.widgets.ui.launcher;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.ui.ILaunchShortcut;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;

import com.opera.widgets.core.WidgetsCore;
import com.opera.widgets.ui.WidgetsActivator;

public class WidgetLaunchShortcut implements ILaunchShortcut {

    private static Process emulatorProcess = null;
    
    @Override
    public void launch(ISelection selection, String mode) {
	Object selectedElement = ((ITreeSelection) selection).getFirstElement();
	IResource selectedResource = (IResource) selectedElement;
	IProject project = selectedResource.getProject();
	runWidget(project);
    }

    @Override
    public void launch(IEditorPart editor, String mode) {
	IFile file = ((IFileEditorInput) editor.getEditorInput()).getFile();
	IProject project = file.getProject();

	runWidget(project);
    }

    private void runWidget(IProject project) {
	try {
	    project.build(IncrementalProjectBuilder.FULL_BUILD, null);
	    WidgetsActivator.getDefault().getPreferenceStore()
		    .getString(WidgetsCore.EMULATOR_PATH_PREFERENCE);
	    String wgtFile = project
		    .getFile(
			    "build" + File.separator + project.getName()
				    + ".wgt").getLocation().toOSString();

	    String emulatorPath = WidgetsActivator.getDefault()
		    .getPreferenceStore()
		    .getString(WidgetsCore.EMULATOR_PATH_PREFERENCE);
	    if (!emulatorPath.isEmpty() && !emulatorPath.endsWith("/")
		    && !emulatorPath.endsWith("\\")) {
		emulatorPath += File.separator;
	    }
	    String osName = System.getProperty("os.name").toLowerCase();

	    // TODO temporary solution!!! (emulator problems)
	    if(emulatorProcess != null){
		
		emulatorProcess.destroy();
		emulatorProcess = null;
	    }
	    if (osName.indexOf("win") >= 0) {
		if (emulatorPath.isEmpty()) {
		    emulatorPath = "C:\\Program Files\\Opera Mobile\\";
		}
		
		emulatorProcess = Runtime.getRuntime()
			.exec("\"" + emulatorPath + "OperaWidgetEmulator\" -sandbox \"" + wgtFile + "\"", null, new File(emulatorPath)); //$NON-NLS-1$
	    } else {
		emulatorProcess = Runtime.getRuntime().exec(
			"operawidgetemulator -sandbox " + wgtFile, null); //$NON-NLS-1$
	    }

	} catch (IOException e) {
	    MessageDialog
		    .openError(
			    WidgetsActivator.getDefault().getWorkbench()
				    .getActiveWorkbenchWindow().getShell(),
			    "An error occured",
			    "Could not run emulator.\nPlease check emulator path in Window->Preferences->Widgets->Emulator\n\n"
				    + e.getMessage());
	    e.printStackTrace();
	} catch (CoreException e) {
	    MessageDialog.openError(WidgetsActivator.getDefault()
		    .getWorkbench().getActiveWorkbenchWindow().getShell(),
		    "An error occured",
		    "Could not build project.\n" + e.getMessage());
	    e.printStackTrace();
	}
    }
}
