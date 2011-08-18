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

package com.opera.widgets.ui.editor.dialog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Arrays;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;

import com.opera.widgets.core.WidgetsCore;
import com.opera.widgets.core.widget.Icon;
import com.opera.widgets.ui.WidgetsActivator;

/**
 * Page of {@link IconWizard}
 * 
 * @author Michal Borek
 * @see IconWizard
 */
public class IconDetailsPage extends WizardPage {

    // max icon size (width or height) in pixels
    private static final int MAX_ICON_SIZE = 128;

    private Text fPath;
    private Text fWidth;
    private Text fHeight;
    private Icon fIcon;
    private Canvas fCanvas;
    private Image fIconImage;
    private Image fIconImageScaled;

    protected IconDetailsPage(String pageName, Icon icon) {
	super(pageName);
	this.fIcon = icon;
	setTitle(Messages.IconDetailsPage_AddIconTitle);
	setDescription(Messages.IconDetailsPage_Description);
	if (icon.getPath() == null) {
	    setPageComplete(false);
	}
    }

    @Override
    public void createControl(Composite parent) {
	final Composite composite = new Composite(parent, SWT.NONE);
	GridLayout layout = new GridLayout(4, false);
	composite.setLayout(layout);
	setControl(composite);
	GridData gd = new GridData(GridData.FILL_HORIZONTAL);
	new Label(composite, SWT.NONE)
		.setText(Messages.IconDetailsPage_IconLabel);
	fPath = new Text(composite, SWT.NONE);
	fPath.setLayoutData(gd);
	fPath.addModifyListener(new ModifyListener() {

	    @Override
	    public void modifyText(ModifyEvent e) {
		handleModifyPath(composite);
	    }

	});

	Button browse = new Button(composite, SWT.BORDER);
	browse.setText(Messages.IconDetailsPage_BrowseLabel);
	browse.addListener(SWT.Selection, new Listener() {
	    @Override
	    public void handleEvent(Event event) {
		handleBrowseForIcon(composite);
	    }
	});

	Button filesystem = new Button(composite, SWT.BORDER);
	filesystem.setText(Messages.IconDetailsPage_FilesystemButtonLabel);
	filesystem.addListener(SWT.Selection, new Listener() {
	    @Override
	    public void handleEvent(Event event) {
		handleBrowseFromFilesystem(composite);

	    }
	});

	new Label(composite, SWT.NONE)
		.setText(Messages.IconDetailsPage_WidthLabel);
	fWidth = new Text(composite, SWT.NONE);
	gd = new GridData();
	gd.horizontalSpan = 3;
	gd.minimumWidth = 100;
	gd.widthHint = 100;
	fWidth.setLayoutData(gd);
	fWidth.addModifyListener(new ModifyListener() {

	    @Override
	    public void modifyText(ModifyEvent e) {
		showIcon(composite);
	    }
	});

	new Label(composite, SWT.NONE)
		.setText(Messages.IconDetailsPage_HeightLabel);
	fHeight = new Text(composite, SWT.NONE);
	gd = new GridData();
	gd.horizontalSpan = 3;
	gd.minimumWidth = 100;
	gd.widthHint = 100;
	fHeight.setLayoutData(gd);
	fHeight.addModifyListener(new ModifyListener() {

	    @Override
	    public void modifyText(ModifyEvent e) {
		showIcon(composite);
	    }
	});

	FormText hint = new FormText(composite, SWT.None);
	// hint to add
	hint.setText("", false, true); //$NON-NLS-1$
	gd = new GridData(GridData.FILL_HORIZONTAL);
	gd.horizontalSpan = 4;
	hint.setLayoutData(gd);

	Label separator = new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL);
	gd = new GridData(GridData.FILL_HORIZONTAL);
	gd.verticalSpan = 4;
	gd.horizontalSpan = 4;
	separator.setLayoutData(gd);

	fCanvas = new Canvas(composite, SWT.NULL);
	gd = new GridData();
	gd.horizontalSpan = 4;
	gd.widthHint = 130;
	gd.heightHint = 130;
	fCanvas.setLayoutData(gd);
	fCanvas.addPaintListener(new PaintListener() {
	    public void paintControl(PaintEvent e) {
		if (fIconImage != null) {
		    e.gc.drawImage(fIconImageScaled, 0, 0);
		}
	    }
	});

	if (fIcon.getPath() != null) {
	    fPath.setText(fIcon.getPath());
	}
	if (fIcon.getWidth() != null) {
	    fWidth.setText(fIcon.getWidth().toString());
	}
	if (fIcon.getHeight() != null) {
	    fHeight.setText(fIcon.getHeight().toString());
	}

    }

    public Text getPath() {
	return fPath;
    }

    public void setPath(Text path) {
	this.fPath = path;
    }

    public Text getWidth() {
	return fWidth;
    }

    public void setWidth(Text width) {
	this.fWidth = width;
    }

    public Text getHeight() {
	return fHeight;
    }

    public void setHeight(Text height) {
	this.fHeight = height;
    }

    private void showIcon(final Composite composite) {
	try {
	    ImageData imageData = fIconImage.getImageData();
	    Integer imageWidth = (!fWidth.getText().isEmpty()) ? Integer
		    .valueOf(fWidth.getText()) : imageData.width;
	    Integer imageHeight = (!fHeight.getText().isEmpty()) ? Integer
		    .valueOf(fHeight.getText()) : imageData.height;
	    imageData = imageData.scaledTo(imageWidth, imageHeight);
	    fIconImageScaled = new Image(fIconImage.getDevice(), imageData);
	    fCanvas.redraw();
	    fCanvas.setVisible(true);
	    setPageComplete(true);
	} catch (Exception e) {
	    setPageComplete(false);
	    fCanvas.setVisible(false);
	}
    }

    private void updateIconInfo(final Composite composite) {
	ImageData imageData = fIconImage.getImageData();
	if (imageData.width > MAX_ICON_SIZE || imageData.height > MAX_ICON_SIZE) {
	    float scaleFactor = (float) MAX_ICON_SIZE
		    / (float) Math.max(imageData.width, imageData.height);
	    imageData = imageData.scaledTo(
		    (int) (imageData.width * scaleFactor),
		    (int) (imageData.height * scaleFactor));
	}
    }

    private Image createImage(final Composite composite, final String pathString) {
	Image image = new Image(composite.getDisplay(), pathString);
	return image;
    }

    private IProject getProject() {
	IEditorPart editorPart = WidgetsActivator.getDefault().getWorkbench()
		.getActiveWorkbenchWindow().getActivePage().getActiveEditor();
	IFileEditorInput input = (IFileEditorInput) editorPart.getEditorInput();
	IProject activeProject = input.getFile().getProject();
	return activeProject;
    }

    private void handleModifyPath(final Composite composite) {
	IProject activeProject = getProject();
	IResource iconFile = activeProject
		.getFile(WidgetsCore.WIDGET_SRC_FOLDER + IPath.SEPARATOR
			+ fPath.getText());
	if (iconFile.exists()
		&& Arrays.binarySearch(WidgetsCore.LEGAL_ICON_EXTENSIONS,
			iconFile.getFileExtension()) >= 0) {
	    fIconImage = createImage(composite, iconFile.getLocation()
		    .toString());
	    updateIconInfo(composite);
	    showIcon(composite);
	} else {
	    setPageComplete(false);
	    fCanvas.setVisible(false);
	}
    }

    private void handleBrowseForIcon(final Composite composite) {
	ElementTreeSelectionDialog iconDialog = new ElementTreeSelectionDialog(
		getShell(), (new WorkbenchLabelProvider()),
		new WorkbenchContentProvider());
	iconDialog.setTitle(Messages.IconDetailsPage_ChooseIconLabel);
	iconDialog.setInput(getProject().getFolder(
		WidgetsCore.WIDGET_SRC_FOLDER)); //$NON-NLS-1$
	iconDialog.addFilter(new ViewerFilter() {

	    @Override
	    public boolean select(Viewer viewer, Object parentElement,
		    Object element) {
		IResource resource = (IResource) element;
		return resource.getType() == IResource.FOLDER
			|| Arrays.binarySearch(
				WidgetsCore.LEGAL_ICON_EXTENSIONS,
				resource.getFileExtension()) >= 0;

	    }
	});
	iconDialog.setValidator(new ISelectionStatusValidator() {

	    @Override
	    public IStatus validate(Object[] selection) {

		if (selection.length == 0 || selection.length > 1) {
		    return new Status(IStatus.ERROR,
			    WidgetsActivator.PLUGIN_ID, IStatus.ERROR, "", null); //$NON-NLS-1$
		}
		IResource resource = (IResource) selection[0];
		if (resource.getType() == IResource.FOLDER) {
		    return new Status(IStatus.ERROR,
			    WidgetsActivator.PLUGIN_ID, ""); //$NON-NLS-1$
		}
		return Status.OK_STATUS;
	    }
	});

	iconDialog.open();
	if (iconDialog.getReturnCode() == IStatus.OK) {
	    IFile selectedFile = (IFile) iconDialog.getFirstResult();
	    setIcon(composite, selectedFile);
	}
    }

    private void handleBrowseFromFilesystem(final Composite composite) {
	FileDialog iconDialog = new FileDialog(getShell(), SWT.OPEN);
	iconDialog.setFilterExtensions(WidgetsCore.LEGAL_FILTER_EXTENSIONS);
	String fileName = iconDialog.open();

	IProject activeProject = getProject();
	File file = new File(fileName);
	IFile projectFile = activeProject.getFile(WidgetsCore.WIDGET_SRC_FOLDER
		+ file.getName());
	String projectPath = activeProject
		.getFile(WidgetsCore.WIDGET_SRC_FOLDER).getLocation()
		.toOSString();

	System.out.println(fileName);
	// if icon is in project file
	try {
	    if (fileName.startsWith(projectPath)) {
		fileName = fileName.substring(projectPath.length());
		projectFile = activeProject
			.getFile(WidgetsCore.WIDGET_SRC_FOLDER + fileName);
		setIcon(composite, projectFile);
	    } else {
		fileName = projectFile.getProjectRelativePath().toString()
			.substring(WidgetsCore.WIDGET_SRC_FOLDER.length());
		if (!projectFile.exists() || confirmReplace(fileName)) {
		    if (projectFile.exists()) {
			projectFile.delete(true, null);
		    }
		    projectFile.create(new FileInputStream(file), true, null);
		    setIcon(composite, projectFile);
		}
	    }
	} catch (FileNotFoundException e) {
	    e.printStackTrace();
	} catch (CoreException e) {
	    e.printStackTrace();
	}
    }

    private boolean confirmReplace(String fileName) {
	return MessageDialog.openConfirm(getShell(), Messages.IconDetailsPage_IconExistsErrorTitle, NLS.bind(
		Messages.IconDetailsPage_IconExistsErrorDesc, fileName));
    }

    private void setIcon(final Composite composite, IFile projectFile) {
	fPath.setText(projectFile.getProjectRelativePath().toOSString()
		.substring(WidgetsCore.WIDGET_SRC_FOLDER.length())); //$NON-NLS-1$
	fIconImage = createImage(composite, projectFile.getLocation()
		.toString());
	updateIconInfo(composite);
	showIcon(composite);
    }
}