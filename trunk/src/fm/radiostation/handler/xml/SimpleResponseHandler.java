/*
 * Generated on Mar 18, 2009. 
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

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import fm.radiostation.RSFMUtils;
import fm.radiostation.ResponseObject;

public class SimpleResponseHandler extends RESTResponseHandler {

	private static String LFM = "lfm";
	private static String ERROR = "error";
	private static String STATUS = "status";
	private static String CODE = "code";
	private static String OK = "ok";

	public void startElement(String uri, String localName, String name,
			Attributes attributes) throws SAXException {
		if (LFM.equals(name)) {
			apiResponseObject = new ResponseObject();
			if (OK.equals(attributes.getValue(STATUS))) {
				apiResponseObject.setSuccess(true);
			}
		} else if (ERROR.equals(name)) {
			RSFMUtils.debug("Last.fm API returned error code: "+attributes.getValue(CODE)+" ;Error message: ");
		} 
	}
	
	public void endElement(String uri, String localName, String name)
			throws SAXException {
		String attr = getAttribute();
		if (ERROR.equals(name)) {
			RSFMUtils.debug(attr);
		} else {
			RSFMUtils.debug("Unprocessed simple response element name="+name+" attr="+attr, this);
		}
		setAttribute("");
	}
}
