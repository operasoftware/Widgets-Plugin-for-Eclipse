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

import java.util.logging.Logger;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;

public class WidgetMarkerFactory {

    public static final String MARKER_ID = "com.opera.widgets.core.problem"; //$NON-NLS-1$

    public static void addProjectMarker(IProject project, int severity,
	    String message) {
	IMarker marker;
	try {
	    marker = project.createMarker(MARKER_ID);
	    marker.setAttribute(IMarker.SEVERITY, severity);
	    marker.setAttribute(IMarker.MESSAGE, message);
	} catch (CoreException e) {
	    Logger.getLogger("WidgetMarkerFactory").info(e.getMessage()); //$NON-NLS-1$
	    e.printStackTrace();
	}
    }

    public static void addFileMarker(IFile fFile, String message, int severity,
	    int line) {

	try {
	    IMarker marker = fFile.createMarker(MARKER_ID);
	    marker.setAttribute(IMarker.SEVERITY, severity);
	    marker.setAttribute(IMarker.MESSAGE, message);
	    if (line == -1) {
		line = 1;
	    }
	    marker.setAttribute(IMarker.LINE_NUMBER, line);
	} catch (CoreException e) {
	    e.printStackTrace();
	}

    }
}
