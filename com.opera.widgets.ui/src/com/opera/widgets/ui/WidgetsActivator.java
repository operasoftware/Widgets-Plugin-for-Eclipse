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

package com.opera.widgets.ui;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.forms.FormColors;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.opera.widgets.core.WidgetsCore;

/**
 * The activator class controls the plug-in life cycle
 * 
 * @author Michał Borek <mborek@opera.com>
 */
public class WidgetsActivator extends AbstractUIPlugin {

    // The plug-in ID
    public static final String PLUGIN_ID = "com.opera.widgets.ui"; //$NON-NLS-1$

    FormColors formColors;

    // The shared instance
    private static WidgetsActivator plugin;

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
     * )
     */
    public void start(BundleContext context) throws Exception {
	super.start(context);
	plugin = this;
	// PropertyConfigurator.configure(getDefault().getBundle().getResource("log4j.properties").toURI().toURL());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
     * )
     */
    public void stop(BundleContext context) throws Exception {
	plugin = null;
	super.stop(context);
    }

    /**
     * Returns the shared instance
     * 
     * @return the shared instance
     */
    public static WidgetsActivator getDefault() {
	return plugin;
    }

    /**
     * Log helper
     * 
     * @param status
     */
    public static void log(IStatus status) {
	getDefault().getLog().log(status);
    }

    public static void log(Throwable e) {
	log(new Status(IStatus.ERROR, PLUGIN_ID, 10001,
		"Widgets Plugin - Internal error", e)); //$NON-NLS-1$
    }

    public static String getPluginId() {
	return PLUGIN_ID;
    }

    /**
     * Toolkit color management
     * 
     * @param display
     * @return
     */
    public FormColors getFormColors(Display display) {
	if (formColors == null) {
	    formColors = new FormColors(display);
	    formColors.markShared();
	}
	return formColors;
    }

    /**
     * Gets list of ignored files and folders. These elements won't be included
     * in widget package after exports
     * 
     * @return array of files and folders to ignore
     */
    public String[] getIgnoredExportElements() {
	String toIgnoreString = WidgetsCore.getDefault().getPreferenceManager()
		.getString(WidgetsCore.IGNORE_EXPORT_PREFERENCE);
	return (toIgnoreString == null) ? null : toIgnoreString.split(";"); //$NON-NLS-1$
    }

    /**
     * Gets project from current selected resource ("active project")
     * 
     * @return project instance
     */
    public IProject getSelectedProject() {
	IWorkbench workbench = getWorkbench();
	if (workbench == null) {
	    return null;
	}
	IWorkbenchWindow workbenchWindow = workbench.getActiveWorkbenchWindow();
	if (workbenchWindow == null) {
	    return null;
	}
	IWorkbenchPage workbenchPage = workbenchWindow.getActivePage();
	if (workbenchPage == null) {
	    return null;
	}
	IEditorPart editorPart = workbenchPage.getActiveEditor();
	if (editorPart == null) {
	    return null;
	}
	IEditorInput editorInput = editorPart.getEditorInput();
	if (editorInput instanceof IFileEditorInput) {
	    return ((IFileEditorInput) editorInput).getFile().getProject();
	}
	return null;
    }
}
