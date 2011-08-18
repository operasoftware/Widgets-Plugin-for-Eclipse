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

package com.opera.widgets.ui.editor.toolbar;

import java.util.Arrays;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jface.action.ControlContribution;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.PlatformUI;

import com.opera.widgets.ui.WidgetsActivator;
import com.opera.widgets.ui.editor.AbstractFormPage;

/**
 * Language Combo that can be added to a Section Toolbar.
 */
public class LanguageControlContribution extends ControlContribution {
    private org.eclipse.swt.widgets.Combo fLangsList;
    private IProject fProject;
    private AbstractFormPage fFormPage;

    public LanguageControlContribution(String id, IProject project,
	    AbstractFormPage formPage) {
	super(id);
	fProject = project;
	fFormPage = formPage;
    }

    @Override
    protected Control createControl(Composite parent) {
	fLangsList = new Combo(parent, SWT.READ_ONLY);
	fLangsList.setToolTipText(Messages.LanguageControlContribution_ChangeLanguageSection);
	FontData[] fd = fLangsList.getFont().getFontData();
	fd[0].setHeight(10);
	Font font = new Font(PlatformUI.getWorkbench().getDisplay(), fd);
	fLangsList.setFont(font);
	fLangsList.addSelectionListener(new SelectionAdapter() {
	    @Override
	    public void widgetSelected(SelectionEvent e) {
		if (fLangsList.getSelectionIndex() == fLangsList.getItemCount() - 1) {
		    handleAddLanguage();
		} else {
		    String selectedItem = fLangsList.getItem(fLangsList
			    .getSelectionIndex());
		    setSelectedLanguage(selectedItem);
		}
	    }

	    private void handleAddLanguage() {
		InputDialog dialog = new InputDialog(WidgetsActivator
			.getDefault().getWorkbench().getActiveWorkbenchWindow()
			.getShell(), Messages.LanguageControlContribution_AddNewLanguageLabel,
			Messages.LanguageControlContribution_TypeNewLanguageLabel, "", null); //$NON-NLS-2$ //$NON-NLS-1$
		if (dialog.open() == IStatus.OK) {
		    String newLanguage = dialog.getValue();
		    addLanguage(newLanguage);
		}
	    }
	});
	updateSelection();
	fLangsList.pack();
	return fLangsList;
    }

    private void addLanguage(String newLanguage) {
	fFormPage.getWidget().addLanguage(newLanguage);
	setSelectedLanguage(newLanguage);
	updateSelection();
    }

    private String[] getAvailableLanguages() {
	return fFormPage.getWidget().getAddedLanguages();
    }

    private void initList() {
	String[] items = getAvailableLanguages();
	fLangsList.removeAll();
	fLangsList.add("default"); //$NON-NLS-1$
	for (String item : items) {
	    fLangsList.add(item);
	}
	fLangsList.add(Messages.LanguageControlContribution_AddButtonLabel);
    }

    public void updateSelection() {
	initList();
	String[] items = getAvailableLanguages();

	String selectedLanguage = fFormPage.getSelectedLanguage();
	int selectedIndex = Arrays.asList(items).indexOf(selectedLanguage);
	if (selectedIndex > -1) {
	    fLangsList.select(selectedIndex + 1);
	} else {
	    fLangsList.select(0);
	}
    }

    private void setSelectedLanguage(String newSelection) {
	String selectedLanguage = fFormPage.getSelectedLanguage();
	if (selectedLanguage != newSelection) {
	    try {
		fProject.setPersistentProperty(new QualifiedName(
			WidgetsActivator.PLUGIN_ID, "selectedLanguage"), //$NON-NLS-1$
			newSelection);
	    } catch (CoreException e) {
		e.printStackTrace();
	    }
	    fFormPage.updateEditor();
	}
    }
}