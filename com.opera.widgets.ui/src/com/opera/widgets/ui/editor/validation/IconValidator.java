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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IManagedForm;

public class IconValidator extends AbstractControlValidation {

    private static final String ICONS_ROOT_FOLDER = "src/"; //$NON-NLS-1$
    IProject project;
    String iconPath;

    public IconValidator(IManagedForm managedForm, Text control,
	    IProject project) {
	super(managedForm, control);
	this.project = project;
    }

    @Override
    protected boolean validateControl() {
	iconPath = ((Text) getControl()).getText();
	if(project != null){
	IFile iconFile = project.getFile(ICONS_ROOT_FOLDER + iconPath);
	return iconFile.exists();
	}
	return true; 
    }

    @Override
    protected String getMessage() {
	return NLS.bind(Messages.IconValidator_IconNotFoundError, iconPath);
    }
}
