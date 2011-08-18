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

public class Preference extends AbstractWidgetObject {

    // XML node names
    private static final String N_NAME = "name"; //$NON-NLS-1$
    private static final String N_READONLY = "readonly"; //$NON-NLS-1$
    private static final String N_VALUE = "value"; //$NON-NLS-1$

    private String fName;
    private String fValue;
    private boolean fReadonly;

    public Preference(String name, String value) {
	this(name, value, false);
    }

    public Preference(String name, String value, boolean readonly) {
	this.fName = name;
	this.fValue = value;
	this.fReadonly = readonly;
    }

    public Preference() {
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

    public boolean isReadonly() {
	return fReadonly;
    }

    public void setReadonly(boolean readonly) {
	this.fReadonly = readonly;
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((fName == null) ? 0 : fName.hashCode());
	result = prime * result + (fReadonly ? 1231 : 1237);
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
	Preference other = (Preference) obj;
	if (fName == null) {
	    if (other.fName != null)
		return false;
	} else if (!fName.equals(other.fName))
	    return false;
	if (fReadonly != other.fReadonly)
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
	return "Preference [fName=" + fName + ", fValue=" + fValue //$NON-NLS-1$ //$NON-NLS-2$
		+ ", fReadonly=" + fReadonly + "]"; //$NON-NLS-1$ //$NON-NLS-2$
    }

    @Override
    public boolean isValid() {
	return true;
    }

    @Override
    public void load(Node node) {
	fName = getNodeAttribute(node, N_NAME);
	fReadonly = Boolean.parseBoolean(getNodeAttribute(node, N_READONLY));
	fValue = getNodeAttribute(node, N_VALUE);
    }

}
