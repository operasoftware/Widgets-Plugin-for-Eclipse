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

import org.w3c.dom.Node;

public class JilAccess extends AbstractWidgetObject {

    private static final String N_NETWORK = "network"; //$NON-NLS-1$
    private static final String N_LOCALFS = "localfs"; //$NON-NLS-1$

    private boolean fNetwork, fLocalFs;

    @Override
    public boolean isValid() {
	return true;
    }

    @Override
    public void load(Node node) {
	String networkString = getNodeAttribute(node, N_NETWORK);
	if (networkString != null) {
	    fNetwork = Boolean.parseBoolean(networkString);
	}
	String localFsString = getNodeAttribute(node, N_LOCALFS);
	if (localFsString != null) {
	    fLocalFs = Boolean.parseBoolean(localFsString);
	}
    }

    public boolean isNetwork() {
	return fNetwork;
    }
    
    public boolean isLocalFs() {
	return fLocalFs;
    }

    public void setNetwork(Boolean network) {
	Boolean oldValue = fNetwork;
	this.fNetwork = network;
	firePropertyChanged(NodeElement.JIL_ACCESS_NETWORK,
		oldValue.toString(), network.toString());

    }

    public void setLocalFs(Boolean localFs) {
	Boolean oldValue = fLocalFs;
	this.fLocalFs = localFs;
	firePropertyChanged(NodeElement.JIL_ACCESS_LOCALFS,
		oldValue.toString(), localFs.toString());
	
    }

    public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + (fLocalFs ? 1231 : 1237);
	result = prime * result + (fNetwork ? 1231 : 1237);
	return result;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj)
	    return true;
	if (obj == null)
	    return false;
	if (getClass() != obj.getClass())
	    return false;
	JilAccess other = (JilAccess) obj;
	if (fLocalFs != other.fLocalFs)
	    return false;
	if (fNetwork != other.fNetwork)
	    return false;
	return true;
    }

    @Override
    public String toString() {
	return "JilAccess [fNetwork=" + fNetwork + "]"; //$NON-NLS-1$ //$NON-NLS-2$
    }

}
