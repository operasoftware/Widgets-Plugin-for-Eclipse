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

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class XMLErrorReporter extends DefaultHandler {

    protected IFile fFile;
    protected IProject fProject;
    private Document fXMLDocument;
    private Locator fLocator;
    protected Element fRootElement;
    protected Stack<Element> fElementStack;
    protected Map<Element, ConfigElement> fElementsMap;

    class ConfigElement {
	int offset;
	boolean fErrorNode;

	public ConfigElement(int offset) {
	    this.offset = offset;
	}
    }

    public XMLErrorReporter(IFile file) {

	fFile = file;
	fProject = file.getProject();
	fElementsMap = new HashMap<Element, ConfigFileErrorReporter.ConfigElement>();
	fElementStack = new Stack<Element>();
	removeMarkers();
    }

    /**
     * Remove all markers from Config file
     */
    protected void removeMarkers() {
	try {
	    fFile.deleteMarkers(WidgetMarkerFactory.MARKER_ID, true, 0);
	} catch (CoreException e) {
	    Logger.getLogger(getClass().getName())
		    .info(Messages.XMLErrorReporter_ErrorDeletingMarkerTitle);
	    e.printStackTrace();
	}
    }

    @Override
    public void startDocument() throws SAXException {
	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	try {
	    fXMLDocument = factory.newDocumentBuilder().newDocument();
	} catch (ParserConfigurationException e) {
	    Logger.getLogger(getClass().getName()).log(Level.WARNING,
		    Messages.XMLErrorReporter_ErrorDeletingMarkerDesc + e.getMessage());
	}
    }

    @Override
    public void startElement(String uri, String localName, String qName,
	    Attributes attributes) throws SAXException {
	Element element = fXMLDocument.createElement(qName);
	for (int i = 0; i < attributes.getLength(); i++) {
	    element.setAttribute(attributes.getQName(i), attributes.getValue(i));
	}

	if (fRootElement == null) {
	    fRootElement = element;
	} else {
	    (fElementStack.peek()).appendChild(element);
	}
	fElementStack.push(element);

	fElementsMap.put(element, new ConfigElement(fLocator.getLineNumber()));
    }

    public int getLine(Element element) {
	return fElementsMap.get(element).offset;
    }

    @Override
    public void setDocumentLocator(Locator locator) {
	fLocator = locator;
    }

    @Override
    public void endElement(String uri, String localName, String qName)
	    throws SAXException {
	fElementStack.pop();
    }

    @Override
    public void endDocument() throws SAXException {
    }

}