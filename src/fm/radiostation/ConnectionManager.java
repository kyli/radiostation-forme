/*
 * Generated on Feb 4, 2009. 
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

package fm.radiostation;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;

import net.rim.blackberry.api.browser.URLEncodedPostData;
import net.rim.device.api.io.http.HttpHeaders;
import net.rim.device.api.system.Branding;
import net.rim.device.api.system.DeviceInfo;
import fm.radiostation.handler.ResponseHandler;

/**
 * Manages and establishes connections to Last.fm API.
 * <p>
 * Keep in mind that both the 1.0 and the 2.0 API will be accessed from an
 * instance of this class. <a href="http://www.last.fm/api/intro">2.0 API</a>
 * responds with REST response, which needs to be read with XML parser. E.g.
 * MobileSession, Playlists in XPFF, and etc. 1.0 API (now obsolete) responds
 * with a property document. Utilities that reads property document is found in @link
 * {@link RSFMUtils#loadProperties(InputStream)}
 * <p>
 * To point out, that the access to the <a
 * href="http://www.last.fm/api/submissions">AudioScrobbler Submission API</a>
 * is using the 1.2.1 submission protocal, which responds in plain text document
 * format.
 * 
 * @author kaiyi
 * 
 */
public final class ConnectionManager {
	
	private static String WEB_SERVICES_ROOT_URL = "http://ws.audioscrobbler.com/2.0/";
	
	public static final String SUBMISSION_ROOT_URL = "http://post.audioscrobbler.com/";
	public static final String SUBMISSION_HANDSHAKE = "?hs=true&p=1.2.1&";
	
	public static final String USER_AGENT = "BlackBerry" + 
		DeviceInfo.getDeviceName() + "/" + DeviceInfo.getSoftwareVersion() + 
		" Profile/" + System.getProperty("microedition.profiles") + " Configuration/" + 
		System.getProperty("microedition.configuration") + " VendorID/" + 
		Branding.getVendorId();

	private final UrlFactory servicemaster = 
		new UrlFactory(UrlFactory.DEFAULT_TRANSPORT_ORDER);

	public ResponseObject getXMLResponse(String api_key,
			String method, Hashtable params, ResponseHandler handler, String httpMethod) {
		String url;
		byte[] postdata = null;
		if (HttpConnection.POST.equals(httpMethod)) {
			url = WEB_SERVICES_ROOT_URL;
			URLEncodedPostData pd = new URLEncodedPostData(URLEncodedPostData.DEFAULT_CHARSET, false);
			pd.append("method", method);
			pd.append("api_key", api_key);
			Enumeration e = params.keys();
			while (e.hasMoreElements()) {
				String key = (String) e.nextElement();
				pd.append(key, (String) params.get(key));
			}
			postdata = pd.getBytes();
		} else { 
			url = buildRESTRequestUrl(method, api_key, params);
		}
		return getResponse(url, handler, postdata);
	}

	public ResponseObject getResponse(String url,
			ResponseHandler handler, byte[] postdata) {
		HttpConnection con = null;
		InputStream in = null;
		try {
			url = servicemaster.appendRimConnectionParam(url, ";ConnectionTimeout=60000");
			RSFMUtils.debug("Request sent to: "+url);
			con = (HttpConnection) Connector.open(url);
			con.setRequestProperty(HttpHeaders.HEADER_USER_AGENT, USER_AGENT);
			if (postdata == null) {
				con.setRequestMethod(HttpConnection.GET);
			} else {
				con.setRequestMethod(HttpConnection.POST);
				con.setRequestProperty(HttpHeaders.HEADER_CONTENT_TYPE, 
						HttpHeaders.CONTENT_TYPE_APPLICATION_X_WWW_FORM_URLENCODED);
				con.setRequestProperty(HttpHeaders.HEADER_CONTENT_LENGTH, 
						Integer.toString(postdata.length));
				OutputStream out = null;
				try {
					out = con.openOutputStream();
					out.write(postdata);
				} finally {
					out.close();
				}
			}
			// HttpConnection state changes to Connected, getting the response
			// code opens the connection.
			int response = con.getResponseCode();
			in = con.openInputStream();
			if (response != HttpConnection.HTTP_OK) {
				RSFMUtils.debug(con.getResponseMessage()
						+ " HTTP response code: " + response);
				int len = (int) con.getLength();
				RSFMUtils.printRESTResponse(in, len);
				throw new IOException();
			}
//			int len = (int) con.getLength();
//			RSFMUtils.printRESTResponse(in, len);
			return handler.handle(in);
		} catch (IOException e) {
			RSFMUtils.debug("Unable to verify server response, " +
					"returning null from ConnectionManager.getResponse");
			return null;
		} catch (Throwable e) {
			e.printStackTrace();
			return null;
		} finally {
			try {
				disconnect(con, in);
			} catch (Throwable e) {
				e.printStackTrace();
				RSFMUtils.debug("Exception when trying to close " +
						"connection after communication is complete. "+e.getMessage(), this);
			}
		}
	}
	
	private void disconnect(HttpConnection con, InputStream in)
			throws IOException {
		if (in != null) {
			in.close();
			con.close();
		}
	}

	/**
	 * Builds the URL with the given parameters. The format of the URL will be
	 * http://ws.audioscrobbler.com/2.0/?method=...&ampapi_key=...&ampparam1=value1...
	 * 
	 * @param method
	 *            the name of the Last.fm API method
	 * @param api_key
	 *            a hexadecimal string of length 32
	 * @param params
	 *            an array of parameter names
	 * @param vals
	 *            an array of values corresponds to parameters
	 * @return the URL for requesting access to a particular Last.fm API method
	 */
	private String buildRESTRequestUrl(String method, String api_key, Hashtable params) {
		StringBuffer sb = new StringBuffer();
		sb.append(WEB_SERVICES_ROOT_URL).append("?");
		sb.append("method=").append(method);
		sb.append("&").append("api_key=").append(api_key);
		if (params != null && !params.isEmpty()) {
			sb.append("&");
			return RSFMUtils.buildURL(sb.toString(), params);
		} else {
			return sb.toString();
		}
	}
}
