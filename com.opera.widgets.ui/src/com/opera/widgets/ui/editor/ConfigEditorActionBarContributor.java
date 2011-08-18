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

import java.util.Hashtable;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.part.EditorActionBarContributor;
import org.eclipse.ui.texteditor.IUpdate;

public class ConfigEditorActionBarContributor extends
		EditorActionBarContributor {

	private Hashtable<String, Action> globalActions = new Hashtable<String, Action>();

	private ConfigEditor editor;

	public void init(IActionBars bars) {
		super.init(bars);
		makeActions();
	}

	class GlobalAction extends Action implements IUpdate {
		private String id;

		public GlobalAction(String id) {
			this.id = id;
		}

		public void run() {
			editor.performGlobalAction(id);
			// updateSelectableActions(editor.getSelection());
		}

		public void update() {
			getActionBars().updateActionBars();
		}
	}

	private void addGlobalAction(String id) {
		GlobalAction action = new GlobalAction(id);
		addGlobalAction(id, action);
	}

	private void addGlobalAction(String id, Action action) {
		globalActions.put(id, action);
		getActionBars().setGlobalActionHandler(id, action);
	}

	private void makeActions() {
		addGlobalAction(ActionFactory.DELETE.getId());
		//addGlobalAction(ActionFactory.UNDO.getId());
		//addGlobalAction(ActionFactory.REDO.getId());
	}

	public void setActiveEditor(IEditorPart targetEditor) {
		if (!(targetEditor instanceof ConfigEditor)) {
			return;
		}

		editor = (ConfigEditor) targetEditor;
	}
}
