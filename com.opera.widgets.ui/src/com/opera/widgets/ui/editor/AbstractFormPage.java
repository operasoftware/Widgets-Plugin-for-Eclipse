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
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ColumnLayout;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

import com.opera.widgets.core.exception.InvalidConfigFileException;
import com.opera.widgets.core.widget.JilWidget;
import com.opera.widgets.core.widget.NodeElement;
import com.opera.widgets.ui.PluginImages;
import com.opera.widgets.ui.WidgetsActivator;
import com.opera.widgets.ui.editor.toolbar.LabelControlContribution;
import com.opera.widgets.ui.editor.toolbar.LanguageControlContribution;
import com.opera.widgets.ui.editor.validation.EmailValidator;
import com.opera.widgets.ui.editor.validation.ExistingFileValidator;
import com.opera.widgets.ui.editor.validation.LinkValidator;
import com.opera.widgets.ui.editor.validation.PositiveIntegerValidator;

/**
 * Class provides basic functions to handle FormPage elements
 * 
 * @author Michal Borek
 */
public abstract class AbstractFormPage extends FormPage implements
	IConfigEditorPage {

    private Control fLastFocusControl;
    protected FormToolkit fToolkit;
    protected List<Control> fFields = new ArrayList<Control>();
    protected ToolBarManager fToolBarManager;
    protected LanguageControlContribution fLanguageButton;
    protected ConfigEditor fEditor;


    public AbstractFormPage(ConfigEditor editor, String id, String title) {
	super(editor, id, title);
	fEditor = editor;
	fToolkit = editor.getToolkit();
    }
    
    
    @Override
    protected void createFormContent(IManagedForm managedForm) {
	final ScrolledForm form = managedForm.getForm();
	fToolkit.decorateFormHeading(form.getForm());

	form.setText(getTitle());
	ColumnLayout layout = new ColumnLayout();
	layout.topMargin = 10;
	layout.bottomMargin = 5;
	layout.leftMargin = 10;
	layout.rightMargin = 10;
	layout.horizontalSpacing = 10;
	layout.verticalSpacing = 10;
	layout.maxNumColumns = 1;
	layout.minNumColumns = 1;
	fToolkit.decorateFormHeading(managedForm.getForm().getForm());
	form.getBody().setLayout(layout);

	IToolBarManager manager = form.getToolBarManager();

	final String helpContextId = getHelpResource();

	if (helpContextId != null) {
	    addHelpAction(manager, helpContextId);
	}
	form.updateToolBar();

	PlatformUI.getWorkbench().getHelpSystem()
		.setHelp(this.getManagedForm().getForm(), getHelpResource());
    }

    private void addHelpAction(IToolBarManager manager,
	    final String helpContextId) {
	Action helpAction = new Action("help") { //$NON-NLS-1$
	    @Override
	    public void run() {
		PlatformUI.getWorkbench().getHelpSystem()
			.displayHelp(helpContextId);
	    }
	};
	helpAction.setToolTipText(Messages.AbstractFormPage_HelpLabel);
	helpAction.setImageDescriptor(PluginImages.HELP_ICON);
	manager.add(helpAction);
    }


    protected void addSeparator(Composite parent) {
	Label separator = fToolkit.createSeparator(parent, SWT.HORIZONTAL);
	GridData gd = new GridData(GridData.FILL_HORIZONTAL);
	gd.horizontalSpan = 2;
	separator.setLayoutData(gd);
    }

    /**
     * Adds button to details sections.
     * 
     * @param section
     *            - where to add button
     * @param form
     * @param label
     * @return created button
     */
    protected Button addActionButton(Composite section, IManagedForm form,
	    String label) {
	Button button = fToolkit.createButton(section, label, SWT.PUSH);
	GridData gd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
	gd.widthHint = 90;
	button.setLayoutData(gd);
	return button;
    }

    /**
     * Adds group that can contain buttons of specific type
     * 
     * @param section
     * @param label
     * @return
     */
    protected Composite addButtonGroup(Composite section, String label) {
	Composite group = fToolkit.createComposite(section);
	group.setLayout(new RowLayout());
	GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
	gridData.horizontalSpan = 2;
	group.setLayoutData(gridData);
	fToolkit.createLabel(group, label);
	return group;
    }

    /**
     * Adds text field with specific label and ID to editor.
     * 
     * @param parent
     * @param label
     * @param fieldId
     */
    protected Text addTextField(Composite parent, String label,
	    NodeElement fieldId) {
	if (label != null) {
	    fToolkit.createLabel(parent, label);
	}
	Text text = fToolkit.createText(parent, "", SWT.BORDER); //$NON-NLS-1$
	text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

	// add specific validator
	switch (fieldId.getContentType()) {
	case POSITIVE_INT:
	    new PositiveIntegerValidator(getManagedForm(), text);
	    break;
	case LINK:
	    new LinkValidator(getManagedForm(), text);
	    break;
	case EMAIL:
	    new EmailValidator(getManagedForm(), text);
	    break;
	case EXISTING_FILE:
	    new ExistingFileValidator(getManagedForm(), text);
	    break;
	default:
	    break;
	}
	return text;
    }

    /**
     * Helper to create sections containing title and description;
     */
    protected Composite createSection(final IManagedForm form, String title,
	    String desc, int numColumns) {
	return createSection(form, title, desc, numColumns, false);
    }

    protected TableViewer createTableViewer(Composite section) {
	GridData gd;
	Table table = fToolkit.createTable(section, SWT.MULTI);
	gd = new GridData(GridData.FILL_BOTH);
	gd.heightHint = 100;
	gd.widthHint = 100;
	gd.verticalSpan = 3;
	table.setLayoutData(gd);
	table.setLinesVisible(true);
	table.setHeaderVisible(true);
	TableViewer tableViewer = new TableViewer(table);
	return tableViewer;
    }

    protected Composite createSection(IManagedForm form, String title,
	    String desc, int numColumns, boolean languageChange) {
	int sectionOptions = Section.TITLE_BAR | Section.NO_TITLE_FOCUS_BOX;
	if (desc != null && !desc.isEmpty()) {
	    sectionOptions |= Section.DESCRIPTION;
	}
	Section section = fToolkit.createSection(form.getForm().getBody(),
		sectionOptions);
	section.setText(title);
	if (desc != null && !desc.isEmpty()) {
	    section.setDescription(desc);
	}
	Composite client = fToolkit.createComposite(section);
	GridLayout layout = new GridLayout();
	layout.marginWidth = 70;
	layout.marginHeight = 15;
	layout.numColumns = numColumns;
	client.setLayout(layout);
	section.setClient(client);
	if (languageChange) {
	    addToolbar(section);
	}
	return client;
    }

    protected void addToolbar(Section section) {
	fToolBarManager = new ToolBarManager(SWT.FLAT);
	ToolBar toolbar = fToolBarManager.createControl(section);

	final Cursor handCursor = new Cursor(Display.getCurrent(),
		SWT.CURSOR_HAND);
	toolbar.setCursor(handCursor);
	// Cursor needs to be explicitly disposed
	toolbar.addDisposeListener(new DisposeListener() {
	    public void widgetDisposed(DisposeEvent e) {
		if ((handCursor != null) && (handCursor.isDisposed() == false)) {
		    handCursor.dispose();
		}
	    }
	});

	fLanguageButton = new LanguageControlContribution("lb", getProject(), this); //$NON-NLS-1$
	fToolBarManager.add(new LabelControlContribution(Messages.AbstractFormPage_LanguageLabel, this));
	fToolBarManager.add(fLanguageButton);
	fToolBarManager.update(true);

	section.setTextClient(toolbar);
    }

    public String getSelectedLanguage() {
	IProject project = getProject();
	String selectedLanguage = null;
	if (project != null) {
	    try {
		selectedLanguage = project
			.getPersistentProperty(new QualifiedName(
				WidgetsActivator.PLUGIN_ID, "selectedLanguage")); //$NON-NLS-1$
	    } catch (CoreException e) {
	    }
	}
	return (selectedLanguage == null) ? "default" : selectedLanguage; //$NON-NLS-1$
    }

    protected Label createLabel(Composite parent, String text) {
	Label label = fToolkit.createLabel(parent, text);
	GridData gd = new GridData(GridData.FILL_HORIZONTAL);
	gd.horizontalSpan = 2;
	label.setLayoutData(gd);
	return label;
    }

    /**
     * Addas focus listener to all fields in specific Composite
     * 
     * @param composite
     */
    public void addLastFocusListeners(Composite composite) {
	Control[] controls = composite.getChildren();
	for (int i = 0; i < controls.length; i++) {
	    Control control = controls[i];
	    if ((control instanceof Text) || (control instanceof Button)
		    || (control instanceof Combo)
		    || (control instanceof Spinner)
		    || (control instanceof Link) || (control instanceof List)
		    || (control instanceof TabFolder)
		    || (control instanceof CTabFolder)
		    || (control instanceof Hyperlink)
		    || (control instanceof FilteredTree)) {
		addLastFocusListener(control);
	    }
	    if (control instanceof Composite) {
		// Recursively add focus listeners to this composites children
		addLastFocusListeners((Composite) control);
	    }
	}
    }

    private void addLastFocusListener(final Control control) {
	control.addFocusListener(new FocusListener() {

	    public void focusGained(FocusEvent e) {
		// NO-OP
	    }

	    public void focusLost(FocusEvent e) {
		fLastFocusControl = control;
	    }
	});
    }

    public Control getLastFocusControl() {
	return fLastFocusControl;
    }

    /**
     * Selects field which should be active
     */
    public void updateFormSelection() {
	if ((fLastFocusControl != null)
		&& (fLastFocusControl.isDisposed() == false)) {
	    Control lastControl = fLastFocusControl;
	    // Set focus on the control 
	    lastControl.forceFocus();
	    // If the control is a Text widget, select its contents
	    if (lastControl instanceof Text) {
		Text text = (Text) lastControl;
		text.setSelection(0, text.getText().length());
	    }
	}
    }

    /**
     * Return context id of help resource assigned to this page
     * 
     * @return help context id or null if there is no help for this element
     */
    public String getHelpResource() {
	return null;
    }

    public IProject getProject() {
	IEditorInput editorInput = getEditorInput();
	if (editorInput instanceof IFileEditorInput) {
	    return ((IFileEditorInput) editorInput).getFile().getProject();
	}
	return null;
    }

    public JilWidget getWidget() {
	return ((ConfigEditor) getEditor()).getWidget();
    }

    public Object getContent() {
	return null;
    }

    public boolean performGlobalAction(String id) {
	// TODO to implement later
	// if (id == ActionFactory.UNDO.getId()) {
	// ((ConfigEditor) getEditor()).getInputContextManager().getManager()
	// .undo();
	// return false;
	// } else if (id == ActionFactory.REDO.getId()) {
	// ((ConfigEditor) getEditor()).getInputContextManager().getManager()
	// .redo();
	// return false;
	// }
	return false;
    }
    
    @Override
    public void updateEditor() {
//	if(fEditor.getSourceEditor().markedDirty()){
	    	try {
		    fEditor.getWidget().load(fEditor.getSourceEditor().getSourceDocument().getDocumentElement());
		} catch (InvalidConfigFileException e) {
		    e.printStackTrace();
		}
	  //  	fEditor.getSourceEditor().removeMarkedDirty();
	//}
    }

}
