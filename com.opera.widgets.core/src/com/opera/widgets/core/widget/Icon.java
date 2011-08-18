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

public class Icon extends AbstractWidgetObject {

	// XML node names
	private static final String N_PATH = "src"; //$NON-NLS-1$
	private static final String N_WIDTH = "width"; //$NON-NLS-1$
	private static final String N_HEIGHT = "height"; //$NON-NLS-1$

	private String fPath;
	private Integer fWidth;
	private Integer fHeight;

	public String getPath() {
		return fPath;
	}

	public void setPath(String path) {
		this.fPath = path;
	}

	public Integer getWidth() {
		return fWidth;
	}

	public void setWidth(Integer width) {
		this.fWidth = width;
	}

	public Integer getHeight() {
		return fHeight;
	}

	public void setHeight(Integer height) {
		this.fHeight = height;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fHeight == null) ? 0 : fHeight.hashCode());
		result = prime * result + ((fPath == null) ? 0 : fPath.hashCode());
		result = prime * result + ((fWidth == null) ? 0 : fWidth.hashCode());
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
		Icon other = (Icon) obj;
		if (fHeight == null) {
			if (other.fHeight != null)
				return false;
		} else if (!fHeight.equals(other.fHeight))
			return false;
		if (fPath == null) {
			if (other.fPath != null)
				return false;
		} else if (!fPath.equals(other.fPath))
			return false;
		if (fWidth == null) {
			if (other.fWidth != null)
				return false;
		} else if (!fWidth.equals(other.fWidth))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Icon [path=" + fPath + ", width=" + fWidth + ", height=" + fHeight + "]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
	}

	@Override
	public boolean isValid() {
		return true;
	}

	@Override
	public void load(Node node) {
		fPath = getNodeAttribute(node, N_PATH);
		try {
			fWidth = Integer.valueOf(getNodeAttribute(node, N_WIDTH));
		} catch (NumberFormatException e) {
			fWidth = null;
		}
		try {
			fHeight = Integer.valueOf(getNodeAttribute(node, N_HEIGHT));
		} catch (NumberFormatException e) {
			fHeight = null;
		}
	}
}
