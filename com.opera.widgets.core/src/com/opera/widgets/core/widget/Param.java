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

/**
 * POJO for Param node from config.xml
 * 
 * @author Michal Borek
 * 
 */
public class Param extends AbstractWidgetObject {
   
    // XML node names
    private static final String N_NAME = "name"; //$NON-NLS-1$
    private static final String N_VALUE = "value"; //$NON-NLS-1$

    private String fName;
    private String fValue;
    private Feature fParent;

    public Param(Feature parent) {
	this.fParent = parent;
    }

    public String getName() {
	return fName;
    }

    public void setName(String name) {
	this.fName = name;
    }

    public String getValue() {
	return fValue;
    }

    public void setValue(String value) {
	this.fValue = value;
    }

    public Feature getParent() {
	return fParent;
    }

    public void setParent(Feature parent) {
	this.fParent = parent;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((fName == null) ? 0 : fName.hashCode());
	result = prime * result + ((fValue == null) ? 0 : fValue.hashCode());
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
	Param other = (Param) obj;
	if (fName == null) {
	    if (other.fName != null)
		return false;
	} else if (!fName.equals(other.fName))
	    return false;
	if (fValue == null) {
	    if (other.fValue != null)
		return false;
	} else if (!fValue.equals(other.fValue))
	    return false;
	return true;
    }

    @Override
    public String toString() {
	return "Param [name=" + fName + ", value=" + fValue + "]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }

    @Override
    public boolean isValid() {
	return true;
    }

    @Override
    public void load(Node node) {
	fName = getNodeAttribute(node, N_NAME);
	fValue = getNodeAttribute(node, N_VALUE);
    }

}
