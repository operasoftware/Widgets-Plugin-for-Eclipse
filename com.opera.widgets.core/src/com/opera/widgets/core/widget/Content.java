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


public class Content extends AbstractWidgetObject {

	// XML node names
	private static final String N_SRC = "src"; //$NON-NLS-1$
	private static final String N_TYPE = "type"; //$NON-NLS-1$
	private static final String N_ENCODING = "encoding"; //$NON-NLS-1$

	private String fSrc;
	private String fType;
	private String fEncoding;

	public String getSrc() {
		return fSrc;
	}

	public void setSrc(String src) {
		String oldValue = this.fSrc;
		this.fSrc = src;
		firePropertyChanged(NodeElement.CONTENT_SRC, oldValue, src);
	}

	public String getType() {
		return fType;
	}

	public void setType(String type) {
		String oldValue = this.fType;
		this.fType = type;
		firePropertyChanged(NodeElement.CONTENT_TYPE, oldValue, type);
	}

	public String getEncoding() {
		return fEncoding;
	}

	public void setEncoding(String encoding) {
		String oldValue = this.fEncoding;
		this.fEncoding = encoding;
		firePropertyChanged(NodeElement.CONTENT_ENCODING, oldValue, encoding);
	}

	@Override
	public boolean isValid() {
		return true;
	}

	@Override
	public void load(Node node) {
		fSrc = getNodeAttribute(node, N_SRC);
		fType = getNodeAttribute(node, N_TYPE);
		fEncoding = getNodeAttribute(node, N_ENCODING);

	}

}
