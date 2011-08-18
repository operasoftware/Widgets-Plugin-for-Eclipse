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

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeSet;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.opera.widgets.core.IModelChangedEvent.ChangeType;
import com.opera.widgets.core.exception.InvalidConfigFileException;

public class Widget extends AbstractWidgetObject {

    public enum ViewMode {

	WINDOWED("windowed"), FLOATING("floating"), FULLSCREEN("fullscreen"), MAXIMIZED("maximized"), MINIMIZED( //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		"minimized"); //$NON-NLS-1$

	private String value;

	private ViewMode(String value) {
	    this.value = value;
	}

	public String getValue() {
	    return value;
	}
    }

    // XML node names
    public static final String N_NAME = "name"; //$NON-NLS-1$
    public static final String N_SHORT_NAME = "short"; //$NON-NLS-1$
    public static final String N_DESCRIPTION = "description"; //$NON-NLS-1$
    public static final String N_VIEWMODES = "viewmodes"; //$NON-NLS-1$
    public static final String N_ID = "id"; //$NON-NLS-1$
    public static final String N_WIDTH = "width"; //$NON-NLS-1$
    public static final String N_HEIGHT = "height"; //$NON-NLS-1$
    public static final String N_VERSION = "version"; //$NON-NLS-1$
    public static final String N_FEATURE = "feature"; //$NON-NLS-1$
    public static final String N_PREFERENCE = "preference"; //$NON-NLS-1$
    public static final String N_AUTHOR = "author"; //$NON-NLS-1$
    public static final String N_CONTENT = "content"; //$NON-NLS-1$
    public static final String N_LICENSE = "license"; //$NON-NLS-1$
    public static final String N_ICON = "icon"; //$NON-NLS-1$
    public static final String N_ACCESS = "access"; //$NON-NLS-1$
    public static final String N_BILLING = "billing"; //$NON-NLS-1$

    private Document fConfigDocument;

    private Map<String, String> fNames;
    private Map<String, String> fShortNames;
    private Map<String, String> fDescriptions;
    private List<ViewMode> fViewmodes;
    private String fId;
    private Integer fWidth;
    private Integer fHeight;
    private String fVersion;
    private List<Icon> fIcons;
    private Map<String, License> fLicenses;
    private List<Feature> fFeatures;
    private List<Preference> fPreferences;
    private List<Access> fAccessList;
    private Author fAuthor;
    private Content fContent;

    private SortedSet<String> fAddedLanguages;

    public Widget() {
	fNames = new HashMap<String, String>();
	fShortNames = new HashMap<String, String>();
	fViewmodes = new ArrayList<Widget.ViewMode>();
	fDescriptions = new HashMap<String, String>();
	fLicenses = new HashMap<String, License>();
	fFeatures = new ArrayList<Feature>();
	fPreferences = new ArrayList<Preference>();
	fIcons = new ArrayList<Icon>();
	fAccessList = new ArrayList<Access>();
	fAddedLanguages = new TreeSet<String>();
    }

    /**
     * Loads configuration from file
     * 
     * @param file
     *            with config.xml
     */
    public void loadFromFile(File file) {

	Document doc;
	try {
	    DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
		    .newInstance();
	    DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();

	    doc = docBuilder.parse(new FileInputStream(file));
	    doc.normalize();
	    fConfigDocument = doc;
	    load(doc.getDocumentElement());
	} catch (Exception e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    /**
     * Parse DOM nodes and initialize widget values
     * 
     * @param node
     *            - root node of config.xml
     */
    public void load(Node node) throws InvalidConfigFileException {
	if (node == null) {
	    throw new InvalidConfigFileException();
	}
	fId = getNodeAttribute(node, N_ID);
	try {
	    fWidth = getNodeAttribute(node, N_WIDTH) != null ? Integer
		    .valueOf(getNodeAttribute(node, N_WIDTH)) : 100;
	} catch (NumberFormatException e) {

	}
	try {
	    fHeight = getNodeAttribute(node, N_HEIGHT) != null ? Integer
		    .valueOf(getNodeAttribute(node, N_HEIGHT)) : 100;
	} catch (NumberFormatException e) {

	}
	fVersion = getNodeAttribute(node, N_VERSION);

	fAuthor = new Author();
	fAuthor.setModel(getModel());
	NodeList authorNodes = ((Element) node).getElementsByTagNameNS(
		NodeElement.WIDGET.getNamespace().getValue(), N_AUTHOR);

	if (authorNodes.getLength() > 0) {
	    fAuthor.load(authorNodes.item(0));
	}

	fContent = new Content();
	fContent.setModel(getModel());
	NodeList contentNodes = ((Element) node).getElementsByTagNameNS(
		NodeElement.WIDGET.getNamespace().getValue(), N_CONTENT);
	if (contentNodes.getLength() > 0) {
	    fContent.load(contentNodes.item(0));
	}
	loadViewModes(node);
	loadNames(node);
	loadDescriptions(node);
	loadIcons(node);
	loadFeatures(node);
	loadPreferences(node);
	loadLicenses(node);
	loadAccessList(node);
    }

    private void loadAccessList(Node node) {
	NodeList children = ((Element) node).getElementsByTagNameNS(
		NodeElement.WIDGET.getNamespace().getValue(), N_ACCESS);
	fAccessList.clear();
	for (int i = 0; i < children.getLength(); i++) {
	    Node child = children.item(i);
	    Access access = new Access();
	    access.setModel(getModel());
	    access.load(child);
	    fAccessList.add(access);
	}
    }

    private void loadPreferences(Node node) {
	NodeList children = ((Element) node).getElementsByTagNameNS(
		NodeElement.WIDGET.getNamespace().getValue(), N_PREFERENCE);
	fPreferences.clear();
	for (int i = 0; i < children.getLength(); i++) {
	    Node child = children.item(i);

	    Preference preference = new Preference();
	    preference.setModel(getModel());
	    preference.load(child);
	    fPreferences.add(preference);
	}
    }

    private void loadIcons(Node node) {
	NodeList children = ((Element) node).getElementsByTagNameNS(
		NodeElement.WIDGET.getNamespace().getValue(), N_ICON);
	fIcons.clear();

	for (int i = 0; i < children.getLength(); i++) {
	    Node child = children.item(i);

	    Icon icon = new Icon();
	    icon.setModel(getModel());
	    icon.load(child);
	    fIcons.add(icon);
	}
    }

    private void loadFeatures(Node node) {
	NodeList children = ((Element) node).getElementsByTagNameNS(
		NodeElement.WIDGET.getNamespace().getValue(), N_FEATURE);
	fFeatures.clear();
	for (int i = 0; i < children.getLength(); i++) {
	    Node child = children.item(i);

	    Feature feature = new Feature();
	    feature.setModel(getModel());
	    feature.load(child);
	    fFeatures.add(feature);
	}
    }

    private void loadNames(Node node) {
	NodeList children = ((Element) node).getElementsByTagNameNS(
		NodeElement.WIDGET.getNamespace().getValue(), N_NAME);
	fNames.clear();
	for (int i = 0; i < children.getLength(); i++) {
	    Node child = children.item(i);

	    Node textNode = child.getFirstChild();
	    String name = (textNode != null) ? textNode.getNodeValue() : null;
	    NamedNodeMap nodeMap = child.getAttributes();
	    Node lang = nodeMap.getNamedItem("xml:lang"); //$NON-NLS-1$ //$NON-NLS-2$
	    String langString = (lang != null) ? lang.getNodeValue()
		    : "default"; //$NON-NLS-1$
	    addLanguage(langString);
	    fNames.put(langString, name);

	    // adding short names
	    Node shortName = nodeMap.getNamedItem(N_SHORT_NAME);
	    if (shortName != null) {
		fShortNames.put(langString, shortName.getNodeValue());
	    }
	}
    }

    private void loadLicenses(Node node) {
	NodeList children = ((Element) node).getElementsByTagNameNS(
		NodeElement.WIDGET.getNamespace().getValue(), N_LICENSE);
	fLicenses.clear();
	for (int i = 0; i < children.getLength(); i++) {
	    Node child = children.item(i);
	    NamedNodeMap nodeMap = child.getAttributes();
	    Node lang = nodeMap.getNamedItem("xml:lang"); //$NON-NLS-1$ //$NON-NLS-2$
	    String langString = (lang != null) ? lang.getNodeValue()
		    : "default"; //$NON-NLS-1$

	    License license = addEmptyLicense(langString);
	    license.setModel(getModel());
	    license.load(child);
	}
    }

    private void loadDescriptions(Node node) {
	NodeList children = ((Element) node).getElementsByTagNameNS(
		NodeElement.WIDGET.getNamespace().getValue(), N_DESCRIPTION);
	fDescriptions.clear();
	for (int i = 0; i < children.getLength(); i++) {
	    Node child = children.item(i);

	    Node textNode = child.getFirstChild();
	    String value = textNode != null ? textNode.getNodeValue() : null;
	    NamedNodeMap nodeMap = child.getAttributes();
	    Node lang = nodeMap.getNamedItem("xml:lang"); //$NON-NLS-1$ //$NON-NLS-2$

	    String langString = (lang != null) ? lang.getNodeValue()
		    : "default"; //$NON-NLS-1$
	    fDescriptions.put(langString, value);

	    addLanguage(langString);

	}
    }

    private void loadViewModes(Node node) {
	NamedNodeMap nodeMap = node.getAttributes();
	fViewmodes.clear();
	Node viewmodesNode = nodeMap.getNamedItem(N_VIEWMODES);
	if (viewmodesNode != null) {
	    StringTokenizer st = new StringTokenizer(
		    viewmodesNode.getNodeValue());
	    while (st.hasMoreElements()) {
		String viewMode = st.nextToken();
		if (viewMode.equals(ViewMode.FLOATING.getValue())) {
		    fViewmodes.add(ViewMode.FLOATING);
		} else if (viewMode.equals(ViewMode.FULLSCREEN.getValue())) {
		    fViewmodes.add(ViewMode.FULLSCREEN);
		} else if (viewMode.equals(ViewMode.WINDOWED.getValue())) {
		    fViewmodes.add(ViewMode.WINDOWED);
		} else if (viewMode.equals(ViewMode.MAXIMIZED.getValue())) {
		    fViewmodes.add(ViewMode.MAXIMIZED);
		} else if (viewMode.equals(ViewMode.MINIMIZED.getValue())) {
		    fViewmodes.add(ViewMode.MINIMIZED);
		}
	    }
	}
    }

    /**
     * Checks if widget is in valid state. If false it means that config.xml is
     * not properly set.
     */
    public boolean isValid() {
	for (Preference el : fPreferences) {
	    if (!el.isValid()) {
		return false;
	    }
	}
	for (Feature el : fFeatures) {
	    if (!el.isValid()) {
		return false;
	    }
	}
	for (Icon el : fIcons) {
	    if (!el.isValid()) {
		return false;
	    }
	}
	for (License el : fLicenses.values()) {
	    if (!el.isValid()) {
		return false;
	    }
	}
	if (!fContent.isValid() || !fAuthor.isValid()) {
	    return false;
	}
	return true;
    }

    public List<ViewMode> getViewmodes() {
	return fViewmodes;
    }

    public void setViewmodes(List<ViewMode> viewmodes) {
	fViewmodes = viewmodes;
	StringBuffer sb = new StringBuffer();

	for (ViewMode vm : viewmodes) {
	    sb.append(vm.getValue());
	    sb.append(" "); //$NON-NLS-1$
	}
	firePropertyChanged(NodeElement.WIDGET_ATTRIBUTE_VIEWMODES, null, sb
		.toString().trim());
    }

    public String getId() {
	return fId;
    }

    public void setId(String id) {
	String oldValue = fId;
	fId = id;
	firePropertyChanged(NodeElement.WIDGET_ATTRIBUTE_ID, oldValue, id);
    }

    public Integer getWidth() {
	return fWidth;
    }

    public void setWidth(Integer width) {
	Integer oldValue = this.fWidth;
	this.fWidth = width;
	firePropertyChanged(NodeElement.WIDGET_ATTRIBUTE_WIDTH,
		oldValue.toString(), width.toString());
    }

    public Integer getHeight() {
	return fHeight;
    }

    public void setHeight(Integer height) {
	Integer oldValue = this.fHeight;
	this.fHeight = height;
	firePropertyChanged(NodeElement.WIDGET_ATTRIBUTE_HEIGHT,
		oldValue.toString(), height.toString());
    }

    public String getVersion() {
	return fVersion;
    }

    public void setVersion(String version) {
	String oldValue = version;
	this.fVersion = version;
	firePropertyChanged(NodeElement.WIDGET_ATTRIBUTE_VERSION, oldValue,
		version);
    }

    public List<Feature> getFeatures() {
	return fFeatures;
    }

    public void setFeatures(List<Feature> featureList) {
	this.fFeatures = featureList;
    }

    public List<Preference> getPreferences() {
	return fPreferences;
    }

    public List<Access> getAccessList() {
	return fAccessList;
    }

    public void setPreferences(List<Preference> preferenceList) {
	this.fPreferences = preferenceList;
    }

    public Author getAuthor() {
	return fAuthor;
    }

    public void setAuthor(Author author) {
	this.fAuthor = author;
    }

    public Content getContent() {
	return fContent;
    }

    public void setContent(Content content) {
	this.fContent = content;
    }

    public List<Icon> getIcons() {
	return fIcons;
    }

    public String getName(String lang) {
	if (lang == null) {
	    lang = "default"; //$NON-NLS-1$
	}
	return fNames.get(lang); //$NON-NLS-1$
    }

    public void setName(String text, String lang) {
	String oldValue = fNames.get(lang); //$NON-NLS-1$
	if (!text.equals(oldValue)) {
	    fNames.put(lang, text);
	    firePropertyChanged(NodeElement.NAME, oldValue, text, lang);
	}
    }

    public String getShortName(String lang) {
	return fShortNames.get(lang); //$NON-NLS-1$
    }

    public void setShortName(String text, String lang) {
	String oldValue = fShortNames.get(lang); //$NON-NLS-1$
	if (!text.equals(oldValue)) {
	    fShortNames.put(lang, text); //$NON-NLS-1$
	    firePropertyChanged(NodeElement.NAME_SHORT, oldValue, text, lang);
	}
    }

    public String getDescription(String lang) {
	return fDescriptions.get(lang); //$NON-NLS-1$
    }

    public void setDescription(String text, String lang) {
	addLanguage(lang);
	String oldValue = fDescriptions.get(lang); //$NON-NLS-1$
	if (!text.equals(oldValue)) {
	    fDescriptions.put(lang, text); //$NON-NLS-1$    
	    firePropertyChanged(NodeElement.DESCRIPTION, oldValue, text, lang);
	}
    }

    public License getLicense(String lang) {
	return fLicenses.get(lang); //$NON-NLS-1$
    }

    public void setLicenseDescription(String description, String lang) {
	addLanguage(lang);
	License license = getLicense(lang);
	if (license == null) {
	    license = addEmptyLicense(lang);
	}
	license.setDescription(description);
    }

    private License addEmptyLicense(String lang) {
	addLanguage(lang);
	License license;
	license = new License();
	license.setModel(getModel());
	if (lang != null && !lang.equals("default")) { //$NON-NLS-1$
	    license.setLanguage(lang);
	}
	fLicenses.put(lang, license);
	return license;
    }

    public void setLicenseHref(String href, String lang) {
	License license = getLicense(lang);
	if (license == null) {
	    license = addEmptyLicense(lang);
	}
	license.setHref(href);
    }

    public void setAuthorName(String text) {
	Author author = getAuthor();
	if (author == null) {
	    author = new Author();
	    author.setModel(getModel());
	    this.fAuthor = author;
	}
	author.setName(text);
    }

    public void setAuthorEmail(String text) {
	Author author = getAuthor();
	if (author == null) {
	    author = new Author();
	    author.setModel(getModel());
	    this.fAuthor = author;
	}
	author.setEmail(text);
    }

    public void setAuthorHref(String text) {
	Author author = getAuthor();
	if (author == null) {
	    author = new Author();
	    author.setModel(getModel());
	    this.fAuthor = author;
	}
	author.setHref(text);
    }

    public void setContentSrc(String text) {
	Content content = getContent();
	if (content == null) {
	    content = new Content();
	    content.setModel(getModel());
	    this.fContent = content;
	}
	content.setSrc(text);
    }

    public void setContentType(String text) {
	Content content = getContent();
	if (content == null) {
	    content = new Content();
	    this.fContent = content;
	    content.setModel(getModel());
	}
	content.setType(text);
    }

    public void setContentEncoding(String text) {
	Content content = getContent();
	if (content == null) {
	    content = new Content();
	    content.setModel(getModel());
	    this.fContent = content;
	}
	content.setEncoding(text);
    }

    public String getContentSrc() {
	Content content = getContent();
	return content != null ? content.getSrc() : null;
    }

    public String getContentEncoding() {
	Content content = getContent();
	return content != null ? content.getEncoding() : null;
    }

    public String getContentType() {
	Content content = getContent();
	return content != null ? content.getType() : null;
    }

    public void add(Access access) {
	access.setModel(getModel());
	fAccessList.add(access);
	fireStructureChanged(access, ChangeType.OBJECT_INSERT);
    }

    public void add(Icon icon) {
	icon.setModel(getModel());
	fIcons.add(icon);
	fireStructureChanged(icon, ChangeType.OBJECT_INSERT);
    }

    public void add(Feature feature) {
	feature.setModel(getModel());
	fFeatures.add(feature);
	fireStructureChanged(feature, ChangeType.OBJECT_INSERT);
    }

    public void add(Preference preference) {
	preference.setModel(getModel());
	fPreferences.add(preference);
	fireStructureChanged(preference, ChangeType.OBJECT_INSERT);
    }

    public void update(Access access) {
	fireStructureChanged(access, ChangeType.OBJECT_CHANGE);
    }

    /**
     * Sets Access origin to * and removes other declarations of access
     */
    public void setAccessToAll() {
	int i = 0;
	while (i < fAccessList.size()) {
	    Access el = fAccessList.get(i);
	    if (el.getOrigin().equals("*") && !el.isSubdomains()) { //$NON-NLS-1$
		i++;
	    } else {
		remove(el);
	    }
	}
	if (i == 0) {
	    Access newAccess = new Access();
	    newAccess.setOrigin("*"); //$NON-NLS-1$
	    add(newAccess);
	}
    }

    public boolean isAccessToAll() {
	Access allAccess = new Access();
	allAccess.setOrigin("*"); //$NON-NLS-1$
	return fAccessList.contains(allAccess);
    }

    public void update(Icon icon) {
	fireStructureChanged(icon, ChangeType.OBJECT_CHANGE);
    }

    public void update(Feature feature) {
	fireStructureChanged(feature, ChangeType.OBJECT_CHANGE);
    }

    public void update(Preference preference) {
	fireStructureChanged(preference, ChangeType.OBJECT_CHANGE);
    }

    public void remove(Access access) {
	fireStructureChanged(access, ChangeType.OBJECT_REMOVE);
	fAccessList.remove(access);
    }

    public void remove(Icon icon) {
	fireStructureChanged(icon, ChangeType.OBJECT_REMOVE);
	fIcons.remove(icon);
    }

    public void remove(Feature feature) {
	fireStructureChanged(feature, ChangeType.OBJECT_REMOVE);
	fFeatures.remove(feature);
    }

    public void remove(Preference preference) {
	fireStructureChanged(preference, ChangeType.OBJECT_REMOVE);
	fPreferences.remove(preference);
    }

    public String[] getAddedLanguages() {
	return fAddedLanguages.toArray(new String[0]);
    }

    public void addLanguage(String lang) {
	if (!lang.equals("default")) { //$NON-NLS-1$
	    fAddedLanguages.add(lang);
	}
    }

    public Document getDocument() {
	return fConfigDocument;
    }
}
