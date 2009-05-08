/*
 * Generated on Apr 18, 2009. 
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

import fm.radiostation.Radio;

public class TuneResponseHandler extends RESTResponseHandler {

	private static String TYPE = "type";
	private static String NAME = "name";
	private static String URL = "url";
	private static String SUPPORT_DISCOVERY = "supportsdiscovery";
	private static String STATION = "station";
	
	public void startElement(String uri, String localName, String name,
			Attributes attributes) throws SAXException {
		if (STATION.equals(name)) {
			apiResponseObject = new Radio();
		}
	}
	
	public void endElement(String uri, String localName, String name)
			throws SAXException {
		Radio tune = (Radio) apiResponseObject;
		String attr = getAttribute();
		if (STATION.equals(name)) {
			tune.setSuccess(true);
		} else if (TYPE.equals(name)) {
			tune.setType(attr);
		} else if (NAME.equals(name)) {
			tune.setName(attr);
		} else if (URL.equals(name)) {
			tune.setUrl(attr);
		} else if (SUPPORT_DISCOVERY.equals(name)) {
			tune.setSupportdiscovery("1".equals(attr));
		}
	}
}
