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

import java.util.Arrays;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;

import com.opera.widgets.core.WidgetsCore;
import com.opera.widgets.core.widget.Icon;
import com.opera.widgets.core.widget.Widget;
import com.opera.widgets.ui.WidgetsActivator;
import com.opera.widgets.ui.editor.validation.IconValidator;

/**
 * Contains details of particular icon added to Config Editor. It's part of
 * standard MasterDetails provided by Eclipse.
 * 
 * @author Michal Borek
 * @see IconsPage
 */
public class IconDetailsPage implements IDetailsPage {

    private boolean stale = false;
    private IManagedForm fManagedForm;
    private Text fPath;
    private Text fWidth;
    private Text fHeight;
    private Icon fIcon;
    private Canvas fCanvas;
    private Image fIconImage;
    private Image fIconImageScaled;
    private IconPropertiesBlock fPage;
    private Button fCustomSizeButton;

    public IconDetailsPage(IconPropertiesBlock page) {
	this.fPage = page;
    }

    @Override
    public void initialize(IManagedForm form) {
	fManagedForm = form;
    }

    @Override
    public void dispose() {

    }

    @Override
    public boolean isDirty() {
	if (fIcon == null) {
	    return false;
	}
	String widthValue = (fIcon.getWidth() != null) ? fIcon.getWidth()
		.toString() : ""; //$NON-NLS-1$
	String heightValue = (fIcon.getHeight() != null) ? fIcon.getHeight()
		.toString() : ""; //$NON-NLS-1$
	return !fPath.getText().equals(fIcon.getPath())
		|| !fWidth.getText().equals(widthValue)
		|| !fHeight.getText().equals(heightValue);
    }

    @Override
    public void commit(boolean onSave) {
	fIcon.setPath(fPath.getText());
	try {
	    if (!fWidth.getText().isEmpty()) {
		fIcon.setWidth(Integer.valueOf(fWidth.getText()));
	    }
	} catch (NumberFormatException e) {

	}
	try {
	    if (!fHeight.getText().isEmpty()) {
		fIcon.setHeight(Integer.valueOf(fHeight.getText()));
	    }
	} catch (NumberFormatException e) {

	}
	Widget widget = getPage().getWidget();
	widget.update(fIcon);
	fPage.getViewer().update(fIcon, null);

    }

    protected AbstractFormPage getPage() {
	return fPage.getFormPage();
    }

    @Override
    public boolean setFormInput(Object input) {
	return true;
    }

    @Override
    public void setFocus() {
	fPath.setFocus();

    }

    @Override
    public boolean isStale() {
	return stale;
    }

    void update() {
	if (fIcon.getPath() != null) {
	    fPath.setText(fIcon.getPath());
	} else {
	    fPath.setText(""); //$NON-NLS-1$
	}
	if (fIcon.getWidth() != null) {
	    fWidth.setText(fIcon.getWidth().toString());
	} else {
	    fWidth.setText(""); //$NON-NLS-1$
	}
	if (fIcon.getHeight() != null) {
	    fHeight.setText(fIcon.getHeight().toString());
	} else {
	    fHeight.setText(""); //$NON-NLS-1$
	}

	boolean customSize = !(fHeight.getText().isEmpty() && fWidth.getText()
		.isEmpty());
	fCustomSizeButton.setSelection(customSize);
	fWidth.setEnabled(customSize);
	fHeight.setEnabled(customSize);

	IProject activeProject = getProject();
	if (activeProject != null) {
	    IResource iconFile = activeProject
		    .getFile(WidgetsCore.WIDGET_SRC_FOLDER + fPath.getText());
	    if (iconFile.exists()
		    && Arrays.binarySearch(WidgetsCore.LEGAL_ICON_EXTENSIONS,
			    iconFile.getFileExtension()) >= 0) {
		fIconImage = createImage(fCanvas.getParent(), iconFile
			.getLocation().toString());
		showIcon();
	    } else {
		fCanvas.setVisible(false);
	    }
	}

    }

    @Override
    public void refresh() {
	update();
    }

    @Override
    public void selectionChanged(IFormPart part, ISelection selection) {

	IStructuredSelection sel = (IStructuredSelection) selection;
	if (sel.size() == 1) {
	    fIcon = (Icon) sel.getFirstElement();
	}
	update();
    }

    @Override
    public void createContents(final Composite parent) {

	GridLayout layout = new GridLayout();
	layout.numColumns = 1;
	layout.marginWidth = 10;
	layout.marginHeight = 5;
	parent.setLayout(layout);

	FormToolkit toolkit = fManagedForm.getToolkit();
	Section s1 = toolkit.createSection(parent, Section.TITLE_BAR);
	s1.marginWidth = 10;
	s1.setText("Icon details"); //$NON-NLS-1$
	GridData gd = new GridData(GridData.FILL_BOTH);
	s1.setLayoutData(gd);
	final Composite client = toolkit.createComposite(s1);
	GridLayout glayout = new GridLayout();
	glayout.marginWidth = glayout.marginHeight = 0;
	glayout.numColumns = 3;
	client.setLayout(glayout);

	s1.setClient(client);

	toolkit.createLabel(client, Messages.IconDetailsPage_Path);
	fPath = toolkit.createText(client, ""); //$NON-NLS-1$
	gd = new GridData(GridData.FILL_HORIZONTAL);
	fPath.setLayoutData(gd);
	fPath.addKeyListener(new KeyAdapter() {
	    @Override
	    public void keyReleased(KeyEvent e) {
		handlePathChange(client);

	    }

	});

	new IconValidator(fPage.getFormPage().getManagedForm(), fPath,
		getProject());

	Button browse = toolkit.createButton(client,
		Messages.IconDetailsPage_Browse, SWT.PUSH);
	browse.addListener(SWT.Selection, new Listener() {
	    @Override
	    public void handleEvent(Event event) {
		handleBrowse(parent, client);
	    }
	});

	Label separator = toolkit.createSeparator(client, SWT.HORIZONTAL);
	gd = new GridData(GridData.FILL_HORIZONTAL);
	gd.horizontalSpan = 3;
	separator.setLayoutData(gd);

	fCustomSizeButton = toolkit.createButton(client,
		Messages.IconDetailsPage_setCustomIconSize, SWT.CHECK);
	gd = new GridData();
	gd.horizontalSpan = 3;
	gd.verticalIndent = 10;
	fCustomSizeButton.setLayoutData(gd);

	toolkit.createLabel(client, Messages.IconDetailsPage_Width);
	fWidth = toolkit.createText(client, ""); //$NON-NLS-1$
	gd = new GridData();
	gd.horizontalSpan = 2;
	gd.widthHint = 100;
	fWidth.setLayoutData(gd);

	fWidth.addKeyListener(new KeyAdapter() {
	    @Override
	    public void keyReleased(KeyEvent e) {
		showIcon();
		commit(false);
	    }
	});

	toolkit.createLabel(client, Messages.IconDetailsPage_Height);
	fHeight = toolkit.createText(client, ""); //$NON-NLS-1$
	gd = new GridData();
	gd.horizontalSpan = 2;
	gd.widthHint = 100;
	fHeight.setLayoutData(gd);
	fHeight.addKeyListener(new KeyAdapter() {
	    @Override
	    public void keyReleased(KeyEvent e) {
		showIcon();
		commit(false);
	    }
	});

	// hint to add
	FormText hint = toolkit.createFormText(client, false);
	hint.setText("", false, true); //$NON-NLS-1$
	gd = new GridData(GridData.FILL_HORIZONTAL);
	gd.horizontalSpan = 3;
	hint.setLayoutData(gd);

	separator = toolkit.createSeparator(client, SWT.HORIZONTAL);
	gd = new GridData(GridData.FILL_HORIZONTAL);
	gd.verticalIndent = 10;
	gd.horizontalSpan = 3;
	separator.setLayoutData(gd);

	fCanvas = new Canvas(client, SWT.NULL);
	fCanvas.setBackground(WidgetsActivator.getDefault()
		.getFormColors(Display.getCurrent()).getBackground());
	gd = new GridData(GridData.FILL_BOTH);
	gd.horizontalSpan = 3;
	fCanvas.setLayoutData(gd);
	fCanvas.addPaintListener(new PaintListener() {
	    public void paintControl(PaintEvent e) {
		if (fIconImage != null) {
		    e.gc.drawImage(fIconImageScaled, 0, 0);
		}
	    }
	});

	fCustomSizeButton.addSelectionListener(new SelectionAdapter() {
	    @Override
	    public void widgetSelected(SelectionEvent e) {
		handleCustomSizeChanged(e);
	    }
	});

    }

