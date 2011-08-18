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

package com.opera.widgets.ui.wizard.imports;

import org.eclipse.ui.IExportWizard;

import com.opera.widgets.core.natures.WACWidgetNature;
import com.opera.widgets.ui.PluginImages;

/**
 * Wizard for widget package import
 * 
 * @author Michal Borek
 */
public class WACWidgetImportWizard extends WidgetImportWizard implements
	IExportWizard {

    public WACWidgetImportWizard() {
	super(WACWidgetNature.NATURE_ID);
	setDefaultPageImageDescriptor(PluginImages.WIDGET_64PX);
    }
}
