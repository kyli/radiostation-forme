/*
 * Feb 22, 2009
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

import fm.radiostation.Track;

/**
 * RadioPlayerEvent defines a set of higher level events fired by the RSFM
 * implementation of the Radio Player
 * <p>
 * Additionally, also adds events that more relevant to the use of
 * RadioStation.ForMe. Such as the notification of empty playlists.
 * 
 * @author kaiyi
 * 
 */
public class RadioPlayerEvent {

	/**
	 * Event occurs when the playlist is empty.
	 */
	public static final int OUT_OF_TRACKS = 1;
	/**
	 * Event occurs at the start of a new track.
	 */
	public static final int TRACK_STARTED = 2;
	/**
	 * Event occurs when a track stops playing, regardless whether it reached
	 * the end or stopped by the user.
	 */
	public static final int TRACK_STOPPED = 4;
	/**
	 * Event occurs when the radio is turned off.
	 */
	public static final int RADIO_OFF = 8;
	
	private int event;
	private Track track;
	
	/**
	 * Construct an event object given the event type.
	 * 
	 * @param event
	 *            one of the event types defined as a constant of this class
	 */
	public RadioPlayerEvent(int event) {
		this.event = event;
	}
	
	public RadioPlayerEvent(int event, Track track) {
		this.event = event;
		this.track = track;
	}

	public int getEvent() {
		return event;
	}

	public Track getTrack() {
		return track;
	}
}
