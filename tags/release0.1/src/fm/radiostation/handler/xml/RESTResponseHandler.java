/*
 * Feb 9, 2009
 * radiostation-forme
 * 
 * Copyright (C) 2009  Kaiyi Li
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package fm.radiostation.handler.xml;

import java.io.InputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import fm.radiostation.ResponseObject;
import fm.radiostation.handler.ResponseHandler;

/**
 * The abstract parent class of the SAX handlers used in radiostation.forme. It
 * traces through attributes so that they can be selected by more specific
 * APIResponseObject handlers to construct specific APIResponseObjects as
 * necessary. There are a few primary functionalities defined by this class:
 * 
 * <li>Dynamically Select attributes as properties of the enclosing APIResponseObject
 * <li>Dynamically build and return finished APIResponseObjects 
 * 
 * @author kaiyi
 * 
 */
abstract public class RESTResponseHandler extends DefaultHandler implements ResponseHandler {

	/**
	 * The current attribute in process
	 */
	private String attribute;
	/**
	 * The underlying APIResponseObject
	 */
	protected ResponseObject apiResponseObject;
	
	public ResponseObject handle(InputStream in) {
		SAXParserFactory saxFac = SAXParserFactory.newInstance();
		try {
			SAXParser parser = saxFac.newSAXParser();
			parser.parse(in, this);
			return apiResponseObject;
		} catch (Throwable e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Captures the attribute between two tags
	 */
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		attribute = new String(ch, start, length);
	}
	
	/**
	 * Get the current attribute
	 */
	String getAttribute() {
		return attribute;
	}
	
	/**
	 * Set the current attribute
	 */
	void setAttribute(String attr) {
		attribute = attr;
	}
}
