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

public class LinkValidator extends AbstractControlValidation {


	private static final Pattern pattern;

	static {
		pattern = Pattern
				.compile("(ftp|http|https):\\/\\/(\\w+:{0,1}\\w*@)?(\\S+)(:[0-9]+)?(\\/|\\/([\\w#!:.?+=&%@!\\-\\/]))?"); //$NON-NLS-1$
	}

	public LinkValidator(IManagedForm managedForm, Text control) {
		super(managedForm, control);
	}


	@Override
	protected boolean validateControl() {
		String string = ((Text) getControl()).getText();
		Matcher m = pattern.matcher(string);
		return string.isEmpty() || m.find();
	}




	@Override
	protected String getMessage() {
		return Messages.LinkValidator_InvalidLink;
	}
}
