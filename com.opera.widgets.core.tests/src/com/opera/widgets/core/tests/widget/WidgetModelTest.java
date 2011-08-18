package com.opera.widgets.core.tests.widget;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;

import com.opera.widgets.core.IModelChangedEvent;
import com.opera.widgets.core.IModelChangedListener;
import com.opera.widgets.core.widget.Widget;
import com.opera.widgets.core.widget.WidgetModel;

public class WidgetModelTest {

    WidgetModel widgetModel;
    private String widgetMockConfig = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
	    + "<widget xmlns=\"http://www.w3.org/ns/widgets\" width=\"240\" height=\"320\">"
	    + "<name>Test Widget</name>"
	    + "<description xml:lang=\"en\">English description</description>"
	    + "</widget>\n";
    private Widget fWidget;

    @Before
    public void setUp() throws Exception {
	try {
	    DocumentBuilder db = DocumentBuilderFactory.newInstance()
		    .newDocumentBuilder();
	    ByteArrayInputStream is = new ByteArrayInputStream(
		    widgetMockConfig.getBytes());
	    Document widgetDocument = db.parse(is);

	    fWidget = new Widget();
	    fWidget.load(widgetDocument.getDocumentElement());

	} catch (Exception e) {
	    fail("Failed to initialize test case " + WidgetTest.class.getName()
		    + " " + e.getMessage());
	}

    }

    @Test
    public void testFireModelChanged() {
	WidgetModel model = new WidgetModel(fWidget);
	fWidget.setModel(model);

	final List<String> firedList = new ArrayList<String>();
	// check if fired change event
	model.addModelChangedListener(new IModelChangedListener() {

	    @Override
	    public void modelChanged(IModelChangedEvent event) {
		assertEquals("en", event.getLanguage());
		assertEquals("English description", event.getOldValue());
		assertEquals("Desc", event.getNewValue());
		firedList.add("fired");
	    }
	});
	fWidget.setDescription("Desc", "en");
	assertEquals("Check if fired", 1, firedList.size());
    }
}
