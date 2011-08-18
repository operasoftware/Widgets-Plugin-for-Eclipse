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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
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
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormText;

import com.opera.widgets.core.natures.WACWidgetNature;
import com.opera.widgets.core.widget.Access;
import com.opera.widgets.ui.IHelpContextIds;
import com.opera.widgets.ui.WidgetsActivator;
import com.opera.widgets.ui.editor.contentprovider.AccessContentProvider;
import com.opera.widgets.ui.editor.dialog.AccessWizard;
import com.opera.widgets.ui.editor.labelprovider.AccessLabelProvider;

/**
 * Access tab in Config Editor. Contains list of URLs widget can connect to.
 * 
 * @author Michal Borek
 * @see ConfigEditor
 */
public class AccessPage extends AbstractFormPage {

    public static final String PAGE_ID = "access"; //$NON-NLS-1$

    private Button allAccessButton;

    private TableViewer accessViewer;

    private Button removeButton;

    private Button propertiesButton;

    private Button addButton;

    private Button fNetworkButton;
    private Button fLocalFsButton;

    public AccessPage(ConfigEditor editor) {
	super(editor, PAGE_ID, Messages.AccessPage_Access);
    }

    @Override
    protected void createFormContent(IManagedForm managedForm) {
	super.createFormContent(managedForm);
	createAccessSection(managedForm);
	createJilAccessSection(managedForm);
    }

    /**
     * Creates section containing access settings (<access> node from
     * config.xml)
     */
    private void createAccessSection(final IManagedForm form) {
	Composite section = createSection(getManagedForm(),
		Messages.AccessPage_Title, "", 2); //$NON-NLS-1$

	allAccessButton = fToolkit
		.createButton(section,
			Messages.AccessPage_AccessToAllNetworkResourcesLabel,
			SWT.CHECK);

	GridData gd = new GridData(GridData.FILL_HORIZONTAL);
	gd.horizontalSpan = 2;
	allAccessButton.setLayoutData(gd);

	Label separator = fToolkit.createSeparator(section, SWT.HORIZONTAL);
	gd = new GridData(GridData.FILL_HORIZONTAL);
	gd.horizontalSpan = 2;
	separator.setLayoutData(gd);
	accessViewer = createTableViewer(section);
	fToolkit.paintBordersFor(section);

	String[] titles = { Messages.AccessPage_OriginColumn,
		Messages.AccessPage_SubdomainsColumn };

	int[] bounds = { 400, 50 };
	for (int i = 0; i < titles.length; i++) {
	    TableViewerColumn column = new TableViewerColumn(accessViewer,
		    SWT.NONE);
	    column.getColumn().setText(titles[i]);
	    column.getColumn().setWidth(bounds[i]);
	    column.getColumn().setResizable(true);
	}
	addButton = addActionButton(section, form,
		Messages.OverviewPage_ButtonAdd);
	removeButton = addActionButton(section, form,
		Messages.OverviewPage_ButtonRemove);
	propertiesButton = addActionButton(section, form,
		Messages.OverviewPage_ButtonProperties);
	addListeners();

	accessViewer.setContentProvider(new AccessContentProvider());
	accessViewer.setLabelProvider(new AccessLabelProvider());
	accessViewer.addDoubleClickListener(new IDoubleClickListener() {
	    @Override
	    public void doubleClick(DoubleClickEvent event) {
		openPropertiesDialog();
	    }
	});

	// info text for access window
	FormText hint = new FormText(section, SWT.NONE);
	hint.setText(Messages.AccessPage_Hint, false, true);
	gd = new GridData(GridData.FILL_HORIZONTAL);
	gd.horizontalSpan = 2;
	hint.setLayoutData(gd);

    }

    private void createJilAccessSection(final IManagedForm form) {
	Composite section = createSection(getManagedForm(),
		Messages.AccessPage_WACAccessSectionTitle,
		Messages.AccessPage_WACAccessSectionDesc, 3);

	fNetworkButton = form.getToolkit().createButton(section,
		Messages.AccessPage_AccessNetworkResourcesLabel, SWT.CHECK);
	fLocalFsButton = form.getToolkit().createButton(section,
		"Access local filesystem", SWT.CHECK);
	
	GridData gd = new GridData();
	gd.horizontalIndent = 15;
	fNetworkButton.setLayoutData(gd);
	fNetworkButton.addSelectionListener(new SelectionAdapter() {

	    @Override
	    public void widgetSelected(SelectionEvent e) {
		getWidget().setJilAccessNetwork(fNetworkButton.getSelection());
	    }
	});

	gd = new GridData();
	gd.horizontalIndent = 15;
	fLocalFsButton.setLayoutData(gd);
	fLocalFsButton.addSelectionListener(new SelectionAdapter() {

	    @Override
	    public void widgetSelected(SelectionEvent e) {
		getWidget().setJilAccessLocalFs(fLocalFsButton.getSelection());
	    }
	});
    }

