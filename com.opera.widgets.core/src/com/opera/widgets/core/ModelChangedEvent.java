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

public class ModelChangedEvent implements IModelChangedEvent {

    private ChangeType changeType;
    private IModelChangeProvider changeProvider;
    private Object[] changedObjects;
    private Object oldValue, newValue;
    private String language;
    private NodeElement changedProperty;

    public ModelChangedEvent(IModelChangeProvider provider, ChangeType type,
	    Object[] objects, NodeElement changedProperty) {
	this.changeType = type;
	this.changeProvider = provider;
	this.changedObjects = objects;
	this.changedProperty = changedProperty;
    }

    public ModelChangedEvent(IModelChangeProvider provider, Object object,
	    NodeElement changedProperty, Object oldValue, Object newValue) {
	this.changeType = ChangeType.OBJECT_CHANGE;
	this.changeProvider = provider;
	this.changedObjects = new Object[] { object };
	this.changedProperty = changedProperty;
	this.oldValue = oldValue;
	this.newValue = newValue;
    }

    public ModelChangedEvent(IModelChangeProvider provider, Object object,
	    NodeElement changedProperty, Object oldValue, Object newValue,
	    String language) {
	this(provider, object, changedProperty, oldValue, newValue);
	this.language = language;
    }

    @Override
    public IModelChangeProvider getChangeProvider() {
	return changeProvider;
    }

    @Override
    public Object[] getChangedObjects() {
	return changedObjects == null ? new Object[0] : changedObjects;
    }

    @Override
    public NodeElement getChangedProperty() {
	return changedProperty;
    }

    @Override
    public Object getOldValue() {
	return oldValue;
    }

    @Override
    public Object getNewValue() {
	return newValue;
    }

    @Override
    public ChangeType getChangeType() {
	return changeType;
    }

    @Override
    public String getLanguage() {
	return language;
    }

    @Override
    public void setLanguage(String language) {
	this.language = language;
    }

}
