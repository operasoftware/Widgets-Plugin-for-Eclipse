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

import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.sse.ui.StructuredTextEditor;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.w3c.dom.Document;

/**
 * Source editor of config.xml file.
 * 
 * @author Michal Borek
 */
@SuppressWarnings("restriction")
public class SourceEditor extends StructuredTextEditor {

    private boolean dirty;

    public SourceEditor() {
    }

    @Override
    protected void createActions() {
	addListenerObject(new IPropertyListener() {
	    @Override
	    public void propertyChanged(Object source, int propId) {
		if (propId == SourceEditor.PROP_DIRTY) {
		    dirty = true;
		}
	    }
	});
	super.createActions();
    }

    /**
     * Gets DOM document from sourceEditor
     * 
     * @return DOM document of config.xml file
     */
    public Document getSourceDocument() {

	IDocument doc = getDocumentProvider().getDocument(getEditorInput());
	Document document = null;

	IStructuredModel model = null;
	try {
	    model = StructuredModelManager.getModelManager()
		    .getExistingModelForRead(doc);
	    if ((model != null) && (model instanceof IDOMModel)) {
		document = ((IDOMModel) model).getDocument();
	    }
	} finally {
	    model.releaseFromRead();
	}
	return document;
    }

    public boolean markedDirty() {
	return dirty;
    }

    public void removeMarkedDirty() {
	dirty = false;
    }

}
