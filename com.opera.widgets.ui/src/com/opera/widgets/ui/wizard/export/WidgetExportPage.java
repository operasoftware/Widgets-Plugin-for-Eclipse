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

import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Widget;

import com.opera.widgets.ui.WidgetsActivator;

/**
 * Page for {@link WidgetExportWizard}
 * 
 * @author Michal Borek
 * @see WidgetExportWizard
 */
public class WidgetExportPage extends WizardPage implements Listener {

    private Combo projectField;
    private Combo destinationNameField;
    private Button destinationBrowseButton;
    private static final String WIDGET_EXTENSION = "wgt"; //$NON-NLS-1$
    protected static final int SIZING_TEXT_FIELD_WIDTH = 350;
    private IProject selectedProject;

    public IProject getSelectedProject() {
	return selectedProject;
    }

    public void setSelectedProject(IProject selectedProject) {
	this.selectedProject = selectedProject;
    }

    @Override
    public void createControl(Composite parent) {
	Composite container = new Composite(parent, SWT.NULL);
	GridLayout layout = new GridLayout();
	container.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL
		| GridData.VERTICAL_ALIGN_FILL));
	layout.verticalSpacing = 10;
	layout.numColumns = 3;
	container.setLayout(layout);
	createProjectGroup(container);
	createDestinationGroup(container);

	initializeFields();
	setControl(container);
    }

    protected WidgetExportPage(String pageName, IStructuredSelection selection) {
	super(pageName);
	setTitle(pageName);
	setDescription(Messages.WidgetExportPage_Description);
	if (selection instanceof ITreeSelection) {
	    Object selectedElement = ((ITreeSelection) selection)
		    .getFirstElement();
	    IResource selectedResource = (IResource) selectedElement;
	    if (selectedResource != null) {
		selectedProject = selectedResource.getProject();
	    }
	}
	setPageComplete(false);
    }

    @Override
    public void handleEvent(Event event) {
	Widget source = event.widget;

	if (source == destinationBrowseButton) {
	    handleDestinationBrowseButtonPressed();
	} else if (source == projectField) {
	    handleProjectFieldSelectionChange();
	}
	// check if it's possible to export widget with present data
	if (source == destinationBrowseButton || source == projectField
		|| source == destinationNameField) {
	    try {
		selectedProject.setPersistentProperty(new QualifiedName(
			WidgetsActivator.PLUGIN_ID, "export_destination"), //$NON-NLS-1$
			destinationNameField.getText().trim());
	    } catch (CoreException e) {
		e.printStackTrace();
	    }
	    setPageComplete(dataValid());
	}

    }

    /**
     * Handles selection change on project field (project to export selection)
     */
    private void handleProjectFieldSelectionChange() {
	int selectedProjectIndex = projectField.getSelectionIndex();
	if (selectedProjectIndex == -1) {
	    selectedProject = null;
	} else {
	    selectedProject = getProjects().get(selectedProjectIndex);
	}
    }

    /**
     * Creates group of controls related to project to export choice
     * 
     * @param parent
     */
    protected void createProjectGroup(Composite parent) {
	Font font = parent.getFont();
	Label label = new Label(parent, SWT.NONE);
	label.setText(Messages.WidgetExportPage_ChooseProjectLabel);
	label.setFont(font);

	// project list
	projectField = new Combo(parent, SWT.SINGLE | SWT.BORDER);
	projectField.addListener(SWT.Modify, this);
	projectField.addListener(SWT.Selection, this);
	GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
	data.widthHint = SIZING_TEXT_FIELD_WIDTH;
	data.horizontalSpan = 2;
	projectField.setLayoutData(data);
	projectField.setFont(font);
    }

    /**
     * Creates group of controls related to file destination choice
     * 
     * @param parent
     */
    protected void createDestinationGroup(Composite parent) {
	Font font = parent.getFont();
	Label destinationLabel = new Label(parent, SWT.NONE);
	destinationLabel.setText(Messages.WidgetExportPage_DestinationLabel);
	destinationLabel.setFont(font);

	// destination name entry field
	destinationNameField = new Combo(parent, SWT.SINGLE | SWT.BORDER);
	destinationNameField.addListener(SWT.Modify, this);
	destinationNameField.addListener(SWT.Selection, this);
	GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL
		| GridData.GRAB_HORIZONTAL);

	if (selectedProject != null) {
	    try {
		String exportDestination = selectedProject
			.getPersistentProperty(new QualifiedName(
				WidgetsActivator.PLUGIN_ID,
				"export_destination")); //$NON-NLS-1$
		if (exportDestination != null) {
		    destinationNameField.setText(exportDestination);
		}
	    } catch (CoreException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	}
	data.widthHint = SIZING_TEXT_FIELD_WIDTH;
	destinationNameField.setLayoutData(data);
	destinationNameField.setFont(font);

	// destination browse button
	destinationBrowseButton = new Button(parent, SWT.PUSH);
	destinationBrowseButton.setText(Messages.WidgetExportPage_BrowseLabel);
	destinationBrowseButton.addListener(SWT.Selection, this);
	destinationBrowseButton.setFont(font);
	// setButtonLayoutData(destinationBrowseButton);
    }

    /**
     * Handles pressing "browse" button to choose destination file
     */
    protected void handleDestinationBrowseButtonPressed() {
	FileDialog dialog = new FileDialog(getContainer().getShell(), SWT.SAVE
		| SWT.SHEET);
	dialog.setText(Messages.WidgetExportPage_WidgetSelectionLabel);
	dialog.setFilterExtensions(new String[] { "*." + WIDGET_EXTENSION }); //$NON-NLS-1$
	dialog.setFilterPath(getDestinationValue());
	if (selectedProject != null) {
	    dialog.setFileName(selectedProject.getName().replaceAll(
		    "^\\s+", "_") + "." + WIDGET_EXTENSION); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}
	String fileName = dialog.open();

	if (fileName != null) {
	    setErrorMessage(null);
	    setDestinationValue(fileName);
	    setPageComplete(true);
	}
    }

    private void initializeFields() {
	List<IProject> projects = getProjects();
	for (IProject project : projects) {
	    projectField.add(project.getName());
	}
	if (selectedProject != null) {
	    projectField.select(projects.indexOf(selectedProject));
	}
    }

    private List<IProject> getProjects() {
	return Arrays.asList(ResourcesPlugin.getWorkspace().getRoot()
		.getProjects());
    }

    /**
     * Gets path of destination file
     * 
     * @return path to destination file
     */
    protected String getDestinationValue() {
	return destinationNameField.getText().trim();
    }

    protected void setDestinationValue(String value) {
	destinationNameField.setText(value.trim());
    }

    /**
     * Returns true if wizard can be complete (project is selected and proper
     * file is chosen)
     * 
     * @return
     */
    private boolean dataValid() {
	if (selectedProject == null || getDestinationValue().isEmpty()) {
	    return false;
	}

	IFile configFile = selectedProject.getFile("src/config.xml"); //$NON-NLS-1$
	return configFile.exists();
    }
}