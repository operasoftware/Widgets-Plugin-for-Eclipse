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

import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IManagedForm;

public class PositiveIntegerValidator extends AbstractControlValidation {

	public PositiveIntegerValidator(IManagedForm managedForm, Text control) {
		super(managedForm, control);
	}

	@Override
	protected boolean validateControl() {
		String string = ((Text) getControl()).getText();
		char[] chars = new char[string.length()];
		string.getChars(0, chars.length, chars, 0);
		for (int i = 0; i < chars.length; i++) {
			if (!Character.isDigit(chars[i])) {
				return false;
			}
		}

		return true;
	}

	@Override
	protected String getMessage() {
		return Messages.PositiveIntegerValidator_InvalidNumber;
	}
}
