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

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.wst.sse.ui.StructuredTextEditor;
import org.w3c.dom.Document;

import com.opera.widgets.core.exception.InvalidConfigFileException;
import com.opera.widgets.core.widget.Author;
import com.opera.widgets.core.widget.JilWidget;
import com.opera.widgets.core.widget.WACWidgetBase;
import com.opera.widgets.core.widget.WidgetModel;
import com.opera.widgets.ui.WidgetsActivator;

/**
 * Config.xml Editor. It contains form editors and source editor to edit raw
 * config.xml file.
 * 
 * @author Michal Borek
 * 
 */
public class ConfigEditor extends FormEditor {

    public final static String LAST_ACTIVE_PAGE_INDEX = "last_active_page_index"; //$NON-NLS-1$

    private SourceEditor fSourceEditor;
    private Map<Integer, IConfigEditorPage> fPages = new HashMap<Integer, IConfigEditorPage>();

    private JilWidget fWidget;
    private WACWidgetBase fConfigModel;

    private int fSourcePageIndex;

    public ConfigEditor() {
	createInputContext();
    }

    @Override
    public void init(IEditorSite site, IEditorInput input)
	    throws PartInitException {

	if (input instanceof FileEditorInput) {
	    IFile configFile = ((FileEditorInput) input).getFile();

	    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	    dbf.setNamespaceAware(true);
	    try {
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(configFile.getLocation().toFile());
		fWidget.load(doc.getDocumentElement());
	    } catch (Exception e) {
		handleInvalidConfig(configFile);
	    }
	}
	super.init(site, input);
    }

    private void createInputContext() {
	fConfigModel = new WACWidgetBase();
	fWidget = new JilWidget();
	WidgetModel widgetModel = new WidgetModel(fWidget);
	fWidget.setModel(widgetModel);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doSave(IProgressMonitor monitor) {
	saveAuthorInformation();
	fSourceEditor.doSave(monitor);
    }

    /**
     * Saves information about author to Eclipse preferences for future use
     */
    private void saveAuthorInformation() {

	Author author = getWidget().getAuthor();
	if (author != null) {
	    IEclipsePreferences preferences = DefaultScope.INSTANCE
		    .getNode(WidgetsActivator.PLUGIN_ID + ".config.author"); //$NON-NLS-1$
	    if (author.getName() != null) {
		preferences.put("name", author.getName()); //$NON-NLS-1$
	    }
	    if (author.getEmail() != null) {
		preferences.put("email", author.getEmail()); //$NON-NLS-1$
	    }
	    if (author.getHref() != null) {
		preferences.put("href", author.getHref()); //$NON-NLS-1$
	    }
	}
    }

    @Override
    protected FormToolkit createToolkit(Display display) {
	// Create a toolkit that shares colors between editors.
	return new FormToolkit(WidgetsActivator.getDefault().getFormColors(
		display));

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addPages() {
	fConfigModel = new WACWidgetBase();
	fConfigModel.setModel(fWidget.getModel());
	fWidget.getModel().addModelChangedListener(fConfigModel);
	try {
	    fSourceEditor = new SourceEditor();
	    AbstractFormPage overviewPage = new OverviewPage(this);
	    AbstractFormPage iconsPage = new IconsPage(this);
	    AbstractFormPage featuresPage = new FeaturesPage(this);
	    AbstractFormPage preferencesPage = new PreferencesPage(this);
	    AbstractFormPage accessPage = new AccessPage(this);
	    AbstractFormPage aboutPage = new AboutPage(this);

	    int overviewPageIndex = addPage(overviewPage);
	    int iconsPageIndex = addPage(iconsPage);
	    int featuresPageIndex = addPage(featuresPage);
	    int preferencesPageIndex = addPage(preferencesPage);
	    int accessPageIndex = addPage(accessPage);
	    int aboutPageIndex = addPage(aboutPage);

	    fPages.put(overviewPageIndex, overviewPage);
	    fPages.put(iconsPageIndex, iconsPage);
	    fPages.put(featuresPageIndex, featuresPage);
	    fPages.put(preferencesPageIndex, preferencesPage);
	    fPages.put(accessPageIndex, accessPage);
	    fPages.put(aboutPageIndex, aboutPage);

	    addSourcePage(fSourceEditor);
	    Document document = fSourceEditor.getSourceDocument();
	    fWidget.load(document.getDocumentElement());

	    restoreLastActivePage();
	    fConfigModel.setConfigContent(document);

	} catch (PartInitException e) {

	} catch (InvalidConfigFileException e) {
	    e.printStackTrace();
	}
    }

    private void handleInvalidConfig(IFile file) {
	URL url = WidgetsActivator.getDefault().getBundle()
		.getResource("/resources/project/src/config.xml"); //$NON-NLS-1$
	try {
	    IFile invFile = file.getProject()
		    .getFile("/src/config.xml.invalid"); //$NON-NLS-1$
	    if (invFile.exists()) {
		invFile.delete(true, null);
	    }
	    file.copy(Path.fromPortableString("config.xml.invalid"), true, null); //$NON-NLS-1$
	    file.setContents(url.openStream(), true, false, null);
	} catch (CoreException e) {
	    e.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    private void restoreLastActivePage() {
	int activePageIndex = getPreferenceStore().getInt(
		LAST_ACTIVE_PAGE_INDEX);
	if (activePageIndex >= 0) {
	    setActivePage(activePageIndex);
	}
    }

    /**
     * Adds source page to editor
     * 
     * @param sourceEditor
     * @throws PartInitException
     */
    private void addSourcePage(StructuredTextEditor sourceEditor)
	    throws PartInitException {

	fSourcePageIndex = addPage(sourceEditor, getEditorInput());
	setPageText(fSourcePageIndex, "config.xml"); // i18n //$NON-NLS-1$
	firePropertyChange(PROP_TITLE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doSaveAs() {
	fSourceEditor.doSaveAs();
    }

    @Override
    public boolean isSaveAsAllowed() {
	return (fSourceEditor != null) && fSourceEditor.isSaveAsAllowed();
    }

    @Override
    public void dispose() {
	saveLastActivePage();
	super.dispose();
    }

    /**
     * Saves last active page to restore at next startup
     */
    void saveLastActivePage() {
	getPreferenceStore().setValue(LAST_ACTIVE_PAGE_INDEX, getCurrentPage());
    }

    private IPreferenceStore getPreferenceStore() {
	return WidgetsActivator.getDefault().getPreferenceStore();
    }

    @Override
    protected void pageChange(int newPageIndex) {
	super.pageChange(newPageIndex);
	if (newPageIndex != fSourcePageIndex) {
	    fPages.get(newPageIndex).updateEditor();
	}
    }

    public JilWidget getWidget() {
	return fWidget;
    }

    public void performGlobalAction(String id) {
	((AbstractFormPage) getActivePageInstance()).performGlobalAction(id);

    }

    public SourceEditor getSourceEditor() {
	return fSourceEditor;
    }

    public boolean isSourceDirty() {
	return fSourceEditor.markedDirty();
    }

}
