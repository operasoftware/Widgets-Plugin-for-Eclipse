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

package com.opera.widgets.ui.editor;

import java.util.Iterator;

import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormText;

import com.opera.widgets.core.widget.Preference;
import com.opera.widgets.ui.IHelpContextIds;
import com.opera.widgets.ui.WidgetsActivator;
import com.opera.widgets.ui.editor.contentprovider.PreferencesContentProvider;
import com.opera.widgets.ui.editor.dialog.PreferenceWizard;
import com.opera.widgets.ui.editor.labelprovider.PreferencesLabelProvider;

/**
 * Preferences tab in Config editor. Contains list preferences associated with
 * widget.
 * 
 * @author Michal Borek
 * @see ConfigEditor
 */
public class PreferencesPage extends AbstractFormPage {

    public static final String PAGE_ID = "preferences"; //$NON-NLS-1$

    TableViewer fPreferenceViewer;

    public PreferencesPage(ConfigEditor editor) {
	super(editor, PAGE_ID, Messages.PreferencesPage_TabName);
    }

    @Override
    public void updateEditor() {
	super.updateEditor();
	fPreferenceViewer.setInput(getWidget().getPreferences());
    }

    @Override
    public String getHelpResource() {
	return IHelpContextIds.CONFIG_PREFERENCES;
    }

    @Override
    public boolean performGlobalAction(String id) {

	if (id.equals(ActionFactory.DELETE.getId())) {
	    handleDelete();
	    return true;
	}
	return false;
    }

    @Override
    protected void createFormContent(IManagedForm managedForm) {
	super.createFormContent(managedForm);
	createPreferencesSection(managedForm);
    }

    private void createPreferencesSection(final IManagedForm form) {
	Composite section = createSection(getManagedForm(),
		Messages.PreferencesPage_Title, "", 2); //$NON-NLS-1$
	fPreferenceViewer = createTableViewer(section);
	fToolkit.paintBordersFor(section);
	String[] titles = { Messages.PreferencesPage_NameColumn,
		Messages.PreferencesPage_ValueColumn,
		Messages.PreferencesPage_ReadOnlyColumn };
	int[] bounds = { 150, 150, 80 };
	int[] style = { SWT.NONE, SWT.NONE, SWT.CHECK };
	for (int i = 0; i < titles.length; i++) {
	    TableViewerColumn column = new TableViewerColumn(fPreferenceViewer,
		    style[i]);
	    column.getColumn().setText(titles[i]);
	    column.getColumn().setWidth(bounds[i]);
	    column.getColumn().setResizable(true);
	}
	Button addButton = addActionButton(section, form,
		Messages.OverviewPage_ButtonAdd);
	addButton.addSelectionListener(new SelectionAdapter() {
	    @Override
	    public void widgetSelected(SelectionEvent e) {
		PreferenceWizard wizard = new PreferenceWizard(getWidget()
			.getPreferences());
		WizardDialog dialog = new WizardDialog(WidgetsActivator
			.getDefault().getWorkbench().getActiveWorkbenchWindow()
			.getShell(), wizard);
		dialog.create();
		dialog.open();
		if (dialog.getReturnCode() == Status.OK) {
		    Preference preference = wizard.getPreference();
		    if (!getWidget().getPreferences().contains(preference)) {
			getWidget().add(preference);
			fPreferenceViewer.add(preference);
		    }
		}
	    }
	});

	Button removeButton = addActionButton(section, form,
		Messages.OverviewPage_ButtonRemove);

	removeButton.addSelectionListener(new SelectionAdapter() {
	    @Override
	    public void widgetSelected(SelectionEvent e) {
		handleDelete();
	    }
	});
	Button bProperties = addActionButton(section, form,
		Messages.OverviewPage_ButtonProperties);
	bProperties.addSelectionListener(new SelectionAdapter() {
	    @Override
	    public void widgetSelected(SelectionEvent e) {
		if (!fPreferenceViewer.getSelection().isEmpty()) {
		    openPropertiesDialog();
		}
	    }

	});

	fPreferenceViewer.setContentProvider(new PreferencesContentProvider());
	fPreferenceViewer.setLabelProvider(new PreferencesLabelProvider());
	fPreferenceViewer.addDoubleClickListener(new IDoubleClickListener() {
	    @Override
	    public void doubleClick(DoubleClickEvent event) {
		openPropertiesDialog();
	    }
	});

	// info text for preferences window
	FormText hint = new FormText(section, SWT.NONE);
	hint.setText(Messages.PreferencesPage_Hint, false, true);
	GridData gd = new GridData(GridData.FILL_HORIZONTAL);
	gd.horizontalSpan = 2;
	hint.setLayoutData(gd);

    }

    private void openPropertiesDialog() {
	Preference preferenceToEdit = (Preference) ((StructuredSelection) fPreferenceViewer
		.getSelection()).getFirstElement();
	PreferenceWizard wizard = new PreferenceWizard(preferenceToEdit,
		getWidget().getPreferences());
	WizardDialog dialog = new WizardDialog(WidgetsActivator.getDefault()
		.getWorkbench().getActiveWorkbenchWindow().getShell(), wizard);
	dialog.create();
	dialog.open();
	if (dialog.getReturnCode() == Status.OK) {
	    getWidget().update(preferenceToEdit);
	    fPreferenceViewer.update(preferenceToEdit, null);
	}
    }

    /**
     * Removes selected elements from preferences table
     */
    private void handleDelete() {
	if (!fPreferenceViewer.getSelection().isEmpty()) {
	    @SuppressWarnings("unchecked")
	    Iterator<Preference> iterator = (Iterator<Preference>) ((StructuredSelection) fPreferenceViewer
		    .getSelection()).iterator();
	    while (iterator.hasNext()) {
		Preference elementToRemove = iterator.next();
		getWidget().remove(elementToRemove);
		fPreferenceViewer.remove(elementToRemove);
		fPreferenceViewer.getTable().select(0);
	    }
	}
    }
}
