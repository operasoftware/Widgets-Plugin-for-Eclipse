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
package com.opera.widgets.core;

import com.opera.widgets.core.widget.NodeElement;

public interface IModelChangedEvent {

	enum ChangeType {
		OBJECT_INSERT, OBJECT_REMOVE, OBJECT_CHANGE, ALL_CHANGE
	}

	IModelChangeProvider getChangeProvider();

	Object[] getChangedObjects();

	NodeElement getChangedProperty();

	Object getOldValue();

	Object getNewValue();

	ChangeType getChangeType();

	public abstract void setLanguage(String language);

	public abstract String getLanguage();
}
