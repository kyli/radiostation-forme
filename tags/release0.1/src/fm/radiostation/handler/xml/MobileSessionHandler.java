/*
 * Feb 7, 2009
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

import fm.radiostation.MobileSession;
import fm.radiostation.RSFMUtils;

public class MobileSessionHandler extends RESTResponseHandler{
	
	private static String SUBSCRIBER = "subscriber";
	private static String KEY = "key";
	private static String NAME = "name";
	private static String SESSION = "session";
	
	public void startElement(String uri, String localName, String name,
			Attributes attributes) throws SAXException {
		if (SESSION.equals(name)) {
			apiResponseObject = new MobileSession();
		} 
	}
	
	public void endElement(String uri, String localName, String name)
			throws SAXException {
		MobileSession session = (MobileSession) apiResponseObject;
		String attr = getAttribute();
		if (SESSION.equals(name)) {
			System.out.println("Username: " + session.getUsername());
			System.out.println("Sk: " + session.getSk());
			System.out.println("Subscriber: " + session.isSubscriber());
			session.setSuccess(true);
		} else if (NAME.equals(name)) {
			session.setUsername(attr);
		} else if (KEY.equals(name)) {
			session.setSk(attr);
		} else if (SUBSCRIBER.equals(name)) {
			session.setSubscriber("1".equals(attr));
		} else {
			RSFMUtils.debug("Unprocessed mobile session element name="+name+" attr="+attr, this);
		}
		setAttribute("");
	}
}