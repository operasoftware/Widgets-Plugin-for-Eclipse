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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * POJO for feature node from config.xml
 * 
 * @author Michal Borek
 * 
 */
public class Feature extends AbstractWidgetObject {

    // XML node names
    private static final String N_NAME = "name"; //$NON-NLS-1$
    private static final String N_REQUIRED = "required"; //$NON-NLS-1$
    private static final Object N_PARAM = "param"; //$NON-NLS-1$

    private String fName;
    private boolean fRequired;

    private List<Param> fParams;

    public Feature() {
	fParams = new ArrayList<Param>();
    }

    public String getName() {
	return fName;
    }

    public void setName(String name) {
	this.fName = name;
    }

    public boolean isRequired() {
	return fRequired;
    }

    public void setRequired(boolean required) {
	this.fRequired = required;
    }

    public List<Param> getParams() {
	return fParams;
    }

    public void setParams(List<Param> params) {
	this.fParams = params;
    }

    public void addParam(Param param) {
	this.fParams.add(param);
    }

    public void removeParam(Param elementToRemove) {
	fParams.remove(fParams.indexOf(elementToRemove));
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((fName == null) ? 0 : fName.hashCode());
	result = prime * result + (fRequired ? 1231 : 1237);
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
	Feature other = (Feature) obj;
	if (fName == null) {
	    if (other.fName != null)
		return false;
	} else if (!fName.equals(other.fName))
	    return false;
	if (fRequired != other.fRequired)
	    return false;
	return true;
    }

    @Override
    public String toString() {
	return "Feature [name=" + fName + ", required=" + fRequired + ", params=" + fParams + "]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
    }

    @Override
    public boolean isValid() {
	for (Param p : fParams) {
	    if (!p.isValid()) {
		return false;
	    }
	}

	return true;
    }

    @Override
    public void load(Node node) {
	fName = getNodeAttribute(node, N_NAME);
	fRequired = Boolean.parseBoolean(getNodeAttribute(node, N_REQUIRED));
	loadParams(node);
    }

    private void loadParams(Node node) {
	NodeList children = node.getChildNodes();

	for (int i = 0; i < children.getLength(); i++) {
	    Node child = children.item(i);
	    if (child.getNodeType() == Node.ELEMENT_NODE
		    && child.getNodeName().toLowerCase(Locale.ENGLISH).equals(N_PARAM)) { //$NON-NLS-1$
		Param param = new Param(this);
		param.setModel(getModel());
		param.load(child);
		fParams.add(param);
	    }
	}
    }

}
