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
 * POJO for access node from config.xml
 * 
 */
public class Access extends AbstractWidgetObject {

	// XML node names
	private static final String N_ORIGIN = "origin"; //$NON-NLS-1$
	private static final String N_SUBDOMAINS = "subdomains"; //$NON-NLS-1$

	private String fOrigin;
	private boolean fSubdomains;

	@Override
	public void load(Node node) {
		fOrigin = getNodeAttribute(node, N_ORIGIN);
		fSubdomains = Boolean.parseBoolean(getNodeAttribute(node, N_SUBDOMAINS));
	}

	public String getOrigin() {
		return fOrigin;
	}

	public void setOrigin(String origin) {
		this.fOrigin = origin;
	}

	public boolean isSubdomains() {
		return fSubdomains;
	}

	public void setSubdomains(boolean subdomains) {
		this.fSubdomains = subdomains;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fOrigin == null) ? 0 : fOrigin.hashCode());
		result = prime * result + (fSubdomains ? 1231 : 1237);
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
		Access other = (Access) obj;
		if (fOrigin == null) {
			if (other.fOrigin != null)
				return false;
		} else if (!fOrigin.equals(other.fOrigin))
			return false;
		if (fSubdomains != other.fSubdomains)
			return false;
		return true;
	}

	@Override
	public boolean isValid() {
		return true;
	}
}
