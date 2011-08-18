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

package com.opera.widgets.core.exception;

public class InvalidWidgetPackageException extends Exception {

    private static final long serialVersionUID = 1L;

    public InvalidWidgetPackageException() {
	super();
    }

    public InvalidWidgetPackageException(String message, Throwable cause) {
	super(message, cause);
    }

    public InvalidWidgetPackageException(String message) {
	super(message);
    }

    public InvalidWidgetPackageException(Throwable cause) {
	super(cause);
    }
}
