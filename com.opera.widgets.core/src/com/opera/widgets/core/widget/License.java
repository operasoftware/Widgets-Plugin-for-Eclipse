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

import org.w3c.dom.Node;

public class License extends AbstractWidgetObject {

    // XML node names
    private static final String N_HREF = "href"; //$NON-NLS-1$

    private String fHref;
    private String fDescription;
    private String language;

    public String getHref() {
	return fHref;
    }

    public void setHref(String href) {
	String oldValue = this.fHref;
	if (!href.equals(oldValue)) {
	    this.fHref = href;
	    firePropertyChanged(NodeElement.LICENSE_HREF, oldValue, href,
		    language);
	}
    }

    public String getDescription() {
	return fDescription;
    }

    public void setDescription(String description) {
	String oldValue = this.fDescription;
	if (!description.equals(oldValue)) {
	    this.fDescription = description;
	    firePropertyChanged(NodeElement.LICENSE, oldValue, description,
		    language);
	}
    }

    @Override
    public boolean isValid() {
	return true;
    }

    @Override
    public void load(Node node) {
	fHref = getNodeAttribute(node, N_HREF);
	Node textNode = node.getFirstChild();
	fDescription = (textNode != null) ? textNode.getNodeValue() : null;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result
		+ ((fDescription == null) ? 0 : fDescription.hashCode());
	result = prime * result + ((fHref == null) ? 0 : fHref.hashCode());
	return result;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	License other = (License) obj;
	if (fDescription == null) {
	    if (other.fDescription != null)
		return false;
	} else if (!fDescription.equals(other.fDescription))
	    return false;
	if (fHref == null) {
	    if (other.fHref != null)
		return false;
	} else if (!fHref.equals(other.fHref))
	    return false;
	return true;
    }

    @Override
    public String toString() {
	return "License [fHref=" + fHref + ", fDescription=" + fDescription //$NON-NLS-1$ //$NON-NLS-2$
		+ "]"; //$NON-NLS-1$
    }

    public String getLanguage() {
	return language;
    }

    public void setLanguage(String language) {
	this.language = language;
    }

}
