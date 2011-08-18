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

package com.opera.widgets.ui.editor.dialog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.wizard.Wizard;

import com.opera.widgets.core.widget.Feature;
import com.opera.widgets.ui.WidgetsActivator;

/**
 * Wizard for widget feature element modification.
 * 
 * @author Michal Borek
 */
public class FeatureWizard extends Wizard {

    private final static Map<String, String[]> fProposals;
    protected FeatureDetailsPage featureDetailsPage;
    protected Feature feature;
    protected List<Feature> fFeatures;

    static {
	fProposals = getFeatureNameProposals();
    }

    public FeatureWizard() {
	feature = new Feature();
    }

    public FeatureWizard(Feature feature) {
	this.feature = feature;
    }

    @Override
    public boolean performFinish() {
	feature.setName(featureDetailsPage.getFeatureName());
	feature.setRequired(featureDetailsPage.getRequired());
	feature.setParams(featureDetailsPage.getParams());
	fFeatures = new ArrayList<Feature>();
	fFeatures.add(feature);
	String[] proposals = fProposals.get(feature.getName());
	for (String uri : proposals) {
	    Feature f = new Feature();
	    f.setName(uri);
	    f.setRequired(feature.isRequired());
	    fFeatures.add(f);
	}
	return true;
    }

    @Override
    public void addPages() {
	featureDetailsPage = new FeatureDetailsPage(
		"featureDetails", feature, fProposals); //$NON-NLS-1$
	addPage(featureDetailsPage);
    }

//    public Feature getFeature() {
//	return feature;
//    }

    public List<Feature> getFeatures() {
	return fFeatures;
    }

    public static Map<String, String[]> getFeatureNameProposals() {
	Map<String, String[]> proposals = new HashMap<String, String[]>();
	try {
	    InputStream is = WidgetsActivator.getDefault().getBundle()
		    .getEntry("resources/feature_name_proposals.txt") //$NON-NLS-1$
		    .openStream();
	    BufferedReader br = new BufferedReader(new InputStreamReader(is));
	    String str;
	    while ((str = br.readLine()) != null) {
		String[] features = str.split(";");
		proposals.put(features[0],
			Arrays.copyOfRange(features, 1, features.length));
	    }

	} catch (IOException e) {

	}
	return proposals;
    }

}
