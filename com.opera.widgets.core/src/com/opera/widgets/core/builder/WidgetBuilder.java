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

package com.opera.widgets.core.builder;

import java.util.Map;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Class responsible for building widgets.
 * 
 * 
 * @author Michal Borek <mborek@opera.com>
 * 
 *         TODO this class is at early stage of development. It has basic
 *         functionality to show building of widget but it would be optimized.
 * 
 */
public class WidgetBuilder extends IncrementalProjectBuilder {

    public static final String BUILDER_ID = "com.opera.widgets.builder.widgetBuilder"; //$NON-NLS-1$

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.core.internal.events.InternalBuilder#build(int,
     * java.util.Map, org.eclipse.core.runtime.IProgressMonitor)
     */
    @SuppressWarnings("rawtypes")
    protected IProject[] build(int kind, Map args, IProgressMonitor monitor)
	    throws CoreException {
	IFolder buildFolder = getProject().getFolder("build"); //$NON-NLS-1$

	if (!buildFolder.exists()) {
	    buildFolder.create(false, true, null);
	}
	if (kind == FULL_BUILD) {
	    fullBuild(monitor);
	} else {
	    IResourceDelta delta = getDelta(getProject());
	    if (delta == null) {
		fullBuild(monitor);
	    } else {
		incrementalBuild(delta, monitor);
	    }
	}
	return null;
    }

    protected void fullBuild(final IProgressMonitor monitor)
	    throws CoreException {
	try {
	    getProject().accept(new WidgetResourceVisitor());
	} catch (CoreException e) {
	}
    }

    protected void incrementalBuild(IResourceDelta delta,
	    IProgressMonitor monitor) throws CoreException {
	// the visitor does the work.
	delta.accept(new WidgetDeltaVisitor());
    }

    /**
     * TODO perform clean action
     */
    @Override
    protected void clean(IProgressMonitor monitor) throws CoreException {
	super.clean(monitor);
	// System.err.println("cleaning");
    }

    class WidgetDeltaVisitor implements IResourceDeltaVisitor {
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.resources.IResourceDeltaVisitor#visit(org.eclipse
	 * .core.resources.IResourceDelta)
	 */
	public boolean visit(IResourceDelta delta) throws CoreException {

	    IResource resource = delta.getResource();
	    // System.err.println("res: " + resource.getName());
	    switch (delta.getKind()) {
	    case IResourceDelta.MOVED_FROM:
		System.out.println("Moved from: " + delta.getMovedToPath());
		break;
	    case IResourceDelta.REMOVED:
		break;
	    case IResourceDelta.CHANGED:
		switch (resource.getType()) {
		case IResource.FILE:
		    // System.err.println("file");
		    break;
		}
		break;
	    }
	    // return true to continue visiting children.
	    return true;
	}
    }

    class WidgetResourceVisitor implements IResourceVisitor {
	public boolean visit(IResource resource) {
	    // checkXML(resource);
	    // return true to continue visiting children.
	    return true;
	}
    }

}