    private void showIcon() {
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
	} catch (Exception e) {

	    fCanvas.setVisible(false);
	}
    }

    private void updateIconInfo(final Composite composite) {
	ImageData imageData = fIconImage.getImageData();

	fWidth.setText(Integer.toString(imageData.width));
	fHeight.setText(Integer.toString(imageData.height));
    }

    private Image createImage(final Composite composite, final String pathString) {
	return new Image(composite.getDisplay(), pathString);
    }

    private IProject getProject() {
	IEditorPart editorPart = WidgetsActivator.getDefault().getWorkbench()
		.getActiveWorkbenchWindow().getActivePage().getActiveEditor();
	if (editorPart.getEditorInput() instanceof IFileEditorInput) {
	    IFileEditorInput input = (IFileEditorInput) editorPart
		    .getEditorInput();
	    IProject activeProject = input.getFile().getProject();
	    return activeProject;
	} else {
	    return null;
	}
    }

    /**
     * Handles browse button click
     * 
     * @param parent
     * @param client
     */
    private void handleBrowse(final Composite parent, final Composite client) {
	ElementTreeSelectionDialog iconDialog = new ElementTreeSelectionDialog(
		getShell(), (new WorkbenchLabelProvider()),
		new WorkbenchContentProvider());
	iconDialog.setTitle(Messages.IconDetailsPage_ChooseIcon);
	IProject activeProject = getProject();
	iconDialog.setInput(activeProject.getFolder("src")); //$NON-NLS-1$
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
	    IResource resource = (IResource) iconDialog.getFirstResult();
	    fPath.setText(resource.getProjectRelativePath().toString()
		    .substring("src".length() + 1)); //$NON-NLS-1$
	    fIconImage = createImage(client, resource.getLocation().toString());
	    // we do not update size automatically anymore
	    updateIconInfo(parent);
	    showIcon();
	}
    }

    private void handlePathChange(final Composite client) {
	IProject activeProject = getProject();
	IResource iconFile = activeProject
		.getFile(WidgetsCore.WIDGET_SRC_FOLDER + fPath.getText());
	if (iconFile.exists()
		&& Arrays.binarySearch(WidgetsCore.LEGAL_ICON_EXTENSIONS,
			iconFile.getFileExtension()) >= 0) {
	    fIconImage = createImage(client, iconFile.getLocation().toString());
	    // we do not update size automatically anymore
	    // updateIconInfo(client);
	    showIcon();
	    commit(false);
	} else {
	    fCanvas.setVisible(false);
	}
    }

    private Shell getShell() {
	return WidgetsActivator.getDefault().getWorkbench()
		.getActiveWorkbenchWindow().getShell();
    }

    private void handleCustomSizeChanged(SelectionEvent e) {
	Button b = (Button) e.getSource();
	if (b.getSelection()) {
	    fWidth.setEnabled(true);
	    fHeight.setEnabled(true);
	} else {
	    if (!(fWidth.getText().isEmpty() && fHeight.getText().isEmpty())) {
		fWidth.setText(""); //$NON-NLS-1$
		fHeight.setText(""); //$NON-NLS-1$
		fIcon.setHeight(null);
		fIcon.setWidth(null);
		commit(false);
	    }

	    fWidth.setEnabled(false);
	    fHeight.setEnabled(false);
	}
	showIcon();
    }
}
