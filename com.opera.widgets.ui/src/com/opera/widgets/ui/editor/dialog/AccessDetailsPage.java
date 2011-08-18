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

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.opera.widgets.core.widget.Access;

/**
 * Page of {@link AccessWizard}
 * 
 * @author Michal Borek
 * @see AccessWizard
 */
public class AccessDetailsPage extends WizardPage {

    private Text origin;
    private Button subdomains;
    private Access access;

    protected AccessDetailsPage(String pageName, Access access) {
	super(pageName);
	this.access = access;
	setTitle(Messages.AccessDetailsPage_Title);
    }

    @Override
    public void createControl(Composite parent) {
	Composite composite = new Composite(parent, SWT.NONE);
	GridLayout layout = new GridLayout(2, false);
	composite.setLayout(layout);
	setControl(composite);
	GridData gd = new GridData(GridData.FILL_HORIZONTAL);
	new Label(composite, SWT.NONE).setText(Messages.AccessDetailsPage_OriginLabel);
	origin = new Text(composite, SWT.NONE);
	origin.setLayoutData(gd);

	new Label(composite, SWT.NONE).setText(Messages.AccessDetailsPage_SubdomainsLabel);
	subdomains = new Button(composite, SWT.CHECK);

	Label separator = new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL);
	gd = new GridData(GridData.FILL_HORIZONTAL);
	gd.horizontalSpan = 2;
	separator.setLayoutData(gd);

	if (access.getOrigin() != null) {
	    origin.setText(access.getOrigin());
	}
	subdomains.setSelection(access.isSubdomains());

    }

    public Text getOriginField() {
	return origin;
    }

    public Button getSubdomainsField() {
	return subdomains;
    }


}