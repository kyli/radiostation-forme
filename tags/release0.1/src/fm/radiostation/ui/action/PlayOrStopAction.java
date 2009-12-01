/*
 * Generated on Feb 1, 2009. 
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
import fm.radiostation.Radio;
import fm.radiostation.player.RadioPlayer;

public class PlayOrStopAction extends AbstractRSFMAction {

	// small hack prevents multiple threads from spawning and invoke run() many times.
	private boolean isActive;

	public PlayOrStopAction(RSFMSession rsfmSession) {
		super(rsfmSession);
	}

	public void run() {
		RadioPlayer player = rsfmSession.getRadioPlayer();
		if (player == null || !player.isPlaying()) {
			if (isActive) {
				return;
			} else {
				isActive = true;
			}
			Thread th = new Thread() {
				public void run() {
					try {
						play();
					} finally {
						isActive = false;
					}
				}
			};
			th.start();
		} else {
			stop();
		}
	}

	private void play() {
		boolean success = false;
		if (rsfmSession.getRadio() == null) {
			success = rsfmSession.tune(Radio.DEFAULT_STATION);
		} else {
			success = true;
		}
		if (success) {
			Playlist playlist = rsfmSession.getPlaylist();
			if (playlist == null) {
				success = rsfmSession.fetchPlayList();
			} else if (playlist.getTracklist().isEmpty()) {
				success = rsfmSession.fetchPlayList();
			}
			if (success) {
				rsfmSession.playRadio();
			}
		}
	}

	private void stop() {
		rsfmSession.shutdownRadio();
	}
}