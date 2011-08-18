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

package com.opera.widgets.core.widget;

import java.util.List;

import org.eclipse.wst.xml.core.internal.provisional.format.FormatProcessorXML;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.opera.widgets.core.IModelChangedEvent;
import com.opera.widgets.core.IModelChangedListener;
import com.opera.widgets.core.widget.NodeElement.WidgetNamespace;

/**
 * Class is responsible of changing source code of config.xml file
 * 
 * TODO FormatProcessorXML it's not recommended, make own solution.
 */
public class WACWidgetBase implements IModelChangedListener {

    private static final String LANG_ATTR = "xml:lang"; //$NON-NLS-1$
    private static final int XMLNS_LENGTH = 6;
    private Document configDocument;
    private WidgetModel fModel;
    private FormatProcessorXML xmlFormatter = new FormatProcessorXML();

    @Override
    public void modelChanged(IModelChangedEvent event) {
	Object[] objects = event.getChangedObjects();
	Widget widget = getModel().getWidget();
	switch (event.getChangeType()) {
	case OBJECT_CHANGE:
	    if (objects[0] == null) {
		// updating attribute or value
		if (event.getLanguage() != null) {
		    this.setLocalizedElement(event.getChangedProperty(),
			    (String) event.getNewValue(), event.getLanguage());
		} else {
		    this.setElement(event.getChangedProperty(),
			    (String) event.getNewValue());
		}
	    } else {
		// updating object (icon, feature, preference)
		for (Object o : objects) {
		    if (o instanceof Icon) {
			int iconIndex = widget.getIcons().indexOf(o);
			this.update(iconIndex, (Icon) o);
		    } else if (o instanceof Feature) {
			this.update(widget.getFeatures().indexOf(o),
				(Feature) o);
		    } else if (o instanceof Preference) {
			this.update(widget.getPreferences().indexOf(o),
				(Preference) o);
		    } else if (o instanceof Access) {
			this.update(widget.getAccessList().indexOf(o),
				(Access) o);
		    }
		}
	    }
	    break;
	case OBJECT_INSERT:
	    for (Object o : objects) {
		if (o instanceof Icon) {
		    this.add((Icon) o);
		} else if (o instanceof Feature) {
		    this.add((Feature) o);
		} else if (o instanceof Preference) {
		    this.add((Preference) o);
		} else if (o instanceof Access) {
		    this.add((Access) o);
		}
	    }
	    break;
	case OBJECT_REMOVE:
	    objects = event.getChangedObjects();
	    for (Object o : objects) {
		if (o instanceof Icon) {
		    this.removeIcon(widget.getIcons().indexOf(o));
		} else if (o instanceof Feature) {
		    this.removeFeature(widget.getFeatures().indexOf(o));
		} else if (o instanceof Preference) {
		    this.removePreference(widget.getPreferences().indexOf(o));
		} else if (o instanceof Access) {
		    this.removeAccess(widget.getAccessList().indexOf(o));
		}
	    }
	    break;
	}
    }

    public WidgetModel getModel() {
	return fModel;
    }

    public void setModel(WidgetModel model) {
	fModel = model;
    }

    public void setLocalizedElement(NodeElement nodeIndex, String value,
	    String language) {
	switch (nodeIndex.getNodeType()) {
	case ELEMENT:
	    setNodeValue(nodeIndex, value, language);
	    break;
	case ATTRIBUTE:
	    setAttributeValue(nodeIndex, value, language);
	    break;
	}
    }

    public void setElement(NodeElement nodeIndex, String value) {
	switch (nodeIndex.getNodeType()) {
	case ELEMENT:
	    setNodeValue(nodeIndex, value, null);
	    break;
	case ATTRIBUTE:
	    setAttributeValue(nodeIndex, value, null);
	    break;
	}

    }

    public void setConfigContent(Document configContent) {
	this.configDocument = configContent;
    }

    /**
     * Adds access to DOM document
     * 
     * @param access
     */
    public void add(Access access) {
	String nodeName = "access"; //$NON-NLS-1$
	String qualifiedNodeName = getPrefixWithColon() + nodeName;
	Element accessElement = configDocument
		.createElementNS(NodeElement.ACCESS.getNamespace().getValue(),
			qualifiedNodeName);
	if (access.getOrigin() != null) {
	    accessElement.setAttribute("origin", access.getOrigin()); //$NON-NLS-1$
	}
	if (access.isSubdomains()) {
	    accessElement.setAttribute(
		    "subdomains", Boolean.toString(access.isSubdomains())); //$NON-NLS-1$
	}
	insertAsLastNode(accessElement, configDocument.getDocumentElement(),
		NodeElement.WIDGET.getNamespace().getValue());
	xmlFormatter.formatNode(accessElement.getParentNode());
    }

    /**
     * Updates access details in DOM document
     * 
     * @param accessIndex
     * @param access
     */
    public void update(int accessIndex, Access access) {
	NodeList nodeList = getElement(NodeElement.WIDGET)
		.getElementsByTagNameNS(
			NodeElement.WIDGET.getNamespace().getValue(), "access"); //$NON-NLS-1$
	Element accessElement = (Element) nodeList.item(accessIndex);
	if (accessElement == null) {
	    return;
	}
	accessElement.setAttribute("origin", access.getOrigin()); //$NON-NLS-1$
	accessElement.setAttribute(
		"subdomains", Boolean.toString(access.isSubdomains())); //$NON-NLS-1$
    }

    /**
     * Removes icon from DOM document
     * 
     * @param index
     */
    public void removeAccess(int index) {
	configDocument.normalize();
	NodeList nodeList = configDocument.getElementsByTagNameNS(
		NodeElement.WIDGET.getNamespace().getValue(), "access"); //$NON-NLS-1$
	if (nodeList.getLength() == 0) {
	    return;
	}
	Node toDelete = nodeList.item(index);
	toDelete.getParentNode().removeChild(toDelete);
    }

    /**
     * Adds icon to DOM document
     * 
     * @param icon
     */
    public void add(Icon icon) {
	String nodeName = getPrefixWithColon() + "icon"; //$NON-NLS-1$
	Element iconElement = configDocument.createElementNS(NodeElement.WIDGET
		.getNamespace().getValue(), nodeName);
	iconElement.setAttribute("src", icon.getPath()); //$NON-NLS-1$
	if (icon.getWidth() != null) {
	    iconElement.setAttribute("width", icon.getWidth().toString()); //$NON-NLS-1$
	}
	if (icon.getHeight() != null) {
	    iconElement.setAttribute("height", icon.getHeight().toString()); //$NON-NLS-1$
	}
	insertAsLastNode(iconElement, configDocument.getDocumentElement(),
		NodeElement.WIDGET.getNamespace().getValue());

	xmlFormatter.formatNode(iconElement.getParentNode());
    }

    /**
     * Updates icon details in DOM document
     * 
     * @param iconIndex
     * @param icon
     */
    public void update(int iconIndex, Icon icon) {
	NodeList nodeList = getElement(NodeElement.WIDGET)
		.getElementsByTagNameNS(
			NodeElement.WIDGET.getNamespace().getValue(), "icon"); //$NON-NLS-1$
	Element iconElement = (Element) nodeList.item(iconIndex);
	if (iconElement == null) {
	    return;
	}
	iconElement.setAttribute("src", icon.getPath()); //$NON-NLS-1$
	if (icon.getWidth() == null) {
	    iconElement.removeAttribute("width"); //$NON-NLS-1$
	} else {
	    iconElement.setAttribute("width", icon.getWidth().toString()); //$NON-NLS-1$
	}
	if (icon.getHeight() == null) {
	    iconElement.removeAttribute("height"); //$NON-NLS-1$
	} else {
	    iconElement.setAttribute("height", icon.getHeight().toString()); //$NON-NLS-1$
	}
    }

    /**
     * Removes icon from DOM document
     * 
     * @param index
     */
    public void removeIcon(int index) {
	configDocument.normalize();
	NodeList nodeList = configDocument.getElementsByTagNameNS(
		NodeElement.WIDGET.getNamespace().getValue(), "icon"); //$NON-NLS-1$
	if (nodeList.getLength() == 0) {
	    return;
	}
	Node toDelete = nodeList.item(index);
	toDelete.getParentNode().removeChild(toDelete);
	new FormatProcessorXML()
		.formatNode(configDocument.getDocumentElement());
    }

    public Document getDocument() {
	return configDocument;
    }

    /**
     * Adds feature to DOM document
     * 
     * @param feature
     *            to add
     */
    public void add(Feature feature) {
	String nodeName = getPrefixWithColon() + "feature"; //$NON-NLS-1$
	Element newFeature = configDocument.createElementNS(NodeElement.WIDGET
		.getNamespace().getValue(), nodeName);
	if (feature.getName() != null) {
	    newFeature.setAttribute("name", feature.getName()); //$NON-NLS-1$
	}
	newFeature.setAttribute(
		"required", Boolean.toString(feature.isRequired())); //$NON-NLS-1$
	for (Param param : feature.getParams()) {
	    add(param, newFeature);
	}
	// adding node just after last feature node
	insertAsLastNode(newFeature, configDocument.getDocumentElement(),
		NodeElement.WIDGET.getNamespace().getValue());

    }

