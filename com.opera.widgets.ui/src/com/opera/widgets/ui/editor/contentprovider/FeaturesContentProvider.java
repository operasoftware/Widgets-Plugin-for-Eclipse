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

package com.opera.widgets.ui.editor.contentprovider;

import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import com.opera.widgets.core.widget.Feature;

/**
 * Content provider for features list.
 * 
 * @author Michal Borek <mborek@opera.com>
 */
public class FeaturesContentProvider implements IStructuredContentProvider {

    @Override
    public void dispose() {

    }

    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

    }

    @SuppressWarnings("unchecked")
    @Override
    public Object[] getElements(Object inputElement) {
	return ((List<Feature>) inputElement).toArray();
    }
    
    
}