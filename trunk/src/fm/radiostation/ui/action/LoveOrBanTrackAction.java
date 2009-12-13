/*
 * Feb 28, 2009
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

package fm.radiostation.ui.action;

import fm.radiostation.Playlist;
import fm.radiostation.RSFMSession;
import fm.radiostation.Track;

public class LoveOrBanTrackAction extends AbstractRSFMAction {
	
	private static String LOVE_METHOD = "track.love";
	private static String BAN_METHOD = "track.ban";

	private final boolean isLove;

	public LoveOrBanTrackAction(RSFMSession session, boolean isLove) {
		super(session);
		this.isLove = isLove;
	}

	public void run() {
		Playlist playlist = session.getPlaylist();
		if (playlist == null || playlist.getTracklist().isEmpty()) {
			return;
		}
		Track track = (Track) playlist.getTracklist().elementAt(0);
		if (isLove) {
			track.setRating(Track.LOVE);
		} else {
			track.setRating(Track.BAN);
			session.stopCurrentTrack();
		}
		session.loveOrBanTrack(track, isLove ? LOVE_METHOD : BAN_METHOD);
	}

}
