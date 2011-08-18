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

package com.opera.widgets.core.widget;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.opera.widgets.core.IModelChangeProvider;
import com.opera.widgets.core.IModelChangedEvent;
import com.opera.widgets.core.IModelChangedListener;

public class WidgetModel implements IModelChangeProvider {

	private List<IModelChangedListener> fListeners;
	private Widget fWidget;

	public WidgetModel(Widget widget) {
		fWidget = widget;
		fListeners = Collections
				.synchronizedList(new ArrayList<IModelChangedListener>());
	}

	@Override
	public void addModelChangedListener(IModelChangedListener listener) {
		fListeners.add(listener);
	}

	@Override
	public void fireModelChanged(IModelChangedEvent event) {
		for (IModelChangedListener listener : fListeners) {
			listener.modelChanged(event);
		}
	}

	public Widget getWidget() {
		return fWidget;
	}

	@Override
	public void removeModelChangedListener(IModelChangedListener listener) {
		fListeners.remove(listener);
	}
}
