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

import com.opera.widgets.core.widget.Access;

/**
 * Wizard used to add or modify widget's access elements.
 * 
 * @author Michal Borek
 */
public class AccessWizard extends Wizard {

    protected AccessDetailsPage accessDetailsPage;
    protected Access access;

    public AccessWizard() {
	access = new Access();
    }

	public AccessWizard(Access access) {
	this.access = access;
    }

    @Override
    public boolean performFinish() {
	access.setOrigin(accessDetailsPage.getOriginField().getText());
	access.setSubdomains(accessDetailsPage.getSubdomainsField().getSelection());
	return true;
    }

    @Override
    public void addPages() {
	accessDetailsPage = new AccessDetailsPage("accessDetails", access); //$NON-NLS-1$
	addPage(accessDetailsPage);
    }

    public Access getAccessElement() {
	return access;
    }

}
