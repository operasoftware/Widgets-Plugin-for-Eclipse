package com.opera.widgets.core.tests.widget;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.opera.widgets.core.widget.JilAccess;
import com.opera.widgets.core.widget.JilWidget;

public class JilWidgetTest {

    private String fWidgetMockConfig = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
	    + "<widget xmlns=\"http://www.w3.org/ns/widgets\" xmlns:JIL=\"http://www.jil.org/ns/widgets1.2\" id=\"widgetId\" version=\"1.2.2\" viewmodes=\"maximized minimized fullscreen\">"
	    + "<name short=\"testWidget\">Test Widget 2</name>"
	    + "<JIL:billing required=\"true\"></JIL:billing>"
	    + "<JIL:access network=\"true\" />"
	    + "<content src=\"index.html\" />" + "</widget>\n";
    private JilWidget fWidget;

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
	fWidget = new JilWidget();

	Element documentElement = getDocumentFromString(fWidgetMockConfig)
		.getDocumentElement();
	fWidget.load(documentElement);
    }

    @Test
    public void testLoad() throws Exception {
	assertTrue("Billing should not be required",
		fWidget.getJilBillingRequired());
	assertTrue("Network access should be enabled",
		fWidget.getJilAccessNetwork());
    }

    @Test
    public void testSetBillingRequired() {
	fWidget.setJilBillingRequired(false);
	assertFalse(fWidget.getJilBillingRequired());
	fWidget.setJilBillingRequired(true);
	assertTrue(fWidget.getJilBillingRequired());
    }

    @Test
    public void testSetAccessNetwork() {
	fWidget.setJilAccessNetwork(false);
	assertFalse(fWidget.getJilAccessNetwork());
	fWidget.setJilAccessNetwork(true);

	JilAccess access = new JilAccess();
	access.setNetwork(true);
	assertEquals(access, fWidget.getJilAccess());
    }
}
