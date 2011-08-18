package com.opera.widgets.core.tests.widget;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;

import com.opera.widgets.core.exception.InvalidConfigFileException;
import com.opera.widgets.core.widget.Access;
import com.opera.widgets.core.widget.Feature;
import com.opera.widgets.core.widget.Icon;
import com.opera.widgets.core.widget.JilWidget;
import com.opera.widgets.core.widget.NodeElement;
import com.opera.widgets.core.widget.Param;
import com.opera.widgets.core.widget.Preference;
import com.opera.widgets.core.widget.WACWidgetBase;
import com.opera.widgets.core.widget.Widget;
import com.opera.widgets.core.widget.WidgetModel;

public class WACWidgetBaseTest {

    WACWidgetBase fWidgetBase;
    private String widgetMockConfig = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
	    + "<widget xmlns=\"http://www.w3.org/ns/widgets\" width=\"240\" height=\"320\" viewmodes=\"floating windowed\">"
	    + "<name>Test Widget</name>"
	    + "<description xml:lang=\"pl\">Polish description</description>"
	    + "<description xml:lang=\"en\">English description</description>"
	    + "<author href=\"http://example.com\" email=\"none@dot.con\" >Example name</author>"
	    + "<access origin=\"http://opera.com\" subdomains=\"true\"></access>"
	    + "<access origin=\"http://my.opera.com\" subdomains=\"false\"></access>"
	    + "<preference name=\"val\" value=\"test\"></preference>"
	    + "<preference name=\"val2\" value=\"test2\"></preference>"
	    + "<icon src=\"icon.png\" width=\"120\" height=\"120\" />"
	    + "<icon src=\"icon2.png\"  />"
	    + "<feature name=\"http://test.com\" required=\"true\"><param name=\"var\" value=\"value\" /></feature>"
	    + "<feature name=\"http://testfeature.com\" required=\"true\"></feature>"
	    + "<license href=\"http://test.com\">license description</license>"
	    + "</widget>\n";
    private String widgetMockConfigSimple = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
	    + "<widget xmlns=\"http://www.w3.org/ns/widgets\" id=\"widgetId\" version=\"1.2.2\" viewmodes=\"maximized minimized fullscreen\">"
	    + "<content src=\"index.html\" />" + "</widget>\n";
    private String widgetMockConfigDescriptionOnly = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
	    + "<widget xmlns=\"http://www.w3.org/ns/widgets\" id=\"widgetId\" version=\"1.2.2\" viewmodes=\"maximized minimized fullscreen\">"
	    + "<description xml:lang=\"en\">endesc</description></widget>\n";

    private Document getDocumentFromString(String documentString)
	    throws Exception {
	DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	dbf.setNamespaceAware(true);
	DocumentBuilder db = dbf.newDocumentBuilder();

	ByteArrayInputStream is = new ByteArrayInputStream(
		documentString.getBytes());
	Document result = db.parse(is);

	return result;
    }

    @Before
    public void setUp() throws Exception {

	fWidgetBase = new WACWidgetBase();
	fWidgetBase.setConfigContent(getDocumentFromString(widgetMockConfig));

    }

    @Test
    public void testModelChanged() throws Exception {
	JilWidget widget = new JilWidget();
	WidgetModel model = new WidgetModel(widget);
	widget.setModel(model);
	model.addModelChangedListener(fWidgetBase);
	widget.load(fWidgetBase.getDocument().getDocumentElement());
	fWidgetBase.setModel(model);
	widget.setDescription("testDesc", "pl");
	widget.setDescription("testDefaultDesc", "default");

	Icon icon = new Icon();
	icon.setPath("path");
	widget.add(icon);

	Feature feature = new Feature();
	feature.setName("test");
	feature.setRequired(true);
	widget.add(feature);

	Preference pref = new Preference();
	pref.setName("prefName");
	pref.setReadonly(true);
	widget.add(pref);

	Access access = new Access();
	access.setOrigin("http://domain.com");
	access.setSubdomains(true);
	widget.add(access);

	JilWidget widgetAfter = new JilWidget();

	widgetAfter.load(fWidgetBase.getDocument().getDocumentElement());
	assertEquals("testDesc", widgetAfter.getDescription("pl"));
	assertEquals("testDefaultDesc", widgetAfter.getDescription("default"));
	List<Icon> icons = widgetAfter.getIcons();
	List<Access> accessList = widgetAfter.getAccessList();
	List<Feature> featureList = widgetAfter.getFeatures();
	List<Preference> preferenceList = widget.getPreferences();
	assertEquals(icon, icons.get(icons.size() - 1));
	assertEquals(access, accessList.get(accessList.size() - 1));
	assertEquals(feature, featureList.get(featureList.size() - 1));
	assertEquals(pref, preferenceList.get(preferenceList.size() - 1));
    }

    @Test
    public void testModelChangedUpdate() throws Exception {
	JilWidget widget = new JilWidget();
	WidgetModel model = new WidgetModel(widget);
	widget.setModel(model);
	model.addModelChangedListener(fWidgetBase);
	widget.load(fWidgetBase.getDocument().getDocumentElement());
	fWidgetBase.setModel(model);

	Icon icon = widget.getIcons().get(0);
	icon.setPath("path");
	widget.update(icon);

	Feature feature = widget.getFeatures().get(0);
	feature.setName("test");
	feature.setRequired(true);
	widget.update(feature);

	Preference pref = widget.getPreferences().get(0);
	pref.setName("prefName");
	pref.setReadonly(true);
	widget.update(pref);

	Access access = widget.getAccessList().get(0);
	access.setOrigin("http://domain.com");
	access.setSubdomains(true);
	widget.update(access);

	JilWidget widgetAfter = new JilWidget();
	widgetAfter.load(fWidgetBase.getDocument().getDocumentElement());

	List<Icon> icons = widgetAfter.getIcons();
	List<Access> accessList = widgetAfter.getAccessList();
	List<Feature> featureList = widgetAfter.getFeatures();
	List<Preference> preferenceList = widget.getPreferences();
	assertEquals(icon, icons.get(0));
	assertEquals(access, accessList.get(0));
	assertEquals(feature, featureList.get(0));
	assertEquals(pref, preferenceList.get(0));
    }

    @Test
    public void testModelChangedRemove() throws Exception {
	JilWidget widget = new JilWidget();
	WidgetModel model = new WidgetModel(widget);
	widget.setModel(model);
	model.addModelChangedListener(fWidgetBase);
	widget.load(fWidgetBase.getDocument().getDocumentElement());
	fWidgetBase.setModel(model);

	Icon icon = widget.getIcons().get(0);
	widget.remove(icon);

	Feature feature = widget.getFeatures().get(0);
	widget.remove(feature);

	Preference pref = widget.getPreferences().get(0);
	widget.remove(pref);

	Access access = widget.getAccessList().get(0);
	widget.remove(access);

	JilWidget widgetAfter = new JilWidget();
	widgetAfter.load(fWidgetBase.getDocument().getDocumentElement());

	List<Icon> icons = widgetAfter.getIcons();
	List<Access> accessList = widgetAfter.getAccessList();
	List<Feature> featureList = widgetAfter.getFeatures();
	List<Preference> preferenceList = widget.getPreferences();
	assertEquals(1, icons.size());
	assertEquals("icon2.png", icons.get(0).getPath());
	assertEquals(1, accessList.size());
	assertEquals("http://my.opera.com", accessList.get(0).getOrigin());
	assertEquals(1, featureList.size());
	assertEquals("http://testfeature.com", featureList.get(0).getName());
	assertEquals(1, preferenceList.size());
	assertEquals("val2", preferenceList.get(0).getName());
    }

    @Test
    public void testSetLocalizedElement() throws Exception {
	fWidgetBase
		.setLocalizedElement(NodeElement.DESCRIPTION, "rudesc", "ru");
	fWidgetBase.setLocalizedElement(NodeElement.NAME_SHORT, "ruShortName",
		"ru");

	Widget widget = new Widget();
	widget.load(fWidgetBase.getDocument().getDocumentElement());

	assertEquals("rudesc", widget.getDescription("ru"));
	assertEquals("ruShortName", widget.getShortName("ru"));

	fWidgetBase.setLocalizedElement(NodeElement.NAME_SHORT, "ruShortName2",
		"ru");

	widget = new Widget();
	widget.load(fWidgetBase.getDocument().getDocumentElement());
	assertEquals("ruShortName2", widget.getShortName("ru"));

	fWidgetBase.setLocalizedElement(NodeElement.NAME_SHORT, "", "ru");

	widget = new Widget();
	widget.load(fWidgetBase.getDocument().getDocumentElement());
	assertNull("Short name should be removed", widget.getShortName("ru"));

    }

    @Test
    public void testSetElement() throws Exception {

	fWidgetBase.setElement(NodeElement.DESCRIPTION, "desc");
	fWidgetBase.setElement(NodeElement.CONTENT_SRC, "index.svg");
	fWidgetBase.setElement(NodeElement.NAME_SHORT, "shortNameChanged");
	fWidgetBase.setElement(NodeElement.JIL_ACCESS_NETWORK, "true");

	JilWidget widget = new JilWidget();
	widget.load(fWidgetBase.getDocument().getDocumentElement());

	assertEquals("desc", widget.getDescription("default"));
	assertEquals("index.svg", widget.getContentSrc());
	assertEquals("shortNameChanged", widget.getShortName("default"));
	System.out.println(widget.getJilAccessNetwork());
	assertTrue(widget.getJilAccessNetwork());

	fWidgetBase
		.setConfigContent(getDocumentFromString(widgetMockConfigSimple));
	fWidgetBase.setElement(NodeElement.NAME_SHORT, "short");

	Icon icon = new Icon();
	icon.setPath("qwerty");
	fWidgetBase.add(icon);

	widget = new JilWidget();
	widget.load(fWidgetBase.getDocument().getDocumentElement());

	List<Icon> icons = widget.getIcons();
	assertEquals("short", widget.getShortName("default"));
	assertEquals("qwerty", icons.get(icons.size() - 1).getPath());

    }

    @Test
    public void testAppendElement() throws Exception {

	fWidgetBase
		.setConfigContent(getDocumentFromString(widgetMockConfigDescriptionOnly));

	fWidgetBase.setElement(NodeElement.DESCRIPTION, "test");

	JilWidget widget = new JilWidget();
	widget.load(fWidgetBase.getDocument().getDocumentElement());
	assertEquals("test", widget.getDescription("default"));
    }

    @Test
    public void testAddAccess() throws Exception {

	Access access = new Access();
	access.setOrigin("testorigin");
	access.setSubdomains(true);

	fWidgetBase.add(access);

	Widget widget = new Widget();
	widget.load(fWidgetBase.getDocument().getDocumentElement());
	assertEquals(3, widget.getAccessList().size());
    }

    @Test
    public void testUpdateAccess() throws Exception {
	Widget widget = new Widget();
	widget.load(fWidgetBase.getDocument().getDocumentElement());
	Access access = widget.getAccessList().get(1);
	access.setOrigin("changedOrigin");
	fWidgetBase.update(1, access);
	widget = new Widget();
	widget.load(fWidgetBase.getDocument().getDocumentElement());
	List<Access> accessList = widget.getAccessList();
	Access accessAfterChange = accessList.get(1);
	assertEquals("changedOrigin", accessAfterChange.getOrigin());
    }

    @Test
    public void testRemoveAccess() throws Exception {
	fWidgetBase.removeAccess(1);

	Widget widget = new Widget();
	widget.load(fWidgetBase.getDocument().getDocumentElement());
	List<Access> accessList = widget.getAccessList();
	assertEquals(1, accessList.size());
	assertEquals("http://opera.com", accessList.get(0).getOrigin());
	fWidgetBase.removeAccess(0);
	widget = new Widget();
	widget.load(fWidgetBase.getDocument().getDocumentElement());
	assertEquals(0, widget.getAccessList().size());
    }

    @Test
    public void testAddIcon() throws Exception {
	Icon icon = new Icon();
	icon.setPath("test");
	icon.setWidth(64);
	icon.setHeight(128);

	fWidgetBase.add(icon);

	Widget widget = new Widget();
	widget.load(fWidgetBase.getDocument().getDocumentElement());
	List<Icon> icons = widget.getIcons();
	assertEquals(3, icons.size());
	Icon actualIcon = icons.get(icons.size() - 1);
	assertEquals(new Integer(64), actualIcon.getWidth());
	assertEquals(new Integer(128), actualIcon.getHeight());
	assertEquals("test", actualIcon.getPath());
    }

    @Test
    public void testUpdateIcon() throws Exception {
	Widget widget = new Widget();
	widget.load(fWidgetBase.getDocument().getDocumentElement());
	Icon icon = widget.getIcons().get(1);
	icon.setPath("changedPath.png");
	fWidgetBase.update(1, icon);
	widget = new Widget();
	widget.load(fWidgetBase.getDocument().getDocumentElement());
	List<Icon> icons = widget.getIcons();
	Icon iconAfterChange = icons.get(1);
	assertEquals("changedPath.png", iconAfterChange.getPath());
    }

    @Test
    public void testRemoveIcon() throws Exception {
	fWidgetBase.removeIcon(1);
	Widget widget = new Widget();
	widget.load(fWidgetBase.getDocument().getDocumentElement());
	List<Icon> features = widget.getIcons();
	assertEquals(1, features.size());
	assertEquals("icon.png", features.get(0).getPath());

	fWidgetBase.removeIcon(0);
	widget = new Widget();
	widget.load(fWidgetBase.getDocument().getDocumentElement());
	assertEquals(0, widget.getIcons().size());
    }

    @Test
    public void testAddFeature() throws Exception {
	Feature feature = new Feature();
	feature.setName("test");
	feature.setRequired(true);
	Param param = new Param(feature);
	param.setName("paramName");
	param.setValue("paramValue");
	feature.addParam(param);

	fWidgetBase.add(feature);

	Widget widget = new Widget();
	widget.load(fWidgetBase.getDocument().getDocumentElement());
	List<Feature> features = widget.getFeatures();
	assertEquals(3, features.size());
	Feature actualFeature = features.get(features.size() - 1);
	assertEquals("test", actualFeature.getName());
	assertTrue(actualFeature.isRequired());
    }

    @Test
    public void testUpdateFeature() throws Exception {
	Widget widget = new Widget();
	widget.load(fWidgetBase.getDocument().getDocumentElement());
	Feature feature = widget.getFeatures().get(1);
	feature.setName("changedName");
	fWidgetBase.update(1, feature);
	widget = new Widget();
	widget.load(fWidgetBase.getDocument().getDocumentElement());
	List<Feature> features = widget.getFeatures();
	Feature featureAfterChange = features.get(1);
	assertEquals("changedName", featureAfterChange.getName());
    }

    @Test
    public void testRemoveFeature() throws Exception {
	fWidgetBase.removeFeature(1);
	Widget widget = new Widget();
	widget.load(fWidgetBase.getDocument().getDocumentElement());
	List<Feature> features = widget.getFeatures();
	assertEquals(1, features.size());
	assertEquals("http://test.com", features.get(0).getName());

	fWidgetBase.removeFeature(0);
	widget = new Widget();
	widget.load(fWidgetBase.getDocument().getDocumentElement());
	assertEquals(0, widget.getFeatures().size());
    }

    @Test
    public void testAddPreference() throws Exception {
	Preference pref = new Preference();
	pref.setName("test");
	pref.setValue("prefValue");

	fWidgetBase.add(pref);

	Widget widget = new Widget();
	widget.load(fWidgetBase.getDocument().getDocumentElement());
	List<Preference> preferences = widget.getPreferences();
	assertEquals(3, preferences.size());
	Preference actualPreference = preferences.get(preferences.size() - 1);
	assertEquals("test", actualPreference.getName());
	assertEquals("prefValue", actualPreference.getValue());
    }

    @Test
    public void testUpdatePreference() throws Exception {
	Widget widget = new Widget();
	widget.load(fWidgetBase.getDocument().getDocumentElement());
	Preference preference = widget.getPreferences().get(1);
	preference.setName("changedName");
	preference.setValue("changedValue");
	fWidgetBase.update(1, preference);
	widget = new Widget();
	widget.load(fWidgetBase.getDocument().getDocumentElement());
	List<Preference> preferences = widget.getPreferences();
	Preference featureAfterChange = preferences.get(1);
	assertEquals("changedName", featureAfterChange.getName());
	assertEquals("changedValue", featureAfterChange.getValue());
    }

    @Test
    public void testRemovePreference() throws Exception {
	fWidgetBase.removePreference(1);
	Widget widget = new Widget();
	widget.load(fWidgetBase.getDocument().getDocumentElement());
	List<Preference> preferences = widget.getPreferences();
	assertEquals(1, preferences.size());
	assertEquals("val", preferences.get(0).getName());

	fWidgetBase.removePreference(0);
	widget = new Widget();
	widget.load(fWidgetBase.getDocument().getDocumentElement());
	assertEquals(0, widget.getPreferences().size());

    }
}
