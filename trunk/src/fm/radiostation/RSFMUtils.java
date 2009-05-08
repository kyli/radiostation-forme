/*
 * Generated on Feb 2, 2009. 
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

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import net.rim.device.api.io.LineReader;

import com.twmacinta.util.MD5;

/**
 * Contains a set of utility methods used in RadioStation.ForMe.
 * 
 * @author kaiyi
 */
public final class RSFMUtils {

	/**
	 * Encrypt the input string using MD5 hashing algorithm and return the
	 * result hexadecimal string. The MD5 hashing implementation is provided in
	 * {@link com.twmacinta.util.MD5}. It <b>does not</b> use the default RIM
	 * implementation that came with the API.
	 * 
	 * @param input
	 * @return A hexadecimal string of the input produced through the md5
	 *         hashing algorithm
	 */
	public static String md5(String input) {
		if (input == null) {
			return null;
		}
		byte[] bytes = input.getBytes();
		MD5 md5 = new MD5(bytes);
		byte[] result = md5.doFinal();
		return MD5.toHex(result);
	}

	/**
	 * Creates a hexadecimal representation of the api_signature. Its
	 * specification can be found on the <a
	 * href="http://www.last.fm/api/mobileauth#4">Last.fm API:
	 * Authentication</a>
	 * 
	 * @param paramList
	 *            parameter name concatenate with value, parameters must be in
	 *            alphabetical order
	 * @param secret
	 * @return a hexadecimal string representing the api_sig
	 */
	public static String createApiSignature(Vector paramList,
			String secret) {
		StringBuffer api_sig = new StringBuffer();
		for (int i = 0; i < paramList.size(); i++) {
			api_sig.append(paramList.elementAt(i));
		}
		api_sig.append(secret);
		String ret = api_sig.toString();
		return md5(ret);
	}

	/**
	 * Build a generic URL given a rootURL, and a hashtable containing
	 * parameters and their respective values
	 * <p>
	 * For Example, given the rootURL to be
	 * <b>http://ws.audioscrobbler.com/2.0/?</b> and a series of parameters. The
	 * created URL will be of the form
	 * <b>http://ws.audioscrobbler.com/2.0/?param1
	 * =value1&ampparam2=value2...</b>
	 */
	public static String buildURL(String rootURL, Hashtable params) {
		StringBuffer sb = new StringBuffer(rootURL);
		if (params != null) {
			for (Enumeration e = params.keys(); e.hasMoreElements();) {
				String key = (String) (e.nextElement());
				sb.append("&").append(key).append("=").append(
						URLUTF8Encoder.encode((String) params.get(key)));
			}
			sb.deleteCharAt(rootURL.length());	// delete the first amp sign before all parameters
		}
		return sb.toString();
	}	

	/**
	 * Prints the XML response resulted from a POST request to Last.fm API
	 * <b>One must remember to close the InputStream after calling this
	 * method.</b>
	 * 
	 * @param in
	 *            InputStream stream to the response
	 * @param len
	 *            byte length of the response
	 */
	public static void printRESTResponse(InputStream in, int len) throws IOException {
		if (len > 0) {
			int actual = 0;
			int bytesread = 0;
			byte[] data = new byte[len];
			while ((bytesread != len) && (actual != -1)) {
				actual = in.read(data, bytesread, len - bytesread);
				bytesread += actual;
			}
			System.out.println(new String(data));
		} else {
			int ch;
			while ((ch = in.read()) != -1) {
				System.out.print((char) ch);
			}
		}
	}
	
	/**
	 * Load properties from the given InputStream into a hashtable. One must
	 * remember to close the InputStream after calling this method.
	 * 
	 * @return a hashtable containing properties in name-value pairs
	 */
	public static Hashtable loadProperties(InputStream in) throws IOException {
		LineReader lr = new LineReader(in);
		Hashtable properties = new Hashtable();
		while (true) {
			try {
				String ln = new String(lr.readLine());
				int pos = ln.indexOf('=');
				String param = ln.substring(0, pos);
				String val = ln.substring(pos + 1);
				properties.put(param, val);
			} catch (EOFException e) {
				// At the end of file/InputStream
				break;
			}
		}
		return properties;
	}

