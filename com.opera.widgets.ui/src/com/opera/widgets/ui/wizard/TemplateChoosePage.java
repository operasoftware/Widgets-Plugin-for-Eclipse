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

package com.opera.widgets.ui.wizard;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.List;

public class TemplateChoosePage extends WizardPage {

    private static Map<String, String> fTemplateMap;
    private List fList;
    private Button fTypeButton;

    static {
	fTemplateMap = new HashMap<String, String>();
	fTemplateMap.put("Accelereometer info", "Accelerometer.wgt");
	fTemplateMap.put("Audio Player", "AudioPlayer.wgt");
	fTemplateMap.put("Camera", "Camera.wgt");
	fTemplateMap.put("Position Info", "PositionInfo.wgt");
	fTemplateMap.put("Messaging", "Messaging.wgt");
	fTemplateMap.put("PIM and Address Book", "PIM.wgt");
    }

    protected TemplateChoosePage(String pageName) {
	super(pageName);
	setTitle("Choose a template");
    }

    @Override
    public void createControl(Composite parent) {
	Composite container = new Composite(parent, SWT.NULL);

	fTypeButton = new Button(container, SWT.CHECK);
	fTypeButton.setText("Create a widget using one of the templates");
	fTypeButton.addSelectionListener(new SelectionAdapter() {
	    @Override
	    public void widgetSelected(SelectionEvent e) {
		fList.setEnabled(fTypeButton.getSelection());
	    }
	});
	GridLayout layout = new GridLayout();
	container.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL
		| GridData.VERTICAL_ALIGN_FILL));
	container.setLayout(layout);
	fList = new List(container, SWT.BORDER);
	fList.setItems(fTemplateMap.keySet().toArray(new String[0]));
	GridData gd = new GridData(GridData.FILL_BOTH);
	gd.grabExcessVerticalSpace = true;
	fList.setLayoutData(gd);
	fList.setEnabled(fTypeButton.getSelection());
	setControl(container);
    }

    public boolean useTemplate() {
	return fTypeButton.getSelection();
    }
    
    public String getTemplateFile(){
	String[] selection = fList.getSelection();
	return fTemplateMap.get(selection[0]);
    }
}
