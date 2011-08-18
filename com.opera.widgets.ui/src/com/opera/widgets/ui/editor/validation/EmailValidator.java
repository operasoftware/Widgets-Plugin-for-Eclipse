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

package com.opera.widgets.ui.editor.validation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IManagedForm;

public class EmailValidator extends AbstractControlValidation {

	private static final Pattern pattern;

	static {
		pattern = Pattern.compile("^[\\w\\.=-]+@[\\w\\.-]+\\.[\\w]{2,4}$"); //$NON-NLS-1$
	}

	public EmailValidator(IManagedForm managedForm, Text control) {
		super(managedForm, control);
	}

	@Override
	protected boolean validateControl() {
		String string = ((Text) getControl()).getText();
		Matcher m = pattern.matcher(string);
		return m.find() || string.isEmpty();
	}

	@Override
	protected String getMessage() {
		return Messages.EmailValidator_InvalidEmail;
	}
}
