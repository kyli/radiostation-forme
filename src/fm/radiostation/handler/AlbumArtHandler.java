/*
 * 2009-02-26
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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import net.rim.device.api.system.EncodedImage;
import fm.radiostation.RSFMUtils;
import fm.radiostation.ResponseObject;

public class AlbumArtHandler implements ResponseHandler {

	private EncodedImage image;

	public ResponseObject handle(InputStream in) {
		int CHUNK_SIZE = 1024;
		byte[] temp = new byte[ CHUNK_SIZE ];
		ByteArrayOutputStream baos = new ByteArrayOutputStream ();
		byte[] result;
		try {
			for( ;; ) {
			    int bytesRead = in.read( temp );
			    if( bytesRead == -1 ) {
			        break;
			    }
			    baos.write(temp,0,bytesRead);
			}
			baos.flush();
			result = baos.toByteArray();
			baos.close();
			image = EncodedImage.createEncodedImage(result, 0, result.length);
			ResponseObject response = new ResponseObject();
			response.setSuccess(true);
			return response;
		} catch (IOException e) {
			e.printStackTrace();
			RSFMUtils.debug("Exception while reading image data.");
			return new ResponseObject();
		}
	}
	
	public EncodedImage getImage() {
		return image;
	}

}