    /**
     * Updates icon details in DOM document
     * 
     * @param featureIndex
     * @param feature
     */
    public void update(int featureIndex, Feature feature) {
	NodeList nodeList = getElement(NodeElement.WIDGET)
		.getElementsByTagNameNS(
			NodeElement.WIDGET.getNamespace().getValue(), "feature"); //$NON-NLS-1$
	Node featureNode = nodeList.item(featureIndex);

	Element newNode = configDocument.createElementNS(NodeElement.WIDGET
		.getNamespace().getValue(), getPrefixWithColon() + "feature"); //$NON-NLS-1$
	Element newElement = (Element) newNode;
	if (feature.getName() != null) {
	    newElement.setAttribute("name", feature.getName()); //$NON-NLS-1$
	} else {
	    newElement.removeAttribute("name"); //$NON-NLS-1$
	}

	newElement.setAttribute(
		"required", Boolean.toString(feature.isRequired())); //$NON-NLS-1$
	List<Param> params = feature.getParams();
	for (int i = 0; i < params.size(); i++) {
	    add(params.get(i), (Element) newNode);
	}
	newNode = (Element) featureNode.getParentNode().replaceChild(newNode,
		featureNode);

    }

    /**
     * Removes feature in DOM document
     * 
     * @param index
     */
    public void removeFeature(int index) {
	configDocument.normalize();
	NodeList nodeList = getElement(NodeElement.WIDGET)
		.getElementsByTagNameNS(
			NodeElement.WIDGET.getNamespace().getValue(), "feature"); //$NON-NLS-1$
	Node toDelete = nodeList.item(index);
	toDelete.getParentNode().removeChild(toDelete);
    }

    public void add(Preference pref) {
	String nodeName = getPrefixWithColon() + "preference"; //$NON-NLS-1$
	Element prefElement = configDocument.createElementNS(NodeElement.WIDGET
		.getNamespace().getValue(), nodeName);
	prefElement.setAttribute("name", pref.getName()); //$NON-NLS-1$
	prefElement.setAttribute("value", pref.getValue()); //$NON-NLS-1$
	prefElement.setAttribute(
		"readonly", Boolean.toString(pref.isReadonly())); //$NON-NLS-1$
	insertAsLastNode(prefElement, configDocument.getDocumentElement(),
		NodeElement.WIDGET.getNamespace().getValue());
	xmlFormatter.formatNode(prefElement.getParentNode());

    }

    public void update(int index, Preference pref) {
	NodeList nodeList = getElement(NodeElement.WIDGET)
		.getElementsByTagNameNS(
			NodeElement.WIDGET.getNamespace().getValue(),
			"preference"); //$NON-NLS-1$
	Element element = (Element) nodeList.item(index);
	if (element == null) {
	    return;
	}
	element.setAttribute("name", pref.getName()); //$NON-NLS-1$
	element.setAttribute("value", pref.getValue()); //$NON-NLS-1$
	element.setAttribute("readonly", Boolean.toString(pref.isReadonly())); //$NON-NLS-1$
    }

    public void removePreference(int index) {
	configDocument.normalize();
	NodeList nodeList = configDocument.getElementsByTagNameNS(
		NodeElement.WIDGET.getNamespace().getValue(), "preference"); //$NON-NLS-1$
	if (nodeList.getLength() == 0) {
	    return;
	}
	Node toDelete = nodeList.item(index);
	toDelete.getParentNode().removeChild(toDelete);

    }

    /**
     * Gets attribute value of specific element
     * 
     * @param element
     * @return
     */
    private String getAttributeValue(NodeElement element) {
	Node node = getElement(element.getParent());
	if (node == null) {
	    return null;
	}
	NamedNodeMap nodeMap = node.getAttributes();
	node = nodeMap.getNamedItem(element.getNodeName());
	if (node != null) {
	    return node.getNodeValue();
	}
	return null;
    }

    private void setNodeValue(NodeElement nodeNameIndex, String nodeValue,
	    String language) {

	if (nodeValue.equals(getNodeValue(nodeNameIndex, language))) {
	    return;
	}

	String nodeName = nodeNameIndex.getNodeName();
	NodeElement parentNodeElement = nodeNameIndex.getParent();
	String parent = parentNodeElement.getNodeName();
	// get parent node
	NodeList parentNodeList = configDocument.getElementsByTagNameNS(
		parentNodeElement.getNamespace().getValue(), parent);
	Element parentElement = null;

	// node not exists
	if (parentNodeList.getLength() == 0) {
	    // TODO creating widget node if it not exists

	    String widgetNodeName = getPrefixWithColon() + "widget";//$NON-NLS-1$

	    Element widgetElement = configDocument.createElementNS(
		    NodeElement.WIDGET.getNamespace().getValue(),
		    widgetNodeName);

	    widgetElement
		    .setAttributeNS(
			    "http://www.w3.org/2000/xmlns/", "xmlns", NodeElement.WIDGET //$NON-NLS-1$
				    .getNamespace().getValue());

	    parentElement = (Element) configDocument.appendChild(widgetElement);
	} else {
	    parentElement = (Element) parentNodeList.item(0);
	}

	parentElement.normalize();
	// get child node
	NodeList nodeList = parentElement.getElementsByTagNameNS(nodeNameIndex
		.getNamespace().getValue(), nodeName);

	Element destinationElement = null;
	for (int i = 0; i < nodeList.getLength() && destinationElement == null; i++) {
	    Element el = (Element) nodeList.item(i);
	    String langAttr = el.getAttribute(LANG_ATTR);
	    if (langAttr.equals("") && language == null //$NON-NLS-1$
		    || langAttr.equals(language)) {
		destinationElement = el;
	    }
	}

	if (destinationElement == null) {
	    Element childElement = configDocument
		    .createElementNS(nodeNameIndex.getNamespace().getValue(),
			    getPrefixWithColon() + nodeName);
	    childElement.appendChild(configDocument.createTextNode(nodeValue));
	    // if there are similar elements, put it at the beginning
	    insertAsLastNode(childElement, parentElement, NodeElement.WIDGET
		    .getNamespace().getValue());

	    if (language != null) {
		childElement.setAttribute(LANG_ATTR, language);
	    }
	    xmlFormatter.formatNode(childElement.getParentNode());
	} else {
	    Node firstChild = destinationElement.getFirstChild();
	    if (firstChild == null) {
		destinationElement.appendChild(configDocument
			.createTextNode(nodeValue));
	    } else {
		firstChild.setNodeValue(nodeValue);
	    }
	}
    }

    private String getPrefixWithColon() {
	String prefix = configDocument.getDocumentElement().getPrefix();
	if (prefix == null || prefix.isEmpty()) {
	    return "";
	}
	return prefix + ":";
    }

    /**
     * Gets node with specific enum type (retrieves it recursively)
     * 
     * @param element
     * @return
     */
    private Element getElement(NodeElement element) {
	return getElement(element, null);
    }

    /**
     * Gets node with specific enum type (retrieves it recursively) and specific
     * language node
     * 
     * @param element
     * @param language
     *            - null = default language
     * @return
     */
    private Element getElement(NodeElement element, String language) {
	NodeList nodeList = null;
	if (element.getParent() != null) {
	    Element el = getElement(element.getParent(), null);
	    if (el != null) {
		nodeList = el.getElementsByTagNameNS(element.getNamespace()
			.getValue(), element.getNodeName());
	    }
	} else {
	    // if it's parent we get node from root node
	    nodeList = configDocument.getElementsByTagNameNS(NodeElement.WIDGET
		    .getNamespace().getValue(), element.getNodeName());
	}
	if (nodeList == null || nodeList.getLength() == 0) {
	    return null;
	}
	Element result = null;
	for (int i = 0; i < nodeList.getLength() && result == null; i++) {
	    Element item = (Element) nodeList.item(i);

	    String langAttr = item.getAttribute(LANG_ATTR);
	    if (language == null && langAttr.isEmpty()
		    || langAttr.equals(language)) {
		result = item;
	    }
	}

	return result;
    }

    private Node createNode(Element parent, String nodeName, String namespace) {
	String qualifiedName = getPrefixWithColon() + nodeName;
	Node element = parent.appendChild(configDocument.createElementNS(
		namespace, qualifiedName));
	xmlFormatter.formatNode(element.getParentNode());
	return element;
    }

    private String getNodeValue(NodeElement nodeElement, String language) {
	Node node = getElement(nodeElement);
	if (node == null) {
	    return null;
	}
	Node firstChild = node.getFirstChild();
	return (firstChild != null) ? firstChild.getNodeValue() : null;
    }

