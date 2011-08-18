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

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.forms.IManagedForm;

import com.opera.widgets.ui.IHelpContextIds;

/**
 * Icons tab in Config editor. Contains list of tabs and provides functionality
 * to manage icons associated with widget.
 * 
 * @author Michal Borek
 */
public class IconsPage extends AbstractFormPage {

    public static final String PAGE_ID = "icons"; //$NON-NLS-1$
    TableViewer iconViewer;
    IconPropertiesBlock block;

    public IconsPage(ConfigEditor editor) {
	super(editor, PAGE_ID, Messages.OverviewPage_TitleIcons);
	block = new IconPropertiesBlock(this);
    }

    @Override
    protected void createFormContent(IManagedForm managedForm) {
	super.createFormContent(managedForm);
	block.createContent(managedForm);
    }

    @Override
    public void updateEditor() {
	super.updateEditor();
	block.getViewer().setInput(getWidget().getIcons());
    }

    @Override
    public Object getContent() {
	return getWidget().getIcons();
    }

    @Override
    public String getHelpResource() {
	return IHelpContextIds.CONFIG_ICONS;
    }

    @Override
    public boolean performGlobalAction(String id) {

	if (id.equals(ActionFactory.DELETE.getId())) {
	    block.handleDelete();
	    return true;
	}
	return false;
    }

}
