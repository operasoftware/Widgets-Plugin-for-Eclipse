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

package com.opera.widgets.ui.editor.labelprovider;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Display;

import com.opera.widgets.core.widget.Icon;

/**
 * Label provider for icons list.
 * 
 * @author Michal Borek
 */
public class IconsLabelProvider extends LabelProvider implements
	ITableLabelProvider {

    private static final String SRC_PATH = "src/"; //$NON-NLS-1$
    IProject fProject;

    public IconsLabelProvider(IProject project) {
	this.fProject = project;
    }

    public String getColumnText(Object obj, int index) {
	Icon icon = (Icon) obj;
	switch (index) {
	case 0:
	    return icon.getPath();
	case 1:
	    return (icon.getWidth() != null) ? icon.getWidth().toString()
		    : Messages.IconsLabelProvider_Auto;
	case 2:
	    return (icon.getHeight() != null) ? icon.getHeight().toString()
		    : Messages.IconsLabelProvider_Auto;
	}
	return null;
    }

    public Image getColumnImage(Object obj, int index) {
	if (index != 0) {
	    return null;
	}

	Icon icon = (Icon) obj;

	if (fProject == null || icon.getPath() == null) {
	    return null;
	}

	IFile iconFile = fProject.getFile(SRC_PATH + icon.getPath());
	if (iconFile.exists()) {
	    String iconPath = iconFile.getLocation().toString(); //$NON-NLS-1$
	    Image image = new Image(Display.getCurrent(), iconPath);
	    ImageData imageData = image.getImageData();
	    double scaleFactor = 20.0f / (float) Math.max(imageData.width,
		    imageData.height);
	    GC gc = new GC(Display.getCurrent());
	    try {
		gc.setInterpolation(SWT.HIGH);
	    } catch (Exception e) {

	    }
	    imageData = imageData.scaledTo(
		    (int) (imageData.width * scaleFactor),
		    (int) (imageData.height * scaleFactor));
	    return new Image(Display.getCurrent(), imageData);
	} else {
	    return null;
	}
    }
}