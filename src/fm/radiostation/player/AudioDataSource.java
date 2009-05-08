/*
 * Feb 14, 2009
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
package fm.radiostation.player;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.io.Connector;
import javax.microedition.io.ContentConnection;
import javax.microedition.io.HttpConnection;
import javax.microedition.media.Control;
import javax.microedition.media.Player;
import javax.microedition.media.protocol.DataSource;
import javax.microedition.media.protocol.SourceStream;

import net.rim.device.api.io.SharedInputStream;
import fm.radiostation.ConnectionManager;
import fm.radiostation.RSFMUtils;
import fm.radiostation.Track;

/**
 * An instance of this class handles the details in both connecting to the
 * Last.fm data source and package the InputStream into AudioSourceStream, which
 * is read by the Player to stream audio data to the client.
 * <p>
 * The current implementation handles media data source similar to other Last.fm
 * clients. It does not store any audio metadata in local file system. It
 * connects directly to the remote data source and opens InputStream for direct
 * reading of audio data.
 * <p>
 * Similar to other Last.fm clients, this implementation does not allow random
 * seeking functionalities, but stop or skip the current track is allowed.
 * <p>
 * Initial http connection made to play.Last.fm is through the url found in
 * {@link Track#getLocation()}. The response code will be 302, indicating a http
 * redirection to one of its data servers. The location to the actual mp3 audio
 * resource is included in the response headers, with the property "Location".
 * <p>
 * Second data connection made to sN.last.fm, where N specifies the server it's
 * pointing to, is through the url found in
 * {@link HttpConnection#getHeaderField(String)}, using property "Location", as
 * result of the first http request. The input stream opened from this
 * connection points to the remote data source.
 * 
 * @see {@link AudioSourceStream}, {@link Player}
 * @author kaiyi
 * 
 */
public class AudioDataSource extends DataSource {
	
	private ContentConnection dataConnection;
	
	private InputStream readStream;
	private AudioSourceStream feedToPlayer;
	
	public AudioDataSource(String location){
		super(location);
	}

	/**
	 * Connect to the Last.fm remote data source and open the InputStream
	 * to be packaged into AudioSourceStream.
	 */
	public void connect() throws IOException {
		String iniRequestUrl = getLocator();//+";deviceside=true";
		iniRequestUrl = ConnectionManager.addRimUrlConnectionParam(iniRequestUrl);
		RSFMUtils.debug("Initial request is made to: " + iniRequestUrl);
		HttpConnection serverConnection = (HttpConnection) Connector.open(iniRequestUrl);
	    
	    int responseCode = serverConnection.getResponseCode();
	    RSFMUtils.debug("Initial request returned response code: "
				+ responseCode + " and response message: "
				+ serverConnection.getResponseMessage());
	    
	    if (responseCode == HttpConnection.HTTP_MOVED_TEMP) {
	    	String resourceLocation = serverConnection.getHeaderField("Location");
	    	resourceLocation = ConnectionManager.addRimUrlConnectionParam(resourceLocation);
	    	if (resourceLocation == null) {
	    		// track ticket expired, track not accessible
	    		RSFMUtils.debug("Remote resouce url not found in response header");
	    		throw new IOException("Unable to access remote mp3 resource");
			}
	    	serverConnection.close();
			dataConnection = (ContentConnection) Connector.open(resourceLocation);
		} else {
			RSFMUtils.debug("HttpConnection made to server returned unhandled HTTP response code: "
							+ responseCode);
			serverConnection.close();
			throw new IOException(Integer.toString(responseCode));
		}
	    
	    int length = (int) dataConnection.getLength();
		RSFMUtils.debug("Remote source data length is: " + length);
		readStream = SharedInputStream.getSharedInputStream(dataConnection
				.openInputStream());
		feedToPlayer = new AudioSourceStream(readStream, dataConnection
				.getType(), length);
	}

	/**
	 * Disconnect from the remote data source, and close/deallocate all
	 * occupied resources where necessary.
	 */
	public void disconnect() {
		try {
			if (readStream != null) {
				readStream.close();
				dataConnection.close();
			}
			feedToPlayer.close();
		} catch (Throwable e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
	}

	public String getContentType() {
		return feedToPlayer.getContentDescriptor().getContentType();
	}

	public SourceStream[] getStreams() {
		return new SourceStream[] { feedToPlayer };
	}

	public void start() throws IOException {
		return;
	}

	public void stop() throws IOException {
		return;
	}

	public Control getControl(String arg0) {
		return null;
	}

	public Control[] getControls() {
		return null;
	}
}
