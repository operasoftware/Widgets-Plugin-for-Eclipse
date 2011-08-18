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

package com.opera.widgets.ui.editor;

import org.eclipse.jface.action.IAction;

import com.opera.widgets.core.IModelChangeProvider;

public interface IModelUndoManager {

	public void connect(IModelChangeProvider provider);

	public void disconnect(IModelChangeProvider provider);

	public boolean isUndoable();

	public boolean isRedoable();

	public void undo();

	public void redo();

	public void setUndoLevelLimit(int limit);

	public void setIgnoreChanges(boolean ignore);

	public void setActions(IAction undoAction, IAction redoAction);
}
