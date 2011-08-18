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

package com.opera.widgets.ui;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class WidgetPerspectiveFactory implements IPerspectiveFactory {

    private static final String PERSPECTIVE_ID = "com.opera.widgets.perspective"; //$NON-NLS-1$

    @Override
    public void createInitialLayout(IPageLayout layout) {
	addViews(layout);
	layout.addPerspectiveShortcut(PERSPECTIVE_ID);
    }

    /**
     * Adds default views to perspective
     * 
     * @param layout
     */
    private void addViews(IPageLayout layout) {
	IFolderLayout topLeft = layout.createFolder("topLeft", IPageLayout.LEFT, .25f, layout.getEditorArea()); //$NON-NLS-1$
	topLeft.addView(IPageLayout.ID_PROJECT_EXPLORER);
    }
    

}