    private void addListeners() {
	addButton.addSelectionListener(new SelectionAdapter() {

	    @Override
	    public void widgetSelected(SelectionEvent e) {
		handleAdd();
	    }
	});

	removeButton.addSelectionListener(new SelectionAdapter() {

	    @Override
	    public void widgetSelected(SelectionEvent e) {
		handleDelete();
	    }
	});

	propertiesButton.addSelectionListener(new SelectionAdapter() {
	    @Override
	    public void widgetSelected(SelectionEvent e) {
		if (!accessViewer.getSelection().isEmpty()) {
		    openPropertiesDialog();
		}
	    }
	});

	allAccessButton.addSelectionListener(new SelectionAdapter() {
	    @Override
	    public void widgetSelected(SelectionEvent e) {
		if (allAccessButton.getSelection()) {
		    getWidget().setAccessToAll();
		} else {
		    if (getWidget().isAccessToAll()) {
			Access a = new Access();
			a.setOrigin("*"); //$NON-NLS-1$
			getWidget().remove(a);
		    }
		}
		updateEditor();
	    }

	});
    }

    private void setTableEnabled(boolean enabled) {
	addButton.setEnabled(enabled);
	removeButton.setEnabled(enabled);
	propertiesButton.setEnabled(enabled);
	accessViewer.getTable().setEnabled(enabled);
    }

    @Override
    public void updateEditor() {
	super.updateEditor();
	boolean isAccessToAll = getWidget().isAccessToAll();
	allAccessButton.setSelection(isAccessToAll);
	setTableEnabled(!isAccessToAll);

	if (isAccessToAll) {
	    accessViewer.setInput(null);
	} else {
	    accessViewer.setInput(getWidget().getAccessList());
	}
	try {
	    IProject project = getProject();
	    if (project != null && project.hasNature(WACWidgetNature.NATURE_ID)) {
		fNetworkButton.setSelection(getWidget().getJilAccessNetwork());
		fLocalFsButton.setSelection(getWidget().getJilAccessLocalFs());
	    }
	} catch (CoreException e) {
	    e.printStackTrace();
	}

    }

    private void openPropertiesDialog() {
	Access accessElementToEdit = (Access) ((StructuredSelection) accessViewer
		.getSelection()).getFirstElement();
	AccessWizard wizard = new AccessWizard(accessElementToEdit);
	WizardDialog dialog = new WizardDialog(WidgetsActivator.getDefault()
		.getWorkbench().getActiveWorkbenchWindow().getShell(), wizard);
	dialog.create();
	dialog.open();
	if (dialog.getReturnCode() == Status.OK) {
	    getWidget().update(accessElementToEdit);
	    accessViewer.update(accessElementToEdit, null);
	}
    }

    @Override
    public String getHelpResource() {
	return IHelpContextIds.CONFIG_ACCESS;
    }

    private void handleDelete() {
	if (!accessViewer.getSelection().isEmpty()) {
	    Access elementToRemove = (Access) ((StructuredSelection) accessViewer
		    .getSelection()).getFirstElement();
	    getWidget().remove(elementToRemove);
	    accessViewer.remove(elementToRemove);
	}
    }

    @Override
    public boolean performGlobalAction(String id) {

	if (id.equals(ActionFactory.DELETE.getId())) {
	    handleDelete();
	    return true;
	}
	return false;
    }

    /**
     * Handles add access element action
     */
    private void handleAdd() {
	AccessWizard wizard = new AccessWizard();
	WizardDialog dialog = new WizardDialog(WidgetsActivator.getDefault()
		.getWorkbench().getActiveWorkbenchWindow().getShell(), wizard);
	dialog.create();
	dialog.open();
	if (dialog.getReturnCode() == Status.OK) {
	    getWidget().add(wizard.getAccessElement());
	    accessViewer.add(wizard.getAccessElement());
	}
    }
}