    /**
     * Adds node after last node of nodes with specific type
     * 
     * @param nodeName
     * @param newNode
     */
    private void insertAsLastNode(Node newNode, Element parent, String namespace) {
	NodeList nodeList = parent.getElementsByTagNameNS(namespace,
		newNode.getNodeName());
	if (nodeList.getLength() > 0) {
	    Element lastNode = (Element) nodeList
		    .item(nodeList.getLength() - 1);
	    if (lastNode.getNextSibling() != null) {
		parent.insertBefore(newNode, lastNode.getNextSibling());
	    } else {
		parent.appendChild(newNode);
	    }
	} else {
	    parent.appendChild(newNode);
	}
	xmlFormatter.formatNode(newNode.getParentNode());
    }

    /**
     * Adds param node to specific feature node
     * 
     * @param param
     * @param featureNode
     */
    private void add(Param param, Node featureNode) {
	String nodeName = getPrefixWithColon() + "param"; //$NON-NLS-1$
	Node paramElement = configDocument.createElementNS(NodeElement.WIDGET
		.getNamespace().getValue(), nodeName);
	if (param.getName() != null) {
	    ((Element) paramElement).setAttribute("name", param.getName()); //$NON-NLS-1$
	}
	if (param.getValue() != null) {
	    ((Element) paramElement).setAttribute("value", param.getValue()); //$NON-NLS-1$
	}
	insertAsLastNode(paramElement, (Element) featureNode,
		NodeElement.WIDGET.getNamespace().getValue());
	xmlFormatter.formatNode(paramElement.getParentNode());
    }

    private void setAttributeValue(NodeElement nodeIndex, String value,
	    String language) {
	Element parentElement = getElement(nodeIndex.getParent(), language);

	// if node does not exist, create it
	if (value.isEmpty()) {
	    if (parentElement != null) {
		parentElement.removeAttribute(nodeIndex.getNodeName());
	    }
	    return;
	}
	if (parentElement == null) {
	    parentElement = createNode(nodeIndex.getParent());
	    if (language != null) {
		parentElement.setAttribute("xml:lang", language); //$NON-NLS-1$
	    }
	}
	String attributeValue = getAttributeValue(nodeIndex);
	if (attributeValue == null || !attributeValue.equals(value)) {
	    parentElement.setAttribute(nodeIndex.getNodeName(), value);
	}
    }

    private Element createNode(NodeElement element) {
	Element newNode;
	if (element.getNamespace().equals(NodeElement.WidgetNamespace.JIL)) {
	    setJilNamespace();
	    newNode = configDocument.createElementNS(element.getNamespace()
		    .getValue(), lookupPrefix(NodeElement.JIL_ACCESS
		    .getNamespace().getValue()) + ":" + element.getNodeName()); //$NON-NLS-1$
	} else {
	    newNode = configDocument.createElementNS(element.getNamespace()
		    .getValue(), getPrefixWithColon() + element.getNodeName());
	}

	if (element.getParent() != null) {
	    Element parentNode = getElement(element.getParent());
	    if (parentNode == null) {
		parentNode = createNode(element.getParent());
	    }
	    newNode = (Element) parentNode.appendChild(newNode);
	} else {
	    // new widget node
	    newNode = (Element) configDocument.getDocumentElement()
		    .appendChild(newNode);
	}
	xmlFormatter.formatNode(newNode.getParentNode());
	return newNode;
    }

    /**
     * Sets JIL namespace into Widget Element
     */
    private void setJilNamespace() {
	Element widgetElement = getElement(NodeElement.WIDGET);
	if (!lookupPrefix(WidgetNamespace.JIL.getValue()).isEmpty()) { //$NON-NLS-1$
	    return;
	}
	widgetElement.setAttributeNS(
		"http://www.w3.org/2000/xmlns/", "xmlns:JIL", //$NON-NLS-1$
		NodeElement.WidgetNamespace.JIL.getValue());
    }

    private String lookupPrefix(String namespace) {

	NamedNodeMap map = configDocument.getDocumentElement().getAttributes();
	for (int i = 0, n = map.getLength(); i < n; i++) {
	    Node node = map.item(i);
	    if ("http://www.w3.org/2000/xmlns/".equals(node.getNamespaceURI())) {
		if (node.getNodeValue().equals(namespace)) {
		    return node.getNodeName().substring(XMLNS_LENGTH);
		}
	    }
	}
	return "";
    }
}
