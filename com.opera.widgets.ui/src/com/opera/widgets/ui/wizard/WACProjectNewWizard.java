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

package com.opera.widgets.ui.wizard;

import com.opera.widgets.core.natures.WACWidgetNature;

public class WACProjectNewWizard extends ProjectNewWizard {

    private static final String WAC_WIDGETS_PROJECT = ProjectNewWizardMessages.ProjectNewWizard_WACProjectName;

    public WACProjectNewWizard() {
	super(WACWidgetNature.NATURE_ID);
	setWindowTitle(WAC_WIDGETS_PROJECT);
    }
}
