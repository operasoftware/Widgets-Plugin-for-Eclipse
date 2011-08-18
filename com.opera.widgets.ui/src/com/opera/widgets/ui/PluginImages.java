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

package com.opera.widgets.ui;

import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;

public class PluginImages {

    private static String ICONS_PATH = "icons/"; //$NON-NLS-1$

    public static final ImageDescriptor HELP_ICON = createIcon("help.gif"); //$NON-NLS-1$
    public static final ImageDescriptor WIDGET_64PX = createIcon("64px.png"); //$NON-NLS-1$

    private static ImageDescriptor createIcon(String iconName) {
	String path = ICONS_PATH + iconName;
	URL url = FileLocator.find(WidgetsActivator.getDefault().getBundle(),
		new Path(path), null);
	return ImageDescriptor.createFromURL(url);
    }
}
