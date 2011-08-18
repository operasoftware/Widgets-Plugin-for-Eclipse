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
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.jface.viewers.IStructuredSelection;
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
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.opera.widgets.core.exception.InvalidConfigFileException;
import com.opera.widgets.core.exception.InvalidWidgetPackageException;

/**
 * Page for {@link WidgetImportWizard}
 * 
 * @author Michal Borek
 * @see WidgetImportWizard
 */
public class WidgetImportPage extends WizardPage implements Listener {

    private Combo sourceNameField;
    private Button sourceBrowseButton;
    private static final String WIDGET_EXTENSION = "wgt"; //$NON-NLS-1$
    protected static final int SIZING_TEXT_FIELD_WIDTH = 350;

    com.opera.widgets.core.widget.Widget widget;

    protected WidgetImportPage(String pageName, IStructuredSelection selection) {
	super(pageName);
	setTitle(pageName);
	setDescription(Messages.WidgetImportPage_Description);
    }

    @Override
    public void handleEvent(Event event) {
	Widget source = event.widget;

	if (source == sourceBrowseButton) {
	    handleSourceBrowseButtonPressed();
	}
	// check if it's possible to export widget with present data
	if (source == sourceBrowseButton || source == sourceNameField) {
	    if (dataValid()) {
		setPageComplete(true);
		((WidgetCreateProjectPage) getNextPage()).setProjectName(widget
			.getName(null));
	    } else {
		setPageComplete(false);
	    }
	}
    }

    /**
     * Creates group of controls related to file destination choice
     * 
     * @param parent
     */
    protected void createSourceGroup(Composite parent) {
	Font font = parent.getFont();
	Label sourceLabel = new Label(parent, SWT.NONE);
	sourceLabel.setText(Messages.WidgetImportPage_WidgetToImportLabel);
	sourceLabel.setFont(font);

	// source widget package entry field
	sourceNameField = new Combo(parent, SWT.SINGLE | SWT.BORDER);
	sourceNameField.addListener(SWT.Modify, this);
	GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL
		| GridData.GRAB_HORIZONTAL);
	data.widthHint = SIZING_TEXT_FIELD_WIDTH;
	sourceNameField.setLayoutData(data);
	sourceNameField.setFont(font);

	// destination browse button
	sourceBrowseButton = new Button(parent, SWT.PUSH);
	sourceBrowseButton.setText(Messages.WidgetImportPage_Browse);
	sourceBrowseButton.addListener(SWT.Selection, this);
	sourceBrowseButton.setFont(font);
    }

    /**
     * Handles pressing "browse" button to choose destination file
     */
    protected void handleSourceBrowseButtonPressed() {
	FileDialog dialog = new FileDialog(getContainer().getShell(), SWT.OPEN);
	dialog.setText(Messages.WidgetImportPage_WidgetFileSelectionLabel);
	dialog.setFilterExtensions(new String[] { "*." + WIDGET_EXTENSION }); //$NON-NLS-1$
	// dialog.setFilterPath(getSourceValue());
	String fileName = dialog.open();
	if (fileName != null) {
	    setErrorMessage(null);
	    sourceNameField.setText(fileName);
	}
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
	// createProjectGroup(container);
	createSourceGroup(container);

	setControl(container);
    }

    /**
     * Gets path of destination file
     * 
     * @return path to destination file
     */
    protected String getSourceValue() {
	return sourceNameField.getText().trim();
    }

    protected void setSourceValue(String value) {
	sourceNameField.setText(value.trim());
    }

    /**
     * Returns true if wizard can be complete (project is selected and proper
     * file is chosen)
     * 
     * @return
     */
    private boolean dataValid() {
	if (!sourceNameField.getText().isEmpty()) {
	    File file = new File(sourceNameField.getText());
	    if (file.exists()) {
		try {
		    initializeWidgetFromZipFile(file);
		    return true;
		} catch (InvalidWidgetPackageException e) {
		}
	    }
	}
	return false;
    }

    /**
     * Creates widget configuration from .wgt file
     * 
     * @param widgetFile
     *            - .wgt package
     * @throws InvalidWidgetPackageException
     */
    void initializeWidgetFromZipFile(File widgetFile)
	    throws InvalidWidgetPackageException {
	ZipFile zipFile;
	try {
	    zipFile = new ZipFile(widgetFile);
	    ZipEntry configEntry = zipFile.getEntry("config.xml"); //$NON-NLS-1$
	    if (configEntry != null) {
		InputStream is = zipFile.getInputStream(configEntry);
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
			.newInstance();
		DocumentBuilder docBuilder = docBuilderFactory
			.newDocumentBuilder();

		Document doc = docBuilder.parse(is);
		doc.normalize();
		widget = new com.opera.widgets.core.widget.Widget();
		try {
		    widget.load(doc.getDocumentElement());
		} catch (InvalidConfigFileException e) {
		    e.printStackTrace();
		}
	    }
	} catch (ZipException e) {
	    e.printStackTrace();
	    throw new InvalidWidgetPackageException(
		    Messages.WidgetImportPage_ZipException);
	} catch (IOException e) {
	    e.printStackTrace();
	    throw new InvalidWidgetPackageException(
		    Messages.WidgetImportPage_IOException);
	} catch (ParserConfigurationException e) {
	    e.printStackTrace();
	    throw new InvalidWidgetPackageException(
		    Messages.WidgetImportPage_InvalidWidgetConfiguration);
	} catch (SAXException e) {
	    e.printStackTrace();
	    throw new InvalidWidgetPackageException(
		    Messages.WidgetImportPage_InvalidWidgetConfiguration);
	}
    }
}