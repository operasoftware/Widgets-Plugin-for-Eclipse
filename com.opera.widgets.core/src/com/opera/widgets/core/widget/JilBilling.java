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

public class JilBilling extends AbstractWidgetObject {

    private static final String N_REQUIRED = "required"; //$NON-NLS-1$

    private boolean fRequired;

    @Override
    public boolean isValid() {
	return true;
    }

    @Override
    public void load(Node node) {
	String requiredString = getNodeAttribute(node, N_REQUIRED);
	if (requiredString != null) {
	    fRequired = Boolean.parseBoolean(requiredString);
	}
    }

    public boolean isRequired() {
	return fRequired;
    }

    public void setRequired(Boolean required) {
	Boolean oldValue = fRequired;
	fRequired = required;
	firePropertyChanged(NodeElement.JIL_BILLING_REQUIRED,
		oldValue.toString(), required.toString());
    }

    @Override
    public int hashCode() {
	final int prime = 31;
	int result = 1;
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
	JilBilling other = (JilBilling) obj;
	if (fRequired != other.fRequired)
	    return false;
	return true;
    }

    @Override
    public String toString() {
	return "JilBilling [fRequired=" + fRequired + "]"; //$NON-NLS-1$ //$NON-NLS-2$
    }

}
