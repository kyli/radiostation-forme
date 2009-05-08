/*
 * 2009-02-23
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
package fm.radiostation.handler;

import java.io.IOException;
import java.io.InputStream;

import net.rim.device.api.io.LineReader;

import fm.radiostation.ResponseObject;
import fm.radiostation.HandshakeResponse;
import fm.radiostation.RSFMUtils;

public class HandshakeResponseHandler implements ResponseHandler {

	public ResponseObject handle(InputStream in) {
		HandshakeResponse handshake = new HandshakeResponse();
		try {
			LineReader lr = new LineReader(in);
			String ln = new String(lr.readLine());
			if (!"OK".equals(ln)) {
				RSFMUtils.debug("Handshake to submission server returned: "+ln);
				handshake.setResponseMessage(ln);
				return handshake;
			} else {
				HandshakeResponse hr = handshake;
				ln = new String(lr.readLine());
				hr.setSessionID(ln);
				ln = new String(lr.readLine());
				hr.setNowPlayingUrl(ln);
				ln = new String(lr.readLine());
				hr.setSubmissionUrl(ln);
				hr.setSuccess(true);
				return hr;
			}
		} catch (IOException e) {
			e.printStackTrace();
			return handshake;
		}
	}

}
