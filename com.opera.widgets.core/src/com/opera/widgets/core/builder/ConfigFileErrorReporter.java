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

package com.opera.widgets.core.builder;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osgi.util.NLS;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.opera.widgets.core.widget.NodeElement;
import com.opera.widgets.core.widget.Widget;

public class ConfigFileErrorReporter extends XMLErrorReporter {

    private static final String PATH_SRC = "src/"; //$NON-NLS-1$
    private static final String[] DEFAULT_START_FILES = { "index.html", //$NON-NLS-1$
	    "index.htm", "index.svg", "index.xhtml", "index.xht" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

    public ConfigFileErrorReporter(IFile file) {
	super(file);
    }

    /**
     * Validates widget configuration
     * 
     * @param monitor
     */
    public void validateContent(IProgressMonitor monitor) {
	if (monitor.isCanceled()) {
	    return;
	}
	monitor.beginTask(
		Messages.ConfigFileErrorReporter_ValidatingWidgetConfigurationTitle1,
		1);
	boolean startFileValidated = false;
	NodeList nodes = fRootElement.getChildNodes();
	for (int i = 0; i < nodes.getLength(); i++) {
	    Element childElement = (Element) nodes.item(i);
	    String nodeName = childElement.getNodeName();
	    if (childElement.getNodeName().equals(Widget.N_ICON)) {
		validateIcon(childElement);
	    } else if (nodeName.equals(Widget.N_CONTENT)) {
		validateContentElement(childElement);
		startFileValidated = true;
	    }
	}
	if (!startFileValidated) {
	    validateDefaultStartFile();
	}
	monitor.worked(1);
    }

    /**
     * Check if start file exists (if it's defined, the exact file should exist,
     * otherwise one of DEFAULT_START_FILES should exist
     * 
     * @param contentElement
     */
    private void validateContentElement(Element contentElement) {
	String contentSrc = contentElement.getAttribute(NodeElement.CONTENT_SRC
		.getNodeName());

	if (contentSrc != null && !contentSrc.isEmpty()) {
	    IFile startFile = fProject.getFile(PATH_SRC + contentSrc); //$NON-NLS-1$
	    if (!startFile.exists()) {
		WidgetMarkerFactory.addFileMarker(fFile, NLS.bind(
			Messages.ConfigFileErrorReporter_StartFileDoesntExist,
			contentSrc), IMarker.SEVERITY_ERROR,
			getLine(contentElement));
	    }
	} else {
	    WidgetMarkerFactory.addFileMarker(fFile, NLS.bind(
		    Messages.ConfigFileErrorReporter_ContentSrcEmpty,
		    contentSrc), IMarker.SEVERITY_ERROR,
		    getLine(contentElement));
	}
    }

    /**
     * Validates existence of default start file
     */
    private void validateDefaultStartFile() {
	for (String startFile : DEFAULT_START_FILES) {
	    if (fProject.getFile(PATH_SRC + startFile).exists()) { //$NON-NLS-1$
		return;
	    }
	}
	WidgetMarkerFactory.addProjectMarker(fProject, IMarker.SEVERITY_ERROR,
		Messages.ConfigFileErrorReporter_StartFileNotFound);
    }

    private void validateIcon(Element iconElement) {

	String iconPath = iconElement.getAttribute("src"); //$NON-NLS-1$
	if (!fProject.getFile(PATH_SRC + iconPath).exists()) { //$NON-NLS-1$
	    WidgetMarkerFactory.addFileMarker(fFile, NLS.bind(
		    Messages.ConfigFileErrorReporter_IconNotFound, iconPath),
		    IMarker.SEVERITY_WARNING, getLine(iconElement));
	}
    }

}
