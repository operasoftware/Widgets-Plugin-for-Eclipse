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

package com.opera.widgets.ui.wizard.imports;

import java.io.File;
import java.net.URI;

import org.eclipse.core.filesystem.URIUtil;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

/**
 * Page for {@link WidgetImportWizard}
 * 
 * @author Michal Borek
 * @see WidgetImportWizard
 */
public class WidgetCreateProjectPage extends WizardPage implements Listener {

	protected WidgetCreateProjectPage(String pageName) {
		super(pageName);
		setPageComplete(false);
	}

	protected Text fProjectNameField;
	private Text fLocationPathField;
	private Button fBrowseButton;
	private Button fUseDefaultsButton;

	private Listener fProjectNameModifyListener = new Listener() {
		public void handleEvent(Event e) {
			if (defaultEnabled) {
				setLocationForSelection();
				setPageComplete(validatePage());
			}
		}

	};

	private Listener locationModifyListener = new Listener() {
		public void handleEvent(Event e) {
			setPageComplete(validatePage());
		}
	};
	private boolean defaultEnabled = true;

	private void setLocationForSelection() {
		fLocationPathField.setText(getDefaultLocation());
	}

	@Override
	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);

		composite.setLayout(new GridLayout());
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));

		createProjectNameGroup(composite);
		createProjectLocationGroup(composite);

		setPageComplete(validatePage());
		setErrorMessage(null);
		setMessage(null);
		setControl(composite);

	}

	@Override
	public void handleEvent(Event event) {
		// TODO Auto-generated method stub

	}

	private boolean validatePage() {

		String locationFieldContents = getProjectLocationFieldValue();

		if (locationFieldContents.equals("")) { //$NON-NLS-1$
			setErrorMessage(null);
			setMessage(Messages.WidgetCreateProjectPage_ContentDirectoryEmptyError);
			return false;
		}

		IPath path = new Path(""); //$NON-NLS-1$
		if (!path.isValidPath(locationFieldContents)) {
			setErrorMessage(Messages.WidgetCreateProjectPage_InvalidProjectContentsDirectory);
			return false;
		}

		// setProjectName(projectFile);

		if (ResourcesPlugin.getWorkspace().getRoot()
				.getProject(getProjectName()).exists()) {
			setErrorMessage(Messages.WidgetCreateProjectPage_ProjectExistsError);
			return false;
		}

		String locationMessage = validateLocation();
		if (locationMessage != null) {
			setErrorMessage(locationMessage);
			return false;
		}

		setErrorMessage(null);
		setMessage(null);
		return true;
	}

	public String getProjectName() {
		return fProjectNameField.getText().trim();
	}

	/**
	 * Creates the project location specification controls.
	 * 
	 * @param parent
	 *            the parent composite
	 */
	private final void createProjectLocationGroup(Composite parent) {
		GridLayout layout = new GridLayout();
		int columnsNum = 3;
		layout.numColumns = columnsNum;
		Composite projectGroup = new Composite(parent, SWT.NONE);

		projectGroup.setLayout(layout);
		projectGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		projectGroup.setFont(parent.getFont());

		fUseDefaultsButton = new Button(projectGroup, SWT.CHECK | SWT.RIGHT);
		fUseDefaultsButton.setText(Messages.WidgetCreateProjectPage_UseDefaultLocationLabel);
		fUseDefaultsButton.setSelection(defaultEnabled);
		GridData buttonData = new GridData();
		buttonData.horizontalSpan = columnsNum;
		fUseDefaultsButton.setLayoutData(buttonData);
		fUseDefaultsButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				defaultEnabled = fUseDefaultsButton.getSelection();
				fLocationPathField.setEnabled(!defaultEnabled);
				if (defaultEnabled) {
					fLocationPathField.setText(getDefaultLocation());
				}
			}

		});

		Label projectContentsLabel = new Label(projectGroup, SWT.NONE);
		projectContentsLabel.setText(Messages.WidgetCreateProjectPage_ProjectContentsLabel);
		projectContentsLabel.setFont(parent.getFont());

		createUserSpecifiedProjectLocationGroup(projectGroup);
	}

	/**
	 * Creates the project name specification controls.
	 * 
	 * @param parent
	 *            the parent composite
	 */
	private final void createProjectNameGroup(Composite parent) {
		Font font = parent.getFont();
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;

		Composite newProjectGroup = new Composite(parent, SWT.NONE);

		newProjectGroup.setFont(font);
		newProjectGroup.setLayout(layout);
		newProjectGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Label newProjectLabel = new Label(newProjectGroup, SWT.NONE);
		newProjectLabel.setText(Messages.WidgetCreateProjectPage_ProjectNameLabel);
		newProjectLabel.setFont(font);

		fProjectNameField = new Text(newProjectGroup, SWT.BORDER);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		// data.widthHint = SIZING_TEXT_FIELD_WIDTH;
		fProjectNameField.setFont(font);
		fProjectNameField.setLayoutData(data);
		fProjectNameField.addListener(SWT.Modify, fProjectNameModifyListener);
	}

	/**
	 * Creates the project location specification controls.
	 * 
	 * @param projectGroup
	 *            the parent composite
	 */
	private void createUserSpecifiedProjectLocationGroup(Composite projectGroup) {

		Font dialogFont = projectGroup.getFont();

		fLocationPathField = new Text(projectGroup, SWT.BORDER);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		// data.widthHint = SIZING_TEXT_FIELD_WIDTH;
		fLocationPathField.setLayoutData(data);
		fLocationPathField.setFont(dialogFont);
		fLocationPathField.setEnabled(!defaultEnabled);
		fLocationPathField.addListener(SWT.Modify, locationModifyListener);

		fBrowseButton = new Button(projectGroup, SWT.PUSH);
		fBrowseButton.setText(Messages.WidgetCreateProjectPage_BrowseButtonLabel);
		fBrowseButton.setFont(dialogFont);
		setButtonLayoutData(this.fBrowseButton);

		fBrowseButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				handleBrowseButtonPressed();
			}
		});

	}

	private void handleBrowseButtonPressed() {
		DirectoryDialog dialog = new DirectoryDialog(
				fLocationPathField.getShell(), SWT.SHEET);
		dialog.setMessage(Messages.WidgetCreateProjectPage_SelectProjectLocationLabel);

		fUseDefaultsButton.setSelection(false);

		String dirName = getProjectLocationFieldValue();
		if (dirName.length() == 0) {
			// dirName = previouslyBrowsedDirectory;
		}

		if (dirName.length() == 0) {
			dialog.setFilterPath(ResourcesPlugin.getWorkspace().getRoot()
					.getLocation().toOSString());
		} else {
			File path = new File(dirName);
			if (path.exists()) {
				dialog.setFilterPath(new Path(dirName).toOSString());
			}
		}

		String selectedDirectory = dialog.open();
		if (selectedDirectory != null) {
			// previouslyBrowsedDirectory = selectedDirectory;
			fLocationPathField.setText(selectedDirectory);
		}
	}

	public String validateLocation() {

		String location = fLocationPathField.getText();
		if (location.length() == 0) {
			return Messages.WidgetCreateProjectPage_ProjectLocationEmptyError;
		}

		URI newPath = new File(location).toURI();
		if (newPath == null) {
			return Messages.WidgetCreateProjectPage_LocationPathError;
		}
		IProject existingProject = ResourcesPlugin.getWorkspace().getRoot()
				.getProject(getProjectName());

		if (existingProject != null) {
			URI projectPath = existingProject.getLocationURI();
			if (projectPath != null && URIUtil.equals(projectPath, newPath)) {
				return Messages.WidgetCreateProjectPage_SameLocationError;
			}
		}
		if (!useDefaults()) {
			IStatus locationStatus = ResourcesPlugin.getWorkspace()
					.validateProjectLocationURI(null, newPath);
			if (!locationStatus.isOK()) {
				return locationStatus.getMessage();
			}
		}

		return null;
	}

	private String getProjectLocationFieldValue() {
		return fLocationPathField.getText().trim();
	}

	public URI getLocationURI() {
		return new File(getProjectLocationFieldValue()).toURI();
	}

	public boolean useDefaults() {
		return fUseDefaultsButton.getSelection();
	}

	private String getDefaultLocation() {
		return Platform.getLocation().append(getProjectName()).toOSString();
	}

	public void setProjectName(String projectName) {
		fProjectNameField.setText(projectName);
	}

}