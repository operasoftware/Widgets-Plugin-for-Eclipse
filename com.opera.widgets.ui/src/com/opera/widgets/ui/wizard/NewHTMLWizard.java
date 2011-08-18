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

import java.net.URL;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.osgi.framework.Bundle;

import com.opera.widgets.ui.WidgetsActivator;

public class NewHTMLWizard extends Wizard implements INewWizard {

    private static final String NEW_HTML_TEMPLATE_PATH = "/resources/html5template.html"; //$NON-NLS-1 //$NON-NLS-1$ //$NON-NLS-1$
    private NewHTMLWizardPage fPageOne;
    private IStructuredSelection fSelection;

    @Override
    public void init(IWorkbench workbench, IStructuredSelection selection) {
	fSelection = selection;
    }

    @Override
    public void addPages() {
	fPageOne = new NewHTMLWizardPage("HTMLFileCreationPage", fSelection); //$NON-NLS-1$
	addPage(fPageOne);
    }

    @Override
    public boolean performFinish() {
	fPageOne.getFileName();

	Bundle bundle = WidgetsActivator.getDefault().getBundle();

	URL htmlTemplate = bundle.getEntry(NEW_HTML_TEMPLATE_PATH);

	IFile file = fPageOne.createNewFile();

	if (file != null) {
	    try {
		file.setContents(htmlTemplate.openStream(), true, false, null);
		openEditor(file);
		return true;
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	}

	return false;

    }

    private void openEditor(final IFile file) {
	if (file == null) {
	    return;
	}
	getShell().getDisplay().asyncExec(new Runnable() {
	    public void run() {
		try {
		    IWorkbenchPage page = PlatformUI.getWorkbench()
			    .getActiveWorkbenchWindow().getActivePage();
		    IDE.openEditor(page, file, true);
		} catch (PartInitException e) {
		}
	    }
	});
    }
}
