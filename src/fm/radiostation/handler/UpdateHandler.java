/*
 * 2009-12-19
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
import java.util.Hashtable;

import fm.radiostation.RSFMUtils;
import fm.radiostation.ResponseObject;
import fm.radiostation.UpdateResponse;

public class UpdateHandler implements ResponseHandler {

	/**
	 * reads the version property file for latest version and update url
	 */
	public ResponseObject handle(InputStream in) {
		Hashtable properties;
		UpdateResponse response = null;
		try {
			properties = RSFMUtils.loadProperties(in);
			response = new UpdateResponse((String)properties.get("fm.radiostation.player_version"), 
					(String) properties.get("updateurl"));
		} catch (IOException e) {
			e.printStackTrace();
			RSFMUtils.debug("Cannot parse remote version information. "+e.getMessage(), this);
		}
		return response;
	}
}