	/**
	 * Output the debug message directly to the standard out. Along with the
	 * message, the name of the current thread is also printed.
	 * <p>
	 * Logging facilities can be added at convenience to provide exclusive
	 * logging information that's relevant to RadioStation.ForMe.
	 */
	public static void debug(String message) {
		System.out.println(Thread.currentThread().getName() + ": " + message);
	}

	/**
	 * Given the total number of seconds ,format the time into MM:SS where MM
	 * represents the number of minutes, SS represents the number of seconds.
	 * 
	 * @param timeInSec
	 *            time in seconds
	 * @return a string representation of time, in MM:SS format.
	 */
	public static String timeFromSeconds(int timeInSec) {
		int min = timeInSec / 60;
		int sec = timeInSec % 60;
		StringBuffer time = new StringBuffer();
		if (min < 10) {
			time.append("0");
		}
		time.append(min);
		time.append(":");
		if (sec < 10) {
			time.append("0");
		}
		time.append(sec);
		return time.toString();
	}

	/**
	 * Perform simple calculation of a base number raised to a certain power.
	 * <p>
	 * The base can be any integer. The power must be a positive integer. The
	 * result is returned as an integer.
	 */
	public static int simplePow(int base, int power) {
		if (power <= 0) {
			return 1;
		}
		else { 
			return base * simplePow(base, power - 1);
		}
	}

	/**
	 * Provides a method to encode any string into a URL-safe form. Non-ASCII
	 * characters are first encoded as sequences of two or three bytes, using
	 * the UTF-8 algorithm, before being encoded as %HH escapes.
	 * 
	 * Created: 17 April 1997 Author: Bert Bos <bert@w3.org>
	 * 
	 * URLUTF8Encoder: http://www.w3.org/International/URLUTF8Encoder.java
	 * 
	 * Copyright © 1997 World Wide Web Consortium, (Massachusetts Institute of
	 * Technology, European Research Consortium for Informatics and Mathematics,
	 * Keio University). All Rights Reserved. This work is distributed under the
	 * W3C® Software License [1] in the hope that it will be useful, but
	 * WITHOUT ANY WARRANTY; without even the implied warranty of
	 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
	 * 
	 * [1] http://www.w3.org/Consortium/Legal/2002/copyright-software-20021231
	 */
	public static class URLUTF8Encoder {

