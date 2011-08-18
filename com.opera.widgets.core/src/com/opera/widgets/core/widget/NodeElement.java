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

import static com.opera.widgets.core.widget.NodeElement.NodeType.ATTRIBUTE;
import static com.opera.widgets.core.widget.NodeElement.WidgetNamespace.*;
import static com.opera.widgets.core.widget.NodeElement.NodeType.ELEMENT;

public enum NodeElement {
    WIDGET("widget", ELEMENT), //$NON-NLS-1$
    WIDGET_ATTRIBUTE_WIDTH("width", ATTRIBUTE, WIDGET, ContentType.POSITIVE_INT), //$NON-NLS-1$
    WIDGET_ATTRIBUTE_HEIGHT(
	    "height", ATTRIBUTE, WIDGET, ContentType.POSITIVE_INT), //$NON-NLS-1$
    WIDGET_ATTRIBUTE_ID("id", ATTRIBUTE, WIDGET), //$NON-NLS-1$
    WIDGET_ATTRIBUTE_VERSION("version", ATTRIBUTE, WIDGET), //$NON-NLS-1$
    WIDGET_ATTRIBUTE_VIEWMODES("viewmodes", ATTRIBUTE, WIDGET), //$NON-NLS-1$
    NAME("name", ELEMENT, WIDGET), //$NON-NLS-1$
    NAME_SHORT("short", ATTRIBUTE, NAME), //$NON-NLS-1$
    DESCRIPTION("description", ELEMENT, WIDGET), //$NON-NLS-1$
    LICENSE("license", ELEMENT, WIDGET), //$NON-NLS-1$
    LICENSE_HREF("href", ATTRIBUTE, LICENSE, ContentType.LINK), //$NON-NLS-1$
    AUTHOR("author", ELEMENT, WIDGET), //$NON-NLS-1$
    AUTHOR_EMAIL("email", ATTRIBUTE, AUTHOR, ContentType.EMAIL), //$NON-NLS-1$
    AUTHOR_HREF("href", ATTRIBUTE, AUTHOR, ContentType.LINK), //$NON-NLS-1$
    SECURITY("security", ELEMENT, WIDGET), //$NON-NLS-1$
    SECURITY_ACCESS("access", ELEMENT, SECURITY), //$NON-NLS-1$
    SECURITY_CONTENT("content", ELEMENT, SECURITY), //$NON-NLS-1$
    SECURITY_CONTENT_PLUGINS("plugins", ATTRIBUTE, SECURITY_CONTENT), //$NON-NLS-1$
    CONTENT("content", ELEMENT, WIDGET), //$NON-NLS-1$
    CONTENT_SRC("src", ATTRIBUTE, CONTENT, ContentType.EXISTING_FILE), //$NON-NLS-1$
    CONTENT_TYPE("type", ATTRIBUTE, CONTENT), //$NON-NLS-1$
    CONTENT_ENCODING("encoding", ATTRIBUTE, CONTENT), //$NON-NLS-1$
    ACCESS("access", ELEMENT, WIDGET), //$NON-NLS-1$
    ACCESS_ORIGIN("uri", ATTRIBUTE, ACCESS), //$NON-NLS-1$
    ACCESS_SUBDOMAINS("subdomains", ATTRIBUTE, ACCESS), //$NON-NLS-1$
    JIL_ACCESS("access", ELEMENT, WIDGET, JIL), //$NON-NLS-1$
    JIL_ACCESS_NETWORK("network", ATTRIBUTE, JIL_ACCESS), //$NON-NLS-1$
    JIL_ACCESS_REMOTE_SCRIPTS("remote_scripts", ATTRIBUTE, JIL_ACCESS), //$NON-NLS-1$
    JIL_ACCESS_LOCALFS("localfs", ATTRIBUTE, JIL_ACCESS), //$NON-NLS-1$
    JIL_BILLING("billing", ELEMENT, WIDGET, JIL), //$NON-NLS-1$
    JIL_BILLING_REQUIRED("required", ATTRIBUTE, JIL_BILLING), //$NON-NLS-1$
    JIL_MAXIMUM_DISPLAY_MODE("maximum_display_mode", ELEMENT, WIDGET, JIL), //$NON-NLS-1$
    JIL_MAXIMUM_DISPLAY_MODE_WIDTH("width", ATTRIBUTE, JIL_MAXIMUM_DISPLAY_MODE), //$NON-NLS-1$
    JIL_MAXIMUM_DISPLAY_MODE_HEIGHT(
	    "height", ATTRIBUTE, JIL_MAXIMUM_DISPLAY_MODE); //$NON-NLS-1$

    private String nodeName;
    private WidgetNamespace namespace;

    private NodeElement parent;
    private NodeElement.NodeType nodeType;
    private NodeElement.ContentType contentType = ContentType.STRING;

    public enum NodeType {
	ATTRIBUTE, ELEMENT;
    }

    public enum WidgetNamespace {
	W3C("http://www.w3.org/ns/widgets"), JIL( //$NON-NLS-1$
		"http://www.jil.org/ns/widgets1.2"); //$NON-NLS-1$

	private String value;

	WidgetNamespace(String s) {
	    value = s;
	}

	public String getValue() {
	    return value;
	}
    }

    public enum ContentType {
	STRING, POSITIVE_INT, EMAIL, LINK, EXISTING_FILE;
    }

    NodeElement(String nodeName, NodeElement.NodeType type) {
	this.nodeName = nodeName;
	this.nodeType = type;
	this.namespace = WidgetNamespace.W3C;
    }

    NodeElement(String nodeName, NodeElement.NodeType type, NodeElement parent) {
	this(nodeName, type);
	this.parent = parent;
    }

    NodeElement(String nodeName, NodeElement.NodeType type, NodeElement parent,
	    NodeElement.ContentType contentType) {
	this(nodeName, type, parent);
	this.contentType = contentType;
    }

    NodeElement(String nodeName, NodeElement.NodeType type, NodeElement parent,
	    WidgetNamespace namespace) {
	this(nodeName, type, parent);
	this.namespace = namespace;
    }

    public String getNodeName() {
	return nodeName;
    }

    public NodeElement getParent() {
	return parent;
    }

    public NodeElement.NodeType getNodeType() {
	return nodeType;
    }

    public NodeElement.ContentType getContentType() {
	return contentType;
    }

    public WidgetNamespace getNamespace() {
	return namespace;
    }
}