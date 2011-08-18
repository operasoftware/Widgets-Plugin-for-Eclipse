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

package com.opera.widgets.ui.editor.toolbar;

import org.eclipse.jface.action.ControlContribution;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import com.opera.widgets.ui.editor.AbstractFormPage;

/**
 * Language Combo that can be added to a Section Toolbar.
 */
public class LabelControlContribution extends ControlContribution {

    private String fValue;

    public LabelControlContribution(String value, AbstractFormPage formPage) {
	super("lang_label"); //$NON-NLS-1$
	fValue = value;
    }

    @Override
    protected Control createControl(Composite parent) {
	Label label = new Label(parent, SWT.NONE);
	label.setText(fValue);
	return label;
    }

}