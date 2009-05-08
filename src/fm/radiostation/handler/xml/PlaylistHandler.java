/*
 * Feb 12, 2009
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

import java.util.Vector;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import fm.radiostation.Playlist;
import fm.radiostation.Track;

public class PlaylistHandler extends RESTResponseHandler {
	
	private static String PLAYLIST = "playlist";
	private static String TRACKLIST = "trackList";
	private static String TITLE = "title";
	private static String CREATOR = "creator";
	private static String TRACK = "track";
	private static String LOCATION = "location";
	private static String ALBUM = "album";
	private static String DURATION = "duration";
	private static String IMAGE = "image";
	private static String TRACKAUTH = "trackauth";
	private static String BUYTRACK = "buyTrackURL";
	
	private Vector tracklist;
	private Track track;

	public void startElement(String uri, String localName, String name,
			Attributes attributes) throws SAXException {
		if (PLAYLIST.equals(name)) {
			apiResponseObject = new Playlist();
		} else if (TRACKLIST.equals(name)) {
			tracklist = new Vector();
		} else if (TRACK.equals(name)) {
			track = new Track();
		} 
	}
	
	public void endElement(String uri, String localName, String name)
			throws SAXException {
		Playlist playlist = (Playlist) apiResponseObject;
		String attr = getAttribute();
		if (track != null) {
			if (LOCATION.equals(name)) {
				track.setLocation(attr);
			} else if (TITLE.equals(name)) {
				track.setTitle(attr);
			} else if (ALBUM.equals(name)) {
				track.setAlbum(attr);
			} else if (CREATOR.equals(name)) {
				track.setCreator(attr);
			} else if (DURATION.equals(name)) {
				track.setDuration(Integer.parseInt(attr));
			} else if (IMAGE.equals(name)) {
				track.setImage(attr);
			} else if (TRACKAUTH.equals(name)) {
				track.setTrackauth(attr);
			} else if (BUYTRACK.equals(name)) {
				track.setTrackpurchase(attr);
			} else if (TRACK.equals(name)) {
				tracklist.addElement(track);
				track = null;
			}
		} else if (TRACKLIST.equals(name)) {
			playlist.setTracklist(tracklist);
			playlist.setSuccess(true);
			System.out.println("Creator: " + playlist.getCreator());
			System.out.println("Number of Songs: " + playlist.getTracklist().size());
		} else if (TITLE.equals(name)) {
			playlist.setTitle(attr);
		} else if (CREATOR.equals(name)) {
			playlist.setCreator(attr);
		}
	}
	
}
