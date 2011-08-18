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

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IManagedForm;

public abstract class AbstractControlValidation {

	private IManagedForm managedForm;

	// prefix to each message (e.g.: "Error: ");
	private String messagePrefix;

	private String F_DEFAULT_MESSAGE_KEY = "default"; //$NON-NLS-1$
	private Control control;
	private ModifyListener modifyListener;
	private FocusListener focusListener;

	public AbstractControlValidation(IManagedForm managedForm, Control control) {
		this.managedForm = managedForm;
		this.control = control;
		initialize();
	}

	private boolean valid;
	private boolean enabled = true;

	public boolean validate() {
		if (enabled == false) {
			return valid;
		}
		valid = validateControl();
		if (valid) {
			managedForm.getMessageManager().removeMessages(control);
		} else {
			addMessage(getMessage(), IMessageProvider.ERROR);
		}
		return valid;
	}

	public void addMessage(Object key, String messageText, int messageType) {
		if (messagePrefix != null) {
			messageText = messagePrefix + ' ' + messageText;
		}
		managedForm.getMessageManager().addMessage(key, messageText, null,
				messageType, control);
	}

	public void addMessage(String messageText, int messageType) {
		// Add a prefix, if one was specified
		if (messagePrefix != null) {
			messageText = messagePrefix + ' ' + messageText;
		}
		// Delegate to message manager
		managedForm.getMessageManager().addMessage(F_DEFAULT_MESSAGE_KEY,
				messageText, null, messageType, control);
	}

	/**
	 * Validates specific control element
	 * 
	 * @return
	 */
	protected abstract boolean validateControl();

	protected abstract String getMessage();

	public static int getMessageType(IStatus status) {
		int severity = status.getSeverity();
		if (severity == IStatus.OK) {
			return IMessageProvider.NONE;
		} else if (severity == IStatus.ERROR) {
			return IMessageProvider.ERROR;
		} else if (severity == IStatus.WARNING) {
			return IMessageProvider.WARNING;
		} else if (severity == IStatus.INFO) {
			return IMessageProvider.INFORMATION;
		}
		return IMessageProvider.NONE;
	}

	public Control getControl() {
		return control;
	}

	public boolean isValid() {
		return valid;
	}

	private void createListeners() {
		modifyListener = new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				if (!((Text) getControl()).isFocusControl()) {
					validate();
				}
			}
		};

		focusListener = new FocusListener() {

			@Override
			public void focusLost(FocusEvent e) {
				validate();

			}

			@Override
			public void focusGained(FocusEvent e) {
				validate();
			}
		};

	}

	protected void initialize() {
		createListeners();
		addListeners();
	}

	private void addListeners() {
		Text textField = (Text) getControl();
		textField.addModifyListener(modifyListener);
		textField.addFocusListener(focusListener);

	}
}
