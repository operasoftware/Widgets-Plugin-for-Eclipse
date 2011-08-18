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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;

import com.opera.widgets.core.natures.WACWidgetNature;
import com.opera.widgets.core.widget.JilWidget;
import com.opera.widgets.core.widget.NodeElement;
import com.opera.widgets.core.widget.Widget.ViewMode;
import com.opera.widgets.ui.IHelpContextIds;
import com.opera.widgets.ui.WidgetsActivator;

/**
 * Overview tab in Config editor. Contains basic configuration data of widget.
 * 
 * @author Michal Borek
 * @see ConfigEditor
 */
public class OverviewPage extends AbstractFormPage {

    private final class DimensionButtonSelectionAdapter extends
	    SelectionAdapter {
	@Override
	public void widgetSelected(SelectionEvent e) {
	    Button b = (Button) e.getSource();
	    if (b.getSelection()) {
		getWidget().setWidth((Integer) b.getData("width")); //$NON-NLS-1$
		getWidget().setHeight((Integer) b.getData("height")); //$NON-NLS-1$
	    }
	}
    }

    Map<ViewMode, Button> fViewmodeMap;
    private Text fNameText;
    private Text fShortNameText;
    private Text fDescriptionText;
    private Text fIdText;
    private Text fContentSrcText;
    private Text fVersionText;
    private Text fWidthText;
    private Text fHeightText;
    private Button fQvgaLandscapeButton;
    private Button fQvgaPortraitButton;
    private Button fQqvgaLandscapeButton;
    private Button fWqvgaPortraitButton;
    private Button fCustomDimensionButton;
    private Button fBillingButton;

    public OverviewPage(ConfigEditor editor) {
	super(editor, "overview", Messages.OverviewPage_Overview); //$NON-NLS-1$
    }

    @Override
    protected void createFormContent(IManagedForm managedForm) {
	super.createFormContent(managedForm);
	createNameAndDescriptionSection();
	createGeneralSection();
	createAppearanceSection();
	createViewModesSection();
	addLastFocusListeners(managedForm.getForm());

    }

    /**
     * Updates form editor data after page change
     */
    @Override
    public void updateEditor() {
	super.updateEditor();
	if (fLanguageButton != null) {
	    fLanguageButton.updateSelection();
	}

	JilWidget widget = getWidget();

	String selelectedLanguage = getSelectedLanguage();
	// name and description section
	String defaultName = widget.getName(selelectedLanguage);
	String shortName = widget.getShortName(selelectedLanguage);
	String description = widget.getDescription(selelectedLanguage);

	fNameText.setText(defaultName != null ? defaultName : ""); //$NON-NLS-1$
	fShortNameText.setText(shortName != null ? shortName : ""); //$NON-NLS-1$
	fDescriptionText.setText(description != null ? description : ""); //$NON-NLS-1$
	// appearance
	Integer width = widget.getWidth();
	Integer height = widget.getHeight();
	fQvgaLandscapeButton.setSelection(false);
	fQvgaPortraitButton.setSelection(false);
	fQqvgaLandscapeButton.setSelection(false);
	fWqvgaPortraitButton.setSelection(false);
	fCustomDimensionButton.setSelection(false);
	if (width != null && height != null) {
	    if (width.intValue() == 320 && height.intValue() == 240) {
		fQvgaLandscapeButton.setSelection(true);
	    } else if (width.intValue() == 240 && height.intValue() == 320) {
		fQvgaPortraitButton.setSelection(true);
	    } else if (width.intValue() == 400 && height.intValue() == 240) {
		fQqvgaLandscapeButton.setSelection(true);
	    } else if (width.intValue() == 240 && height.intValue() == 400) {
		fWqvgaPortraitButton.setSelection(true);
	    } else {
		fCustomDimensionButton.setSelection(true);
		fWidthText.setText(width.toString());
		fHeightText.setText(height.toString());
	    }
	} else {
	    fCustomDimensionButton.setSelection(true);
	    fWidthText.setText(width != null ? width.toString() : ""); //$NON-NLS-1$
	    fHeightText.setText(height != null ? height.toString() : ""); //$NON-NLS-1$
	}

	fWidthText.setEnabled(fCustomDimensionButton.getSelection());
	fHeightText.setEnabled(fCustomDimensionButton.getSelection());

	// general section
	String id = widget.getId();
	String contentSrc = widget.getContentSrc();
	String version = widget.getVersion();
	fIdText.setText(id != null ? id : ""); //$NON-NLS-1$
	fContentSrcText.setText(contentSrc != null ? contentSrc : ""); //$NON-NLS-1$
	fVersionText.setText(version != null ? version : ""); //$NON-NLS-1$

	List<ViewMode> viewmodesList = widget.getViewmodes();
	for (Button b : fViewmodeMap.values()) {
	    b.setSelection(false);
	}
	for (ViewMode el : viewmodesList) {
	    if (fViewmodeMap.containsKey(el)) {
		fViewmodeMap.get(el).setSelection(true);
	    }
	}
	try {
	    if (projectHasWacNature()) {
		fBillingButton.setSelection(widget.getJilBillingRequired());
	    }
	} catch (CoreException e) {
	    e.printStackTrace();
	}
    }

    private boolean projectHasWacNature() throws CoreException {
	IProject project = getProject();
	return project != null && project.hasNature(WACWidgetNature.NATURE_ID);
    }

    /**
     * Creates section containing general settings: name, description, size etc.
     */
    private void createGeneralSection() {
	Composite section = createSection(getManagedForm(),
		Messages.OverviewPage_SectionGeneralTitle, "", 3); //$NON-NLS-1$
	fIdText = addTextField(section, Messages.OverviewPage_Id,
		NodeElement.WIDGET_ATTRIBUTE_ID);
	GridData gd = new GridData(GridData.FILL_HORIZONTAL);
	gd.horizontalSpan = 2;
	fIdText.setLayoutData(gd);
	fIdText.addKeyListener(new KeyAdapter() {
	    @Override
	    public void keyReleased(KeyEvent e) {
		Text element = (Text) e.getSource();
		getWidget().setId(element.getText());
	    }
	});
	fContentSrcText = addTextField(section,
		Messages.OverviewPage_LabelStartFile, NodeElement.CONTENT_SRC);
	fContentSrcText.addKeyListener(new KeyAdapter() {
	    @Override
	    public void keyReleased(KeyEvent e) {
		Text element = (Text) e.getSource();
		getWidget().setContentSrc(element.getText());
	    }
	});

	Button browseContentSrcButton = getEditor().getToolkit().createButton(
		section, Messages.OverviewPage_Browse, SWT.PUSH);

	browseContentSrcButton.addSelectionListener(new SelectionAdapter() {
	    @Override
	    public void widgetSelected(SelectionEvent e) {

		ElementTreeSelectionDialog startFileSelectionDialog = new ElementTreeSelectionDialog(
			WidgetsActivator.getDefault().getWorkbench()
				.getActiveWorkbenchWindow().getShell(),
			(new WorkbenchLabelProvider()),
			new WorkbenchContentProvider());
		startFileSelectionDialog
			.setTitle(Messages.OverviewPage_ChooseStartFile);
		IProject activeProject = getProject();
		startFileSelectionDialog.setInput(activeProject
			.getFolder("src")); //$NON-NLS-1$
		startFileSelectionDialog.addFilter(new ViewerFilter() {

		    @Override
		    public boolean select(Viewer viewer, Object parentElement,
			    Object element) {
			IResource resource = (IResource) element;
			final String[] legalExtensions = {
				"htm", "html", "svg", "xhtml", "xht" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
			return resource.getFileExtension() != null
				&& Arrays.binarySearch(legalExtensions,
					resource.getFileExtension()) >= 0;

		    }
		});
		startFileSelectionDialog
			.setValidator(new ISelectionStatusValidator() {

			    @Override
			    public IStatus validate(Object[] selection) {

				if (selection.length == 0
					|| selection.length > 1) {
				    return new Status(IStatus.ERROR,
					    WidgetsActivator.PLUGIN_ID,
					    IStatus.ERROR, "", null); //$NON-NLS-1$
				}
				IResource resource = (IResource) selection[0];
				if (resource.getType() == IResource.FOLDER) {
				    return new Status(IStatus.ERROR,
					    WidgetsActivator.PLUGIN_ID, ""); //$NON-NLS-1$
				}
				return Status.OK_STATUS;
			    }
			});
		startFileSelectionDialog.open();

		if (startFileSelectionDialog.getReturnCode() == IStatus.OK) {
		    IResource resource = (IResource) startFileSelectionDialog
			    .getFirstResult();
		    fContentSrcText.setText(resource.getProjectRelativePath()
			    .toString().substring("src".length() + 1)); //$NON-NLS-1$
		    getWidget().setContentSrc(fContentSrcText.getText());
		}
	    }
	});

	fVersionText = addTextField(section, Messages.OverviewPage_Version,
		NodeElement.WIDGET_ATTRIBUTE_VERSION);
	fVersionText.addKeyListener(new KeyAdapter() {
	    @Override
	    public void keyReleased(KeyEvent e) {
		Text element = (Text) e.getSource();
		getWidget().setVersion(element.getText());
	    }
	});
	gd = new GridData(GridData.FILL_HORIZONTAL);
	gd.horizontalSpan = 2;
	fVersionText.setLayoutData(gd);

	try {
	    IProject project = getProject();
	    if (project != null && project.hasNature(WACWidgetNature.NATURE_ID)) {
		fToolkit.createLabel(section, Messages.OverviewPage_BillingLabel);
		fBillingButton = fToolkit.createButton(section,
			Messages.OverviewPage_billingRequiredLabel, SWT.CHECK);
		fBillingButton.addSelectionListener(new SelectionAdapter() {
		    @Override
		    public void widgetSelected(SelectionEvent e) {
			getWidget().setJilBillingRequired(
				fBillingButton.getSelection());

		    }
		});

	    }
	} catch (CoreException e1) {
	    e1.printStackTrace();
	}
    }

    /**
     * Creates section containing author settings (<author> node from
     * config.xml)
     */
    private void createAppearanceSection() {
	Composite section = createSection(getManagedForm(),
		Messages.OverviewPage_Appearance, "", 4); //$NON-NLS-1$

	fQvgaLandscapeButton = fToolkit.createButton(section,
		"QVGA (Landscape)", //$NON-NLS-1$
		SWT.RADIO);
	fQvgaLandscapeButton.setData("width", new Integer(320)); //$NON-NLS-1$
	fQvgaLandscapeButton.setData("height", new Integer(240)); //$NON-NLS-1$
	GridData gd = new GridData();
	gd.horizontalIndent = 7;
	fToolkit.createLabel(section, "320").setLayoutData(gd); //$NON-NLS-1$
	fToolkit.createLabel(section, "x"); //$NON-NLS-1$
	fToolkit.createLabel(section, "240").setLayoutData(gd); //$NON-NLS-1$

	fQvgaPortraitButton = fToolkit.createButton(section, "QVGA (Portrait)", //$NON-NLS-1$
		SWT.RADIO);
	fQvgaPortraitButton.setData("width", new Integer(240)); //$NON-NLS-1$
	fQvgaPortraitButton.setData("height", new Integer(320)); //$NON-NLS-1$

	fToolkit.createLabel(section, "240").setLayoutData(gd); //$NON-NLS-1$
	fToolkit.createLabel(section, "x"); //$NON-NLS-1$
	fToolkit.createLabel(section, "320").setLayoutData(gd); //$NON-NLS-1$

	fQqvgaLandscapeButton = fToolkit.createButton(section,
		"WQVGA (Landscape)", SWT.RADIO); //$NON-NLS-1$
	fQqvgaLandscapeButton.setData("width", new Integer(400)); //$NON-NLS-1$
	fQqvgaLandscapeButton.setData("height", new Integer(240)); //$NON-NLS-1$

	fToolkit.createLabel(section, "400").setLayoutData(gd); //$NON-NLS-1$
	fToolkit.createLabel(section, "x"); //$NON-NLS-1$
	fToolkit.createLabel(section, "240").setLayoutData(gd); //$NON-NLS-1$

	fWqvgaPortraitButton = fToolkit.createButton(section,
		"WQVGA (Portrait)", //$NON-NLS-1$
		SWT.RADIO);
	fWqvgaPortraitButton.setData("width", new Integer(240)); //$NON-NLS-1$
	fWqvgaPortraitButton.setData("height", new Integer(400)); //$NON-NLS-1$

	fToolkit.createLabel(section, "240").setLayoutData(gd); //$NON-NLS-1$
	fToolkit.createLabel(section, "x"); //$NON-NLS-1$
	fToolkit.createLabel(section, "400").setLayoutData(gd); //$NON-NLS-1$

	fCustomDimensionButton = fToolkit.createButton(section,
		Messages.OverviewPage_Custom, SWT.RADIO);

	fWidthText = addTextField(section, null,
		NodeElement.WIDGET_ATTRIBUTE_WIDTH);
	gd = new GridData();
	gd.widthHint = 30;
	fWidthText.setLayoutData(gd);
	fWidthText.addKeyListener(new KeyAdapter() {
	    @Override
	    public void keyReleased(KeyEvent ev) {
		Text element = (Text) ev.getSource();
		String text = element.getText();
		if (!text.isEmpty()) {
		    try {
			getWidget().setWidth(
				Integer.parseInt(element.getText()));
		    } catch (NumberFormatException e) {

		    }
		}
	    }
	});

	fToolkit.createLabel(section, "x"); //$NON-NLS-1$

	fHeightText = addTextField(section, null,
		NodeElement.WIDGET_ATTRIBUTE_HEIGHT);

	gd = new GridData();
	gd.widthHint = 30;
	fHeightText.setLayoutData(gd);
	fHeightText.addKeyListener(new KeyAdapter() {
	    @Override
	    public void keyReleased(KeyEvent ev) {
		Text element = (Text) ev.getSource();
		String text = element.getText();
		if (!text.isEmpty()) {
		    try {
			getWidget().setHeight(
				Integer.parseInt(element.getText()));
		    } catch (NumberFormatException e) {

		    }
		}
	    }
	});

	fQvgaLandscapeButton
		.addSelectionListener(new DimensionButtonSelectionAdapter());
	fQvgaPortraitButton
		.addSelectionListener(new DimensionButtonSelectionAdapter());
	fQqvgaLandscapeButton
		.addSelectionListener(new DimensionButtonSelectionAdapter());
	fWqvgaPortraitButton
		.addSelectionListener(new DimensionButtonSelectionAdapter());

	fCustomDimensionButton.addSelectionListener(new SelectionAdapter() {
	    @Override
	    public void widgetSelected(SelectionEvent e) {
		fWidthText.setEnabled(fCustomDimensionButton.getSelection());
		fHeightText.setEnabled(fCustomDimensionButton.getSelection());
		if (fCustomDimensionButton.getSelection()) {
		    if (!fWidthText.getText().isEmpty()) {
			getWidget().setWidth(
				Integer.valueOf(fWidthText.getText()));
		    }
		    if (!fHeightText.getText().isEmpty()) {
			getWidget().setHeight(
				Integer.valueOf(fHeightText.getText()));
		    }
		}
	    }
	});

	// info text for features window
	FormText hint = fToolkit.createFormText(section, false);
	hint.setText(Messages.OverviewPage_AppearanceHint, false, true);
	gd = new GridData(GridData.FILL_HORIZONTAL);
	gd.horizontalSpan = 4;
	hint.setLayoutData(gd);
	FormText hintExample = fToolkit.createFormText(section, false);
	hintExample.setText(Messages.OverviewPage_AppearanceHintExample, false,
		true);
	gd = new GridData(GridData.FILL_HORIZONTAL);
	gd.horizontalSpan = 4;
	hintExample.setLayoutData(gd);

    }

    private void createNameAndDescriptionSection() {
	Composite section = createSection(getManagedForm(),
		Messages.OverviewPage_NameAndDescription, "", 2, true); //$NON-NLS-1$

	fNameText = addTextField(section, Messages.OverviewPage_Name,
		NodeElement.NAME);
	fNameText.addKeyListener(new KeyAdapter() {
	    @Override
	    public void keyReleased(KeyEvent e) {
		Text element = (Text) e.getSource();
		getWidget().setName(element.getText(), getSelectedLanguage());
	    }
	});
	fShortNameText = addTextField(section, Messages.OverviewPage_ShortName,
		NodeElement.NAME_SHORT);
	fShortNameText.addKeyListener(new KeyAdapter() {
	    @Override
	    public void keyReleased(KeyEvent e) {
		Text element = (Text) e.getSource();
		getWidget().setShortName(element.getText(),
			getSelectedLanguage());
	    }
	});
	fToolkit.createLabel(section, Messages.OverviewPage_LabelDescription);
	fDescriptionText = fToolkit.createText(section, "", //$NON-NLS-1$
		SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.WRAP);
	GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
	gridData.minimumHeight = 100;
	gridData.grabExcessVerticalSpace = true;
	fDescriptionText.setLayoutData(gridData);
	fDescriptionText.addKeyListener(new KeyAdapter() {
	    @Override
	    public void keyReleased(KeyEvent e) {
		Text element = (Text) e.getSource();
		getWidget().setDescription(element.getText(),
			getSelectedLanguage());
	    }
	});
    }

    private void createViewModesSection() {
	Composite section = createSection(getManagedForm(),
		Messages.OverviewPage_ViewModesTitle, "", 5); //$NON-NLS-2$ //$NON-NLS-1$ //$NON-NLS-1$ //$NON-NLS-1$
	fViewmodeMap = new HashMap<ViewMode, Button>(5);
	fViewmodeMap.put(ViewMode.WINDOWED,
		fToolkit.createButton(section, "windowed", SWT.CHECK)); //$NON-NLS-1$ //$NON-NLS-2$
	fViewmodeMap.put(ViewMode.FLOATING,
		fToolkit.createButton(section, "floating", SWT.CHECK)); //$NON-NLS-1$ //$NON-NLS-2$
	fViewmodeMap.put(ViewMode.FULLSCREEN,
		fToolkit.createButton(section, "fullscreen", SWT.CHECK)); //$NON-NLS-1$ //$NON-NLS-2$
	fViewmodeMap.put(ViewMode.MAXIMIZED,
		fToolkit.createButton(section, "maximized", SWT.CHECK)); //$NON-NLS-1$ //$NON-NLS-2$
	fViewmodeMap.put(ViewMode.MINIMIZED,
		fToolkit.createButton(section, "minimized", SWT.CHECK)); //$NON-NLS-1$ //$NON-NLS-2$
	for (Button b : fViewmodeMap.values()) {
	    GridData gd = new GridData();
	    gd.horizontalIndent = 15;
	    b.setLayoutData(gd);
	    b.addSelectionListener(new SelectionAdapter() {

		@Override
		public void widgetSelected(SelectionEvent e) {
		    List<ViewMode> viewModes = new ArrayList<ViewMode>();
		    for (Entry<ViewMode, Button> b : fViewmodeMap.entrySet()) {
			if (b.getValue().getSelection()) {
			    viewModes.add(b.getKey());
			}
		    }
		    getWidget().setViewmodes(viewModes);
		}
	    });
	}
    }

    @Override
    public String getHelpResource() {
	return IHelpContextIds.CONFIG_OVERVIEW;
    }
}