		final static String[] hex = { "%00", "%01", "%02", "%03", "%04", "%05",
				"%06", "%07", "%08", "%09", "%0a", "%0b", "%0c", "%0d", "%0e",
				"%0f", "%10", "%11", "%12", "%13", "%14", "%15", "%16", "%17",
				"%18", "%19", "%1a", "%1b", "%1c", "%1d", "%1e", "%1f", "%20",
				"%21", "%22", "%23", "%24", "%25", "%26", "%27", "%28", "%29",
				"%2a", "%2b", "%2c", "%2d", "%2e", "%2f", "%30", "%31", "%32",
				"%33", "%34", "%35", "%36", "%37", "%38", "%39", "%3a", "%3b",
				"%3c", "%3d", "%3e", "%3f", "%40", "%41", "%42", "%43", "%44",
				"%45", "%46", "%47", "%48", "%49", "%4a", "%4b", "%4c", "%4d",
				"%4e", "%4f", "%50", "%51", "%52", "%53", "%54", "%55", "%56",
				"%57", "%58", "%59", "%5a", "%5b", "%5c", "%5d", "%5e", "%5f",
				"%60", "%61", "%62", "%63", "%64", "%65", "%66", "%67", "%68",
				"%69", "%6a", "%6b", "%6c", "%6d", "%6e", "%6f", "%70", "%71",
				"%72", "%73", "%74", "%75", "%76", "%77", "%78", "%79", "%7a",
				"%7b", "%7c", "%7d", "%7e", "%7f", "%80", "%81", "%82", "%83",
				"%84", "%85", "%86", "%87", "%88", "%89", "%8a", "%8b", "%8c",
				"%8d", "%8e", "%8f", "%90", "%91", "%92", "%93", "%94", "%95",
				"%96", "%97", "%98", "%99", "%9a", "%9b", "%9c", "%9d", "%9e",
				"%9f", "%a0", "%a1", "%a2", "%a3", "%a4", "%a5", "%a6", "%a7",
				"%a8", "%a9", "%aa", "%ab", "%ac", "%ad", "%ae", "%af", "%b0",
				"%b1", "%b2", "%b3", "%b4", "%b5", "%b6", "%b7", "%b8", "%b9",
				"%ba", "%bb", "%bc", "%bd", "%be", "%bf", "%c0", "%c1", "%c2",
				"%c3", "%c4", "%c5", "%c6", "%c7", "%c8", "%c9", "%ca", "%cb",
				"%cc", "%cd", "%ce", "%cf", "%d0", "%d1", "%d2", "%d3", "%d4",
				"%d5", "%d6", "%d7", "%d8", "%d9", "%da", "%db", "%dc", "%dd",
				"%de", "%df", "%e0", "%e1", "%e2", "%e3", "%e4", "%e5", "%e6",
				"%e7", "%e8", "%e9", "%ea", "%eb", "%ec", "%ed", "%ee", "%ef",
				"%f0", "%f1", "%f2", "%f3", "%f4", "%f5", "%f6", "%f7", "%f8",
				"%f9", "%fa", "%fb", "%fc", "%fd", "%fe", "%ff" };

/**
	   * Encode a string to the "x-www-form-urlencoded" form, enhanced
	   * with the UTF-8-in-URL proposal. This is what happens:
	   *
	   * <ul>
	   * <li><p>The ASCII characters 'a' through 'z', 'A' through 'Z',
	   *        and '0' through '9' remain the same.
	   *
	   * <li><p>The unreserved characters - _ . ! ~ * ' ( ) remain the same.
	   *
	   * <li><p>The space character ' ' is converted into a plus sign '+'.
	   *
	   * <li><p>All other ASCII characters are converted into the
	   *        3-character string "%xy", where xy is
	   *        the two-digit hexadecimal representation of the character
	   *        code
	   *
	   * <li><p>All non-ASCII characters are encoded in two steps: first
	   *        to a sequence of 2 or 3 bytes, using the UTF-8 algorithm;
	   *        secondly each of these bytes is encoded as "%xx".
	   * </ul>
	   *
	   * @param s The string to be encoded
	   * @return The encoded string
	   */
		public static String encode(String s) {
			StringBuffer sbuf = new StringBuffer();
			int len = s.length();
			for (int i = 0; i < len; i++) {
				int ch = s.charAt(i);
				if ('A' <= ch && ch <= 'Z') { // 'A'..'Z'
					sbuf.append((char) ch);
				} else if ('a' <= ch && ch <= 'z') { // 'a'..'z'
					sbuf.append((char) ch);
				} else if ('0' <= ch && ch <= '9') { // '0'..'9'
					sbuf.append((char) ch);
				} else if (ch == ' ') { // space
					sbuf.append('+');
				} else if (ch == '-'
						|| ch == '_' // unreserved
						|| ch == '.' || ch == '!' || ch == '~' || ch == '*'
						|| ch == '\'' || ch == '(' || ch == ')') {
					sbuf.append((char) ch);
				} else if (ch <= 0x007f) { // other ASCII
					sbuf.append(hex[ch]);
				} else if (ch <= 0x07FF) { // non-ASCII <= 0x7FF
					sbuf.append(hex[0xc0 | (ch >> 6)]);
					sbuf.append(hex[0x80 | (ch & 0x3F)]);
				} else { // 0x7FF < ch <= 0xFFFF
					sbuf.append(hex[0xe0 | (ch >> 12)]);
					sbuf.append(hex[0x80 | ((ch >> 6) & 0x3F)]);
					sbuf.append(hex[0x80 | (ch & 0x3F)]);
				}
			}
			return sbuf.toString();
		}
	}
}
