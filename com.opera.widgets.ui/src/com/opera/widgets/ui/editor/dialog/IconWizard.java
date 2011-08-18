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

package com.opera.widgets.ui.editor.dialog;

import org.eclipse.jface.wizard.Wizard;

import com.opera.widgets.core.widget.Icon;

/**
 * Wizard for adding icons to config.xml file
 * 
 * @author Michal Borek
 */
public class IconWizard extends Wizard {

    protected IconDetailsPage chooseIconPage;
    Icon icon;

    @Override
    public void addPages() {
	chooseIconPage = new IconDetailsPage("chooseIcon", icon); //$NON-NLS-1$
	addPage(chooseIconPage);
    }

    public IconWizard() {
	icon = new Icon();
    }

    public IconWizard(Icon icon) {
	this.icon = icon;
    }

    @Override
    public boolean performFinish() {
	icon.setPath(chooseIconPage.getPath().getText());
	if (!chooseIconPage.getWidth().getText().isEmpty()) {
	    icon.setWidth(Integer.parseInt(chooseIconPage.getWidth().getText()));
	} else {
	    icon.setWidth(null);
	}
	if (!chooseIconPage.getHeight().getText().isEmpty()) {
	    icon.setHeight(Integer.parseInt(chooseIconPage.getHeight().getText()));
	} else {
	    icon.setHeight(null);
	}
	return true;
    }

    public Icon getIcon() {
	return icon;
    }
}
