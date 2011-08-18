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

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.opera.widgets.core.exception.InvalidConfigFileException;

public class JilWidget extends Widget {

    private JilBilling fJilBilling;
    private JilAccess fJilAccess;

    @Override
    public void load(Node node) throws InvalidConfigFileException {
	super.load(node);

	fJilBilling = new JilBilling();
	fJilBilling.setModel(getModel());
	NodeList jilBillingNodes = ((Element) node).getElementsByTagNameNS(
		NodeElement.WidgetNamespace.JIL.getValue(), N_BILLING);
	if (jilBillingNodes.getLength() > 0) {
	    fJilBilling.load(jilBillingNodes.item(0));
	}

	fJilAccess = new JilAccess();
	fJilAccess.setModel(getModel());
	NodeList jilAccessNodes = ((Element) node).getElementsByTagNameNS(
		NodeElement.WidgetNamespace.JIL.getValue(), N_ACCESS);
	if (jilAccessNodes.getLength() > 0) {
	    fJilAccess.load(jilAccessNodes.item(0));
	}
    }

    public JilBilling getJilBilling() {
	return fJilBilling;
    }

    public boolean getJilBillingRequired() {
	JilBilling jilBilling = getJilBilling();
	return jilBilling != null ? jilBilling.isRequired() : false;
    }

    public void setJilBillingRequired(boolean value) {

	JilBilling content = getJilBilling();
	if (content == null) {
	    content = new JilBilling();
	    fJilBilling = content;
	    content.setModel(getModel());
	}
	content.setRequired(value);
    }

    public JilAccess getJilAccess() {
	return fJilAccess;
    }

    public boolean getJilAccessNetwork() {
	JilAccess jilAccess = getJilAccess();
	return jilAccess != null ? jilAccess.isNetwork() : false;
    }

    public boolean getJilAccessLocalFs() {
	JilAccess jilAccess = getJilAccess();
	return jilAccess != null ? jilAccess.isLocalFs() : false;
    }

    public void setJilAccessNetwork(boolean value) {

	JilAccess jilAccess = getJilAccess();
	if (jilAccess == null) {
	    jilAccess = new JilAccess();
	    fJilAccess = jilAccess;
	    jilAccess.setModel(getModel());
	}
	jilAccess.setNetwork(value);
    }

    public void setJilAccessLocalFs(boolean value) {

	JilAccess jilAccess = getJilAccess();
	if (jilAccess == null) {
	    jilAccess = new JilAccess();
	    fJilAccess = jilAccess;
	    jilAccess.setModel(getModel());
	}
	jilAccess.setLocalFs(value);
    }

}