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

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
// 29006
import com.opera.widgets.core.IModelChangeProvider;
import com.opera.widgets.core.IModelChangedEvent;
import com.opera.widgets.core.ModelChangedEvent;

public abstract class AbstractWidgetObject implements IWidgetObject {

    private WidgetModel model;

    @Override
    public WidgetModel getModel() {
	return model;
    }

    @Override
    public void setModel(WidgetModel model) {
	this.model = model;
    }

    protected String getNodeAttribute(Node node, String name) {
	NamedNodeMap nodeMap = node.getAttributes();
	if (nodeMap == null) {
	    return null;
	}
	Node attribute = nodeMap.getNamedItem(name);
	if (attribute != null){
	    return attribute.getNodeValue();
	}
	return null;
    }

    protected void firePropertyChanged(NodeElement property, String oldValue,
	    String newValue, String language) {
	if (language != null && !language.equals("default")) { //$NON-NLS-1$
	    fireModelChanged(new ModelChangedEvent(getModel(), null, property,
		    oldValue, newValue, language));
	} else {
	    firePropertyChanged(property, oldValue, newValue);
	}
    }

    protected void firePropertyChanged(NodeElement property, String oldValue,
	    String newValue) {
	fireModelChanged(new ModelChangedEvent(getModel(), null, property,
		oldValue, newValue));
    }

    protected void fireStructureChanged(AbstractWidgetObject child,
	    IModelChangedEvent.ChangeType changeType) {
	if (model instanceof IModelChangeProvider) {
	    IModelChangedEvent e = new ModelChangedEvent(
		    (IModelChangeProvider) model, changeType,
		    new Object[] { child }, null);
	    fireModelChanged(e);
	}
    }

    protected void fireModelChanged(IModelChangedEvent e) {
	WidgetModel model = getModel();
	if (model instanceof IModelChangeProvider) {
	    IModelChangeProvider provider = (IModelChangeProvider) model;
	    provider.fireModelChanged(e);
	}
    }

    public boolean isModelSet() {
	return model != null;
    }
}
