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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormText;

import com.opera.widgets.core.widget.Feature;
import com.opera.widgets.ui.IHelpContextIds;
import com.opera.widgets.ui.WidgetsActivator;
import com.opera.widgets.ui.editor.contentprovider.FeaturesContentProvider;
import com.opera.widgets.ui.editor.dialog.FeatureWizard;
import com.opera.widgets.ui.editor.labelprovider.FeatureLabelProvider;

/**
 * Features tab in Config Editor. Contains list of features that widget uses
 * (i.e.: geolocation, camera)
 * 
 * @author Michal Borek
 * @see ConfigEditor
 */
public class FeaturesPage extends AbstractFormPage {

    public static final String PAGE_ID = "features"; //$NON-NLS-1$

    private TableViewer fFeatureViewer;

    public FeaturesPage(ConfigEditor editor) {
	super(editor, PAGE_ID, Messages.FeaturesPage_Features);
    }

    @Override
    public String getHelpResource() {
	return IHelpContextIds.CONFIG_FEATURES;
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
	createFeatureSection(managedForm);
    }

    /**
     * Creates section containing feature settings (<feature> node from
     * config.xml)
     */
    private void createFeatureSection(final IManagedForm form) {
	Composite section = createSection(getManagedForm(),
		Messages.OverviewPage_FeaturesTitle, null, 2);

	FormText descriptionText = new FormText(section, SWT.NONE);
	String description = Messages.FeaturesPage_FeatureElementHint;
	descriptionText.setText(description, true, false);
	GridData gd = new GridData(GridData.FILL_HORIZONTAL);
	gd.horizontalSpan = 2;
	descriptionText.setLayoutData(gd);
	descriptionText.addHyperlinkListener(new HyperlinkAdapter() {
	    @Override
	    public void linkActivated(HyperlinkEvent e) {
		handleAddLinkActivated(e);
	    }
	});

	fFeatureViewer = createTableViewer(section);
	fToolkit.paintBordersFor(section);
	String[] titles = { Messages.FeaturesPage_UriColumn,
		Messages.FeaturesPage_RequiredColumn };
	int[] bounds = { 470, 80 };
	for (int i = 0; i < titles.length; i++) {
	    TableViewerColumn column = new TableViewerColumn(fFeatureViewer,
		    SWT.NONE);
	    column.getColumn().setText(titles[i]);
	    column.getColumn().setWidth(bounds[i]);
	    column.getColumn().setResizable(true);
	}
	Button addButton = addActionButton(section, form,
		Messages.OverviewPage_ButtonAdd);
	addButton.addSelectionListener(new SelectionAdapter() {

	    @Override
	    public void widgetSelected(SelectionEvent e) {
		FeatureWizard wizard = new FeatureWizard();
		WizardDialog dialog = new WizardDialog(WidgetsActivator
			.getDefault().getWorkbench().getActiveWorkbenchWindow()
			.getShell(), wizard);
		dialog.create();
		dialog.open();
		if (dialog.getReturnCode() == Status.OK) {
		    handleAddFeatures(wizard.getFeatures());
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

	Button propertiesButton = addActionButton(section, form,
		Messages.OverviewPage_ButtonProperties);
	propertiesButton.addSelectionListener(new SelectionAdapter() {
	    @Override
	    public void widgetSelected(SelectionEvent e) {
		if (!fFeatureViewer.getSelection().isEmpty()) {
		    openPropertiesDialog();
		}
	    }

	});
	fFeatureViewer.setContentProvider(new FeaturesContentProvider());
	fFeatureViewer.setLabelProvider(new FeatureLabelProvider());
	fFeatureViewer.addDoubleClickListener(new IDoubleClickListener() {
	    @Override
	    public void doubleClick(DoubleClickEvent event) {
		openPropertiesDialog();
	    }
	});
    }

    private void handleDelete() {
	if (!fFeatureViewer.getSelection().isEmpty()) {
	    @SuppressWarnings("unchecked")
	    Iterator<Feature> iterator = (Iterator<Feature>)((StructuredSelection) fFeatureViewer
		    .getSelection()).iterator();
	    while (iterator.hasNext()) {
		Feature  elementToRemove = iterator.next();
		getWidget().remove(elementToRemove);
		fFeatureViewer.remove(elementToRemove);
		fFeatureViewer.getTable().select(0);
	    }
	}
    }

    @Override
    public void updateEditor() {
	super.updateEditor();
	List<Feature> features = getWidget().getFeatures();
	fFeatureViewer.setInput(features);
    }

    private void openPropertiesDialog() {
	Feature featureToEdit = (Feature) ((StructuredSelection) fFeatureViewer
		.getSelection()).getFirstElement();
	FeatureWizard wizard = new FeatureWizard(featureToEdit);
	WizardDialog dialog = new WizardDialog(WidgetsActivator.getDefault()
		.getWorkbench().getActiveWorkbenchWindow().getShell(), wizard);
	dialog.create();
	dialog.open();
	if (dialog.getReturnCode() == Status.OK) {
	    getWidget().update(featureToEdit);
	    fFeatureViewer.update(featureToEdit, null);
	}
    }

    /**
     * Adds feature and all dependent features (if they not exist)
     * 
     * @param wizard
     */
    private void handleAddFeatures(List<Feature> features) {
	List<Feature> existingFeatures = getWidget().getFeatures();
	Map<String, Feature> existingFeatureMap = new HashMap<String, Feature>();
	for (Feature existingFeature : existingFeatures) {
	    existingFeatureMap.put(existingFeature.getName(), existingFeature);
	}
	for (Feature feature : features) {
	    if (!existingFeatureMap.containsKey(feature.getName())) {
		getWidget().add(feature);
		fFeatureViewer.add(feature);
	    } else {
		if (feature.isRequired()) {
		    Feature toUpdateFeature = existingFeatureMap.get(feature
			    .getName());
		    toUpdateFeature.setRequired(true);
		    getWidget().update(toUpdateFeature);
		}
	    }
	}
    }

    private void handleAddLinkActivated(HyperlinkEvent e) {
	String[] featureNames = e.getHref().toString().split(";");
	List<Feature> features = new ArrayList<Feature>();
	for (String featureName : featureNames) {
	    Feature feature = new Feature();
	    feature.setName(featureName);
	    features.add(feature);
	}
	handleAddFeatures(features);
    }
}
