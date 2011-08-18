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

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.opera.widgets.core.tools.ZipMaker;

//import com.opera.widgets.core.widget.WACWidgetBase;
//import com.opera.widgets.core.widget.Widget;
//import com.opera.widgets.core.widget.WidgetModel;

public class ConfigConsistencyChecker extends IncrementalProjectBuilder {

    private static final String SRC_CONFIG_XML = "src/config.xml"; //$NON-NLS-1$
    // private Widget fWidget;
    private IFile fConfigFile;

    @SuppressWarnings("rawtypes")
    @Override
    protected IProject[] build(int kind, Map args, IProgressMonitor monitor)
	    throws CoreException {

	IProject project = getProject();

	// fWidget = new Widget();
	fConfigFile = project.getFile(SRC_CONFIG_XML);
	// fWidget.loadFromFile(fConfigFile.getLocation().toFile());
	// WACWidgetBase widgetBase = new WACWidgetBase();
	// widgetBase.setConfigContent(widgetBase.getDocument());
	// WidgetModel model = new WidgetModel(fWidget);
	// model.addModelChangedListener(widgetBase);
	// fWidget.setModel(model);
	validateProject(project, monitor);

	if (kind == FULL_BUILD) {
	    fullBuild(project, monitor);
	} else {
	    IResourceDelta delta = getDelta(getProject());
	    if (delta == null
		    && !project.getFolder("build")
			    .getFile(project.getName() + ".wgt").exists()) {
		fullBuild(project, monitor);
	    } else {
		incrementalBuild(delta, monitor);
	    }
	}

	return null;
    }

    private void validateProject(IProject project, IProgressMonitor monitor) {

	// TODO delta type
	try {
	    project.deleteMarkers(WidgetMarkerFactory.MARKER_ID, false,
		    IResource.DEPTH_ZERO);
	} catch (CoreException e1) {
	    Logger.getLogger(getClass().getName()).info(
		    Messages.ConfigConsistencyChecker_ErrorDeletingMarkers1);
	}

	IFile projectFile = project.getFile(SRC_CONFIG_XML);
	if (projectFile.exists()) {
	    validateConfigFile(project, monitor);
	} else {
	    WidgetMarkerFactory
		    .addProjectMarker(
			    project,
			    IMarker.SEVERITY_ERROR,
			    Messages.ConfigConsistencyChecker_ErrorDeletingMarkersConfigDoesntExist);
	}
    }

    /**
     * Config.xml file validation
     * 
     * @param projectFile
     */
    private void validateConfigFile(IProject project, IProgressMonitor monitor) {

	SAXParserFactory factory = SAXParserFactory.newInstance();
	SAXParser parser;
	try {
	    ConfigFileErrorReporter handler = new ConfigFileErrorReporter(
		    fConfigFile);
	    parser = factory.newSAXParser();
	    parser.parse(fConfigFile.getLocation().toFile(), handler);

	    handler.validateContent(monitor);
	} catch (ParserConfigurationException e) {
	    e.printStackTrace();
	} catch (SAXException e) {
	    WidgetMarkerFactory.addFileMarker(fConfigFile, e.getMessage(),
		    IMarker.SEVERITY_ERROR,
		    ((SAXParseException) e).getLineNumber());
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    protected void fullBuild(final IProject project,
	    final IProgressMonitor monitor) throws CoreException {
	try {
	    createWidgetPackage(project);
	} catch (CoreException e) {
	}
    }

    protected void incrementalBuild(IResourceDelta delta,
	    IProgressMonitor monitor) throws CoreException {
	// the visitor does the work.
	delta.accept(new WidgetDeltaVisitor());
    }

    private void createWidgetPackage(IProject project) throws CoreException {
	try {
	    IFolder buildFolder = project.getFolder("build");
	    if (!buildFolder.exists()) {
		buildFolder.create(true, true, null);
	    } else {
		IFile buildFile = buildFolder.getFile(getProject().getName()
			+ ".wgt");
		if (buildFile.exists()) {
		    buildFile.delete(true, null);
		}
	    }

	    ZipMaker.zipDirectory(new File(project.getFolder("src")
		    .getRawLocationURI()), new File(buildFolder //$NON-NLS-1$
		    .getLocation().toOSString() + File.separator
		    + getProject().getName() + ".wgt")); //$NON-NLS-1$
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    class WidgetDeltaVisitor implements IResourceDeltaVisitor {
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.resources.IResourceDeltaVisitor#visit(org.eclipse
	 * .core.resources.IResourceDelta)
	 */
	public boolean visit(IResourceDelta delta) throws CoreException {
	    IResource resource = delta.getResource();
	    IProject project = getProject();
	    // System.err.println("res: " + resource.getName());
	    switch (delta.getKind()) {
	    case IResource.PROJECT:
		if (resource.getProjectRelativePath().toString().equals("src")) { //$NON-NLS-1$
		    createWidgetPackage(project);
		}
		break;
	    }
	    // return true to continue visiting children.
	    return true;
	    // TODO REFACTORING OF ICONS in config.xml this requires some time
	    // to implement

	    // IResource resource = delta.getResource();
	    // // System.err.println("res: " + resource.getName());
	    // switch (delta.getKind()) {
	    // case IResourceDelta.REMOVED:
	    // if (resource.getType() == IResource.FILE
	    // && (delta.getFlags() & IResourceDelta.MOVED_TO) != 0) {
	    // IFile file = (IFile) resource;
	    //
	    // // if is icon
	    // if (Arrays.binarySearch(WidgetsCore.LEGAL_ICON_EXTENSIONS,
	    // file.getFileExtension()) >= 0) {
	    // IFolder srcFolder = getProject().getFolder(
	    // WidgetsCore.WIDGET_SRC_FOLDER);
	    //
	    // List<Icon> icons = fWidget.getIcons();
	    // for (Icon i : icons) {
	    // IFile configIFile = srcFolder.getFile(i.getPath());
	    //
	    // if (configIFile
	    // .getProjectRelativePath()
	    // .toOSString()
	    // .equals(file.getProjectRelativePath()
	    // .toOSString())) {
	    // String iconPath = delta
	    // .getMovedToPath()
	    // .removeFirstSegments(
	    // srcFolder
	    // .getProjectRelativePath()
	    // .segmentCount())
	    // .toOSString();
	    // i.setPath(iconPath);
	    // }
	    // }
	    // }
	    // }
	    // break;
	    // case IResourceDelta.CHANGED:
	    // switch (resource.getType()) {
	    // case IResource.FILE:
	    // // System.err.println("file");
	    // break;
	    // }
	    // break;
	    // }
	    // // return true to continue visiting children.
	}
    }

    class WidgetResourceVisitor implements IResourceVisitor {
	public boolean visit(IResource resource) {
	    // checkXML(resource);
	    // return true to continue visiting children.
	    return true;
	}
    }
}
