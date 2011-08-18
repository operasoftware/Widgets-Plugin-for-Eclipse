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

package com.opera.widgets.ui.editor.labelprovider;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import com.opera.widgets.core.widget.Feature;

/**
 * Label provider for security/access children list.
 * 
 * @author Michal Borek <mborek@opera.com>
 */
public class FeatureLabelProvider extends LabelProvider implements ITableLabelProvider {

    @Override
    public Image getColumnImage(Object element, int columnIndex) {
	return null;
    }

    @Override
    public String getColumnText(Object element, int columnIndex) {
	Feature f = (Feature) element;
	switch (columnIndex) {
	case 0:
	    return f.getName();
	case 1:
	    return Boolean.toString(f.isRequired());
	}
	return null;
    }
}