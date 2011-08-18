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


public class Author extends AbstractWidgetObject {

	// XML node names
	private static final String N_EMAIL = "email"; //$NON-NLS-1$
	private static final String N_HREF = "href"; //$NON-NLS-1$

	private String fName;
	private String fEmail;
	private String fHref;

	public String getName() {
		return fName;
	}

	public void setName(String name) {
		String oldValue = this.fName;
		this.fName = name;
		firePropertyChanged(NodeElement.AUTHOR, oldValue, name);
	}

	public String getEmail() {
		return fEmail;
	}

	public void setEmail(String email) {
		String oldValue = this.fEmail;
		this.fEmail = email;
		firePropertyChanged(NodeElement.AUTHOR_EMAIL, oldValue, email);
	}

	public String getHref() {
		return fHref;
	}

	public void setHref(String href) {
		String oldValue = this.fHref;
		this.fHref = href;
		firePropertyChanged(NodeElement.AUTHOR_HREF, oldValue, href);
	}

	@Override
	public boolean isValid() {
		return true;
	}

	@Override
	public void load(Node node) {
		Node child = node.getFirstChild();
		fName = child != null ? child.getNodeValue() : null;
		fEmail = getNodeAttribute(node, N_EMAIL);
		fHref = getNodeAttribute(node, N_HREF);
	}

}
