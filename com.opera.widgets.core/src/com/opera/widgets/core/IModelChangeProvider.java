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

public interface IModelChangeProvider {

    void addModelChangedListener(IModelChangedListener listener);

    void removeModelChangedListener(IModelChangedListener listener);

    void fireModelChanged(IModelChangedEvent event);
}
