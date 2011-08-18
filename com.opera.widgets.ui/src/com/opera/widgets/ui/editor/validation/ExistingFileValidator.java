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

import org.eclipse.core.resources.IProject;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.forms.IManagedForm;

import com.opera.widgets.ui.WidgetsActivator;

public class ExistingFileValidator extends AbstractControlValidation {

    public ExistingFileValidator(IManagedForm managedForm, Text control) {
	super(managedForm, control);
    }

    @Override
    protected boolean validateControl() {
	IProject project = getProject();
	String string = ((Text) getControl()).getText();
	if (project != null && !string.isEmpty()) {
	    return project.getFile("src/" + string).exists(); //$NON-NLS-1$
	}
	return true;
    }

    @Override
    protected String getMessage() {
	return NLS.bind(Messages.ExistingFileValidator_FileDoesntExistsErrorDesc,
		((Text) getControl()).getText());
    }

    private IProject getProject() {
	IWorkbenchPage editorPage = WidgetsActivator.getDefault()
		.getWorkbench().getActiveWorkbenchWindow().getActivePage();
	if (editorPage != null) {
	    IEditorPart editorPart = editorPage.getActiveEditor();
	    if (editorPart == null) {
		return null;
	    }
	    IEditorInput editorInput = editorPart.getEditorInput();
	    if (editorInput instanceof IFileEditorInput) {
		IFileEditorInput input = (IFileEditorInput) editorInput;
		IProject activeProject = input.getFile().getProject();
		return activeProject;
	    } else {
		return null;
	    }
	}
	return null;

    }
}
