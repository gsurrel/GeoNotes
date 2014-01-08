package org.surrel.geoposts;

import java.util.ArrayList;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/*
 * Default Notification handler class for receiving ContentHandler
 * events raised by the SAX Parser
 * 
 * */
public class XParser extends DefaultHandler {

	ArrayList<String> titlelist = new ArrayList<String>();
	ArrayList<String> textlist = new ArrayList<String>();
	ArrayList<String> categorylist = new ArrayList<String>();
	ArrayList<String> creationlist = new ArrayList<String>();
	
	//temp variable to store the data chunk read while parsing 
	private String tempStore	=	null;
		
	public XParser() {
		// TODO Auto-generated constructor stub
	}
	
	/*
	 * Clears the tempStore variable on every start of the element
	 * notification
	 * 
	 * */
	public void startElement (String uri, String localName, String qName,
			   Attributes attributes) throws SAXException {
	
		super.startElement(uri, localName, qName, attributes);
		
		if (localName.equalsIgnoreCase("text")) {
			tempStore = "";
		} else if (localName.equalsIgnoreCase("title")) {
			tempStore = "";
		} 
		else if (localName.equalsIgnoreCase("creation")) {
			tempStore = "";
		}
		else if (localName.equalsIgnoreCase("category")) {
			tempStore = "";	tempStore = "";
		}
		else {
			tempStore = "";
		}
	}
	
	/*
	 * updates the value of the tempStore variable into
	 * corresponding list on receiving end of the element
	 * notification
	 * */
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		super.endElement(uri, localName, qName);
		
		if (localName.equalsIgnoreCase("text")) {
			textlist.add(tempStore);
		} 
		else if (localName.equalsIgnoreCase("title")) {
			titlelist.add(tempStore);
		}
		else if (localName.equalsIgnoreCase("category")) {
			categorylist.add(tempStore);
		}
		else if (localName.equalsIgnoreCase("creation")) {
			creationlist.add(tempStore);
		}
		
		tempStore = "";
	}
	
	/*
	 * adds the incoming data chunk of character data to the 
	 * temp data variable - tempStore
	 * 
	 * */
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		super.characters(ch, start, length);
		tempStore += new String(ch, start, length);
	}

}
