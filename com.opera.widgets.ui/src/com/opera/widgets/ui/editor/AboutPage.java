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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IManagedForm;

import com.opera.widgets.core.widget.Author;
import com.opera.widgets.core.widget.License;
import com.opera.widgets.core.widget.NodeElement;
import com.opera.widgets.core.widget.Widget;
import com.opera.widgets.ui.IHelpContextIds;

/**
 * Features tab in Config Editor. Contains author and licensing information.
 * 
 * @author Michal Borek
 * @see ConfigEditor
 */
public class AboutPage extends AbstractFormPage {

    private Text authorNameText;
    private Text authorEmailText;
    private Text authorHrefText;
    private Text licenseDescText;
    private Text licenseHrefText;

    public AboutPage(ConfigEditor editor) {
	super(editor, "about", Messages.AboutPage_PageName); //$NON-NLS-1$
    }

    @Override
    protected void createFormContent(IManagedForm managedForm) {
	super.createFormContent(managedForm);
	createAuthorSection();
	createLicenseSection();
	addLastFocusListeners(managedForm.getForm());
    }

    /**
     * Updates form editor data after page change
     */
    @Override
    public void updateEditor() {
	super.updateEditor();
	if (fLanguageButton != null) {
	    fLanguageButton.updateSelection();
	}
	Widget widget = getWidget();
	// author section
	Author author = widget.getAuthor();
	String authorName = null;
	String authorEmail = null;
	String authorHref = null;
	if (author != null) {
	    authorName = widget.getAuthor().getName();
	    authorEmail = widget.getAuthor().getEmail();
	    authorHref = widget.getAuthor().getHref();
	}
	authorNameText.setText(authorName != null ? authorName : ""); //$NON-NLS-1$
	authorEmailText.setText(authorEmail != null ? authorEmail : ""); //$NON-NLS-1$
	authorHrefText.setText(authorHref != null ? authorHref : ""); //$NON-NLS-1$

	// license section
	String selectedLanguage = getSelectedLanguage();
	License license = widget.getLicense(selectedLanguage);
	String licenceDesc = null;
	String licenceHref = null;
	if (license != null) {
	    licenceDesc = license.getDescription();
	    licenceHref = license.getHref();
	}
	licenseDescText.setText(licenceDesc != null ? licenceDesc : ""); //$NON-NLS-1$
	licenseHrefText.setText((licenceHref != null) ? licenceHref : ""); //$NON-NLS-1$
    }

    /**
     * Creates section containing author settings (<author> node from
     * config.xml)
     */
    private void createAuthorSection() {
	Composite section = createSection(getManagedForm(),
		Messages.OverviewPage_Author, "", 2); //$NON-NLS-1$
	authorNameText = addTextField(section,
		Messages.OverviewPage_LabelAuthorName, NodeElement.AUTHOR);
	authorNameText.addKeyListener(new KeyAdapter() {
	    @Override
	    public void keyReleased(KeyEvent e) {
		Text element = (Text) e.getSource();
		getWidget().setAuthorName(element.getText());
	    }
	});
	authorEmailText = addTextField(section,
		Messages.OverviewPage_LabelAuthorEmail,
		NodeElement.AUTHOR_EMAIL);
	authorEmailText.addKeyListener(new KeyAdapter() {
	    @Override
	    public void keyReleased(KeyEvent e) {
		Text element = (Text) e.getSource();
		getWidget().setAuthorEmail(element.getText());
	    }
	});
	authorHrefText = addTextField(section,
		Messages.OverviewPage_LabelAuthorHref, NodeElement.AUTHOR_HREF);
	authorHrefText.addKeyListener(new KeyAdapter() {
	    @Override
	    public void keyReleased(KeyEvent e) {
		Text element = (Text) e.getSource();
		getWidget().setAuthorHref(element.getText());
	    }
	});
    }

    /**
     * Creates section containing author settings (<author> node from
     * config.xml)
     */
    private void createLicenseSection() {
	Composite section = createSection(getManagedForm(),
		Messages.OverviewPage_License,
		Messages.OverviewPage_LicenseDescription, 2, true);
	fToolkit.createLabel(section,
		Messages.OverviewPage_LicenseDescriptionLabel);
	licenseDescText = fToolkit.createText(section, "", //$NON-NLS-1$
		SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.WRAP);
	GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
	gridData.minimumHeight = 100;
	gridData.grabExcessVerticalSpace = true;
	licenseDescText.setLayoutData(gridData);
	licenseDescText.addKeyListener(new KeyAdapter() {
	    @Override
	    public void keyReleased(KeyEvent e) {
		Text element = (Text) e.getSource();
		getWidget().setLicenseDescription(element.getText(),
			getSelectedLanguage());
	    }
	});
	licenseHrefText = addTextField(section,
		Messages.OverviewPage_LinkToLicense, NodeElement.LICENSE_HREF);
	licenseHrefText.addKeyListener(new KeyAdapter() {
	    @Override
	    public void keyReleased(KeyEvent e) {
		Text element = (Text) e.getSource();
		getWidget().setLicenseHref(element.getText(),
			getSelectedLanguage());
	    }
	});
    }

    @Override
    public String getHelpResource() {
	return IHelpContextIds.CONFIG_ABOUT;
    }
}
