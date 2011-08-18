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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.fieldassist.AutoCompleteField;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

import com.opera.widgets.core.widget.Feature;
import com.opera.widgets.core.widget.Param;
import com.opera.widgets.ui.editor.Messages;

/**
 * Page of {@link FeatureWizard}
 * 
 * @author Michal Borek
 * @see FeatureWizard
 */
public class FeatureDetailsPage extends WizardPage {

     
    private Text fFeatureName;
    private Button fRequired;
    private Feature fFeature;
    private TableViewer fTableViewer;
    private List<Param> fParams;
    private Map<String, String[]> fProposals;


    protected FeatureDetailsPage(String pageName, Feature feature, Map<String, String[]> proposals) {
	super(pageName);
	fFeature = feature;
	fProposals = proposals;
	fParams = new ArrayList<Param>(feature.getParams());
	setTitle(Messages.FeatureDetailsPage_FeatureDetailsTitle);
    }

    @Override
    public void createControl(Composite parent) {
	Composite composite = new Composite(parent, SWT.NONE);
	GridLayout layout = new GridLayout(2, false);
	composite.setLayout(layout);
	setControl(composite);
	GridData gd = new GridData(GridData.FILL_HORIZONTAL);
	new Label(composite, SWT.NONE)
		.setText(Messages.FeatureDetailsPage_URILabel);
	fFeatureName = new Text(composite, SWT.NONE);
	fFeatureName.setLayoutData(gd);
	new AutoCompleteField(fFeatureName, new TextContentAdapter(),
		fProposals.keySet().toArray(new String[0]));
	new Label(composite, SWT.NONE)
		.setText(Messages.FeatureDetailsPage_RequiredLabel);
	fRequired = new Button(composite, SWT.CHECK);

	Label separator = new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL);
	gd = new GridData(GridData.FILL_HORIZONTAL);
	gd.horizontalSpan = 2;
	separator.setLayoutData(gd);

	if (fFeature.getName() != null) {
	    fFeatureName.setText(fFeature.getName());
	}
	fRequired.setSelection(fFeature.isRequired());

	Label paramsTableLabel = new Label(composite, SWT.NONE);
	paramsTableLabel.setText(Messages.FeatureDetailsPage_ParametersLabel);
	fTableViewer = new TableViewer(composite);
	String[] titles = { Messages.FeatureDetailsPage_NameColumn,
		Messages.FeatureDetailsPage_ValueColumn };
	int[] bounds = { 250, 250 };
	for (int i = 0; i < titles.length; i++) {
	    TableViewerColumn column = new TableViewerColumn(fTableViewer,
		    SWT.NONE);
	    column.getColumn().setText(titles[i]);
	    column.getColumn().setWidth(bounds[i]);
	    column.getColumn().setResizable(true);
	    column.setEditingSupport(new ParamsEditingSupport(fTableViewer, i));
	}
	gd = new GridData(GridData.FILL_HORIZONTAL);
	gd.horizontalSpan = 2;
	paramsTableLabel.setLayoutData(gd);

	fTableViewer.getTable().setHeaderVisible(true);
	fTableViewer.getTable().setLinesVisible(true);
	gd = new GridData(GridData.FILL_BOTH);
	gd.horizontalSpan = 2;
	gd.minimumHeight = 100;
	fTableViewer.getTable().setLayoutData(gd);
	fTableViewer.setContentProvider(new ParamsContentProvider());
	fTableViewer.setLabelProvider(new ParamsLabelProvider());
	fTableViewer.setInput(fParams);

	// EDITING SUPPORT
	Composite buttonsComposite = new Composite(composite, SWT.NONE);
	gd = new GridData(GridData.FILL_HORIZONTAL);
	gd.horizontalSpan = 2;
	buttonsComposite.setLayoutData(gd);
	GridLayout buttonsLayout = new GridLayout(2, false);
	buttonsLayout.marginLeft = 0;
	buttonsComposite.setLayout(buttonsLayout);
	Button addButton = new Button(buttonsComposite, SWT.PUSH);
	addButton.setText(Messages.OverviewPage_ButtonAdd);
	addButton.addListener(SWT.Selection, new Listener() {
	    @Override
	    public void handleEvent(Event event) {
		Param newParam = new Param(fFeature);
		newParam.setName(Messages.FeatureDetailsPage_NameDefaultValue);
		newParam.setValue(Messages.FeatureDetailsPage_ValueDefaultValue);
		fParams.add(newParam);
		fTableViewer.add(newParam);
		fTableViewer.editElement(newParam, 0);
	    }
	});
	Button removeButton = new Button(buttonsComposite, SWT.PUSH);
	removeButton.setText(Messages.OverviewPage_ButtonRemove);
	removeButton.addListener(SWT.Selection, new Listener() {
	    @Override
	    public void handleEvent(Event event) {
		if (!fTableViewer.getSelection().isEmpty()) {
		    fParams.remove(((Param) ((IStructuredSelection) fTableViewer
			    .getSelection()).getFirstElement()));
		    fTableViewer.setInput(fParams);
		}
	    }
	});

    }

    public String getFeatureName() {
	return fFeatureName.getText();
    }

    public boolean getRequired() {
	return fRequired.getSelection();
    }

    public List<Param> getParams() {
	return fParams;
    }

    /**
     * Label provider for params table
     * 
     * @author Michal Borek
     */
    private final class ParamsLabelProvider extends LabelProvider implements
	    ITableLabelProvider {

	@Override
	public String getColumnText(Object element, int columnIndex) {
	    Param param = (Param) element;
	    switch (columnIndex) {
	    case 0:
		return param.getName();
	    case 1:
		return param.getValue();
	    default:
		throw new RuntimeException("Table error"); //$NON-NLS-1$
	    }
	}

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
	    return null;
	}
    }

    /**
     * Content provider for params table
     * 
     * @author Michal Borek
     * 
     */
    private final class ParamsContentProvider implements
	    IStructuredContentProvider {
	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

	}

	@Override
	public void dispose() {
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object[] getElements(Object inputElement) {
	    return ((List<Feature>) inputElement).toArray();
	}
    }

    /**
     * Editing support for params table. It brings possibility to edit params by
     * clicking at row.
     * 
     * @author mborek
     * 
     */
    private final class ParamsEditingSupport extends EditingSupport {
	private int column;
	private CellEditor editor;

	public ParamsEditingSupport(ColumnViewer viewer, int column) {
	    super(viewer);
	    this.column = column;
	    editor = new TextCellEditor(((TableViewer) viewer).getTable());
	}

	@Override
	protected void setValue(Object element, Object value) {
	    Param param = (Param) element;
	    switch (column) {
	    case 0:
		param.setName((String) value);
		break;
	    case 1:
		param.setValue((String) value);
	    }
	    getViewer().update(element, null);
	}

	@Override
	protected Object getValue(Object element) {
	    Param param = (Param) element;
	    switch (column) {
	    case 0:
		return param.getName();
	    case 1:
		return param.getValue();
	    }
	    return null;
	}

	@Override
	protected CellEditor getCellEditor(Object element) {
	    return editor;
	}

	@Override
	protected boolean canEdit(Object element) {
	    return true;
	}
    }

}