package com.opera.widgets.core.tests.widget;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;

import com.opera.widgets.core.widget.Access;
import com.opera.widgets.core.widget.Feature;
import com.opera.widgets.core.widget.Icon;
import com.opera.widgets.core.widget.License;
import com.opera.widgets.core.widget.Preference;
import com.opera.widgets.core.widget.Widget;
import com.opera.widgets.core.widget.Widget.ViewMode;
import com.opera.widgets.core.widget.WidgetModel;

public class WidgetTest {

    private Document fWidgetDocument;
    private Widget fWidget;
    private WidgetModel fWidgetModel;
    private String widgetMockConfig = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
	    + "<widget xmlns=\"http://www.w3.org/ns/widgets\" width=\"240\" height=\"320\" viewmodes=\"floating windowed\">"
	    + "<name>Test Widget</name>"
	    + "<description xml:lang=\"pl\">Polish description</description>"
	    + "<description xml:lang=\"en\">English description</description>"
	    + "<author href=\"http://example.com\" email=\"none@dot.con\" >Example name</author>"
	    + "<access origin=\"http://opera.com\" subdomains=\"true\"></access>"
	    + "<access origin=\"http://my.opera.com\" subdomains=\"false\"></access>"
	    + "<preference name=\"val\" value=\"test\"></preference>"
	    + "<icon src=\"icon.png\" width=\"120\" height=\"120\" />"
	    + "<icon src=\"icon.png\"  />"
	    + "<feature name=\"http://test.com\" required=\"true\"><param name=\"var\" value=\"value\" /></feature>"
	    + "<license href=\"http://test.com\">license description</license>"
	    + "</widget>\n";
    private String widgetMockConfigSimple = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
	    + "<widget xmlns=\"http://www.w3.org/ns/widgets\" id=\"widgetId\" version=\"1.2.2\" viewmodes=\"maximized minimized fullscreen\">"
	    + "<name short=\"testWidget\">Test Widget 2</name>"
	    + "<content src=\"index.html\" />" + "</widget>\n";

    private Document getDocumentFromString(String documentString)
	    throws Exception {
	DocumentBuilder db = DocumentBuilderFactory.newInstance()
		.newDocumentBuilder();
	ByteArrayInputStream is = new ByteArrayInputStream(
		documentString.getBytes());
	Document result = db.parse(is);

	return result;
    }

    @Before
    public void before() {
	try {
	    DocumentBuilder db = DocumentBuilderFactory.newInstance()
		    .newDocumentBuilder();
	    ByteArrayInputStream is = new ByteArrayInputStream(
		    widgetMockConfig.getBytes());
	    fWidgetDocument = db.parse(is);

	    fWidget = new Widget();
	    fWidget.load(fWidgetDocument.getDocumentElement());

	} catch (Exception e) {
	    fail("Failed to initialize test case " + WidgetTest.class.getName()
		    + " " + e.getMessage());
	}

	fWidgetModel = new WidgetModel(fWidget);
	fWidget.setModel(fWidgetModel);
    }

    @Test
    public void testLoadFromFile() throws IOException {
	File file = File.createTempFile("config", "xml");
	try {
	    FileWriter fw;
	    fw = new FileWriter(file);
	    fw.write(widgetMockConfig);
	    fw.close();
	    Widget widget = new Widget();
	    widget.loadFromFile(file);
	    assertEquals("Test Widget", widget.getName("default"));
	    assertEquals("Test Widget", widget.getName(null));
	    assertEquals(new Integer(240), widget.getWidth());
	    assertEquals(new Integer(320), widget.getHeight());
	    assertEquals("English description", widget.getDescription("en"));
	} finally {
	    file.delete();
	}

    }

    @Test
    public void testLoad() throws Exception {
	Widget widget = new Widget();
	widget.load(getDocumentFromString(widgetMockConfig)
		.getDocumentElement());
	assertEquals("Test Widget", widget.getName(null));
	assertEquals(new Integer(240), widget.getWidth());
	assertEquals(new Integer(320), widget.getHeight());
	assertEquals("English description", widget.getDescription("en"));
	assertEquals("Example name", widget.getAuthor().getName());
	assertEquals("license description", widget.getLicense("default")
		.getDescription());

	License license = new License();
	license.setHref("http://test.com");
	license.setDescription("license description");
	assertEquals(license, widget.getLicense("default"));
	assertEquals("var", widget.getFeatures().get(0).getParams().get(0).getName());
	assertEquals("value", widget.getFeatures().get(0).getParams().get(0).getValue());

	Widget widget2 = new Widget();
	widget2.load(getDocumentFromString(widgetMockConfigSimple)
		.getDocumentElement());
	assertEquals("Test Widget 2", widget2.getName(null));
	assertArrayEquals(new Widget.ViewMode[] { ViewMode.MAXIMIZED,
		ViewMode.MINIMIZED, ViewMode.FULLSCREEN }, widget2
		.getViewmodes().toArray());
	assertEquals("index.html", widget2.getContent().getSrc());
	assertEquals("1.2.2", widget2.getVersion());
	assertEquals("widgetId", widget2.getId());
	assertEquals("testWidget", widget2.getShortName("default"));
    }

    @Test
    public void testIsValid() {
	assertTrue(fWidget.isValid());
    }

    @Test
    public void testAddAccess() {
	Access access = new Access();
	access.setOrigin("http://example.com");
	fWidget.add(access);
	List<Access> accessList = fWidget.getAccessList();
	assertEquals(access, accessList.get(accessList.size() - 1));
	assertSame("Model should have been set", fWidgetModel,
		access.getModel());
    }

    @Test
    public void testAddIcon() {
	Icon icon = new Icon();
	icon.setPath("icons/test.png");
	fWidget.add(icon);
	List<Icon> iconList = fWidget.getIcons();
	assertEquals(icon, iconList.get(iconList.size() - 1));
	assertSame("Model should have been set", fWidgetModel, icon.getModel());
    }

    @Test
    public void testAddFeature() {
	Feature feature = new Feature();
	feature.setName("http://testfeature.com");
	fWidget.add(feature);
	List<Feature> featureList = fWidget.getFeatures();
	assertEquals(feature, featureList.get(featureList.size() - 1));
	assertSame("Model should have been set", fWidgetModel,
		feature.getModel());
    }

    @Test
    public void testAddPreference() {
	Preference preference = new Preference();
	preference.setName("http://testPreference.com");
	fWidget.add(preference);
	List<Preference> PreferenceList = fWidget.getPreferences();
	assertEquals(preference, PreferenceList.get(PreferenceList.size() - 1));
	assertSame("Model should have been set", fWidgetModel,
		preference.getModel());
    }

    @Test
    public void testRemoveAccess() {
	Access accessOne = new Access();
	accessOne.setOrigin("http://opera.com");
	Access accessTwo = new Access();
	accessTwo.setOrigin("http://my.opera.com");
	fWidget.add(accessOne);
	fWidget.add(accessTwo);

	assertEquals("There should be two access elements", 4, fWidget
		.getAccessList().size());

	fWidget.remove(accessOne);

	assertFalse("Access element should be removed", fWidget.getAccessList()
		.contains(accessOne));

    }

    @Test
    public void testRemoveIcon() {
	Icon iconOne = new Icon();
	iconOne.setPath("/icons/test.png");
	Icon iconTwo = new Icon();
	iconOne.setPath("/icons/test2.png");
	fWidget.add(iconOne);
	fWidget.add(iconTwo);

	assertEquals("There should be two icon elements", 4, fWidget.getIcons()
		.size());

	fWidget.remove(iconOne);

	assertFalse("Icon element should be removed", fWidget.getIcons()
		.contains(iconOne));
    }

    @Test
    public void testRemoveFeature() {
	Feature featureOne = new Feature();
	featureOne.setName("http://feature.com");
	Feature featureTwo = new Feature();
	featureOne.setName("http://feature.com/second");
	featureOne.setRequired(true);
	fWidget.add(featureOne);
	fWidget.add(featureTwo);

	assertEquals("There should be two feature elements", 3, fWidget
		.getFeatures().size());

	fWidget.remove(featureOne);

	assertFalse("Feature element should be removed", fWidget.getFeatures()
		.contains(featureOne));
    }

    @Test
    public void testRemovePreference() {
	Preference preferenceOne = new Preference();
	preferenceOne.setName("http://feature.com");
	Preference preferenceTwo = new Preference();
	preferenceOne.setName("http://feature.com/second");
	preferenceOne.setReadonly(true);
	fWidget.add(preferenceOne);
	fWidget.add(preferenceTwo);

	assertEquals("There should be two feature elements", 3, fWidget
		.getPreferences().size());

	fWidget.remove(preferenceOne);

	assertFalse("Preference element should be removed", fWidget
		.getPreferences().contains(preferenceOne));
    }

    @Test
    public void testGetAddedLanguages() {
	String[] addedLangs = fWidget.getAddedLanguages();
	assertArrayEquals(new String[] { "en", "pl" }, addedLangs);
    }

    @Test
    public void testSetAccessToAll() {
	fWidget.setAccessToAll();
	Access access = new Access();
	access.setOrigin("*");
	access.setSubdomains(false);
	List<Access> accessList = fWidget.getAccessList();
	assertEquals(1, accessList.size());
	assertEquals(access, accessList.get(0));

    }

}
