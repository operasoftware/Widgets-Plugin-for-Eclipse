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

package com.opera.widgets.core.launching;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate;

public class WidgetLaunchConfigurationDelegate implements
	ILaunchConfigurationDelegate {

    @Override
    public void launch(ILaunchConfiguration configuration, String mode,
	    ILaunch launch, IProgressMonitor monitor) throws CoreException {
	System.out.println(launch.getLaunchConfiguration().getAttribute("emulator_path", "usr/bin"));

    }

}
