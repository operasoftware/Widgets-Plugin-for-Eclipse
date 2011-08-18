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

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.osgi.framework.Bundle;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.opera.widgets.core.widget.NodeElement;
import com.opera.widgets.core.widget.WACWidgetBase;

/**
 * Supports creating widget projects. Creates directory structure and adds basic
 * files.
 * 
 * @author Michal Borek <mborek@opera.com>
 */
public class CreateWidgetProjectSupport {

    private static final String RESOURCES_PATH = "/resources/project/"; //$NON-NLS-1$
    private static String[] initialProjectPaths = {
	    "docs", "src/icons", "src/libs", "src/script", "src/style" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
    private static String[] initialProjectFiles = {
	    "src/index.html", "src/config.xml", "src/script/main.js", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	    "src/style/main.css" }; //$NON-NLS-1$

    private String fProjectNature;

    public CreateWidgetProjectSupport(String projectNature) {
	this.fProjectNature = projectNature;
    }

    /**
     * Creates new project with given name in given location
     * 
     * @param projectName
     * @param location
     * @throws CoreException
     */
    public IProject createProject(String projectName, URI location,
	    boolean copyFiles) throws CoreException {
	Assert.isNotNull(projectName);

	IProject newProject = createBaseProject(projectName, location);

	addNature(newProject);
	if (copyFiles) {
	    createProjectStructure(newProject);

	    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	    dbf.setNamespaceAware(true);
	    DocumentBuilder db;
	    try {
		db = dbf.newDocumentBuilder();
		Document configContent = db.parse(newProject.getFile(
			"/src/config.xml").getContents()); //$NON-NLS-1$
		WACWidgetBase configModel = new WACWidgetBase();

		configModel.setConfigContent(configContent);
		configModel.setElement(NodeElement.NAME, newProject.getName());
		setAuthorData(configModel);
		writeXmlFile(configContent,
			newProject.getFile("/src/config.xml") //$NON-NLS-1$
				.getRawLocationURI());
	    } catch (ParserConfigurationException e) {
		e.printStackTrace();
	    } catch (SAXException e) {
		e.printStackTrace();
	    } catch (IOException e) {
		e.printStackTrace();
	    }
	}

	return newProject;

    }

    private void setAuthorData(WACWidgetBase configModel) {
	IEclipsePreferences preferences = DefaultScope.INSTANCE
		.getNode(WidgetsActivator.PLUGIN_ID + ".config.author"); //$NON-NLS-1$
	String authorName = preferences.get("name", null); //$NON-NLS-1$
	String authorHref = preferences.get("href", null); //$NON-NLS-1$
	String authorEmail = preferences.get("email", null); //$NON-NLS-1$
	if (authorName != null) {
	    configModel.setElement(NodeElement.AUTHOR, authorName);
	}
	if (authorEmail != null) {
	    configModel.setElement(NodeElement.AUTHOR_EMAIL, authorEmail);
	}
	if (authorHref != null) {
	    configModel.setElement(NodeElement.AUTHOR_HREF, authorHref);
	}
    }

    /**
     * Creates initial project directory structure
     * 
     * @param newProject
     * @param copyFiles
     * @throws CoreException
     */
    private void createProjectStructure(IProject newProject)
	    throws CoreException {

	for (String p : initialProjectPaths) {
	    IFolder newFolder = newProject.getFolder(p);
	    createFolder(newFolder);
	}
	copyFiles(newProject);
    }

    /**
     * Creates folder in project directory
     * 
     * @param newFolder
     * @throws CoreException
     */
    private void createFolder(IFolder newFolder) throws CoreException {
	IContainer parent = newFolder.getParent();
	if (parent instanceof IFolder) {
	    createFolder((IFolder) parent);
	}
	if (!newFolder.exists()) {
	    newFolder.create(false, true, null);
	}
    }

    /**
     * Copy initial files to project directory
     * 
     * @param project
     */
    private void copyFiles(IProject project) {

	Bundle bundle = WidgetsActivator.getDefault().getBundle();
	for (String f : initialProjectFiles) {
	    URL url = bundle.getEntry(RESOURCES_PATH + f);
	    try {
		IFile file = project.getFile(f);
		if (!file.exists()) {
		    file.create(url.openStream(), false, null);
		}
	    } catch (CoreException e) {
		e.printStackTrace();
	    } catch (IOException e) {
		e.printStackTrace();
	    }
	}
    }

    /**
     * Adds widget nature to project so that project has its own builder
     * 
     * @param project
     * @throws CoreException
     */
    private void addNature(IProject project) throws CoreException {
	if (!project.hasNature(fProjectNature)) {
	    IProjectDescription description = project.getDescription();
	    String[] oldNatures = description.getNatureIds();
	    String[] newNatures = Arrays.copyOf(oldNatures,
		    oldNatures.length + 1);
	    newNatures[oldNatures.length] = fProjectNature;
	    description.setNatureIds(newNatures);
	    project.setDescription(description, null);
	}

    }

    /**
     * Creates project in specified location
     * 
     * @param projectName
     * @param location
     *            - project location
     * @return created project
     */
    private IProject createBaseProject(String projectName, URI location) {
	IProject newProject = ResourcesPlugin.getWorkspace().getRoot()
		.getProject(projectName);

	if (!newProject.exists()) {
	    IProjectDescription desc = newProject.getWorkspace()
		    .newProjectDescription(newProject.getName());
	    URI projectLocation = location;
	    if (location != null
		    && ResourcesPlugin.getWorkspace().getRoot()
			    .getLocationURI().equals(location)) {
		projectLocation = null;
	    }

	    desc.setLocationURI(projectLocation);
	    // create and open new project
	    try {
		newProject.create(desc, null);
		if (!newProject.isOpen()) {
		    newProject.open(null);
		}
	    } catch (CoreException e) {
		e.printStackTrace();
	    }

	}
	return newProject;
    }

    public static void writeXmlFile(Document doc, URI filename) {
	try {
	    doc.setXmlStandalone(true);
	    // Prepare the DOM document for writing
	    Source source = new DOMSource(doc);

	    // Prepare the output file
	    File file = new File(filename);
	    StreamResult result = new StreamResult(file);

	    // Write the DOM document to the file
	    TransformerFactory transformerFactory = TransformerFactory
		    .newInstance();
	    Transformer xformer = transformerFactory.newTransformer();

	    xformer.transform(source, result);
	} catch (TransformerConfigurationException e) {
	} catch (TransformerException e) {
	}
    }
}
