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
import fm.radiostation.ServiceEvent;
import fm.radiostation.ServiceEventListener;
import fm.radiostation.player.RadioPlayer;

/**
 * the activation of this action effectively starts or stops the radio depending
 * on the current application service state.
 * <p>
 * if the radio is playing, then it is stopped. otherwise, it will start to play
 * as soon as all necessary resources are aquired.
 * 
 * @author kaiyi
 * 
 */
public class PlayOrStopAction extends AbstractRSFMAction implements
		ServiceEventListener {

	/**
	 * instance-controlled runner that is capable of starting the radio
	 */
	private RadioRunner runner;

	public PlayOrStopAction(RSFMSession session) {
		super(session);
		session.addWebServiceListener(this);
	}

	/**
	 * attempts to start or stop the radio depending on service state
	 */
	public void run() {
		RadioPlayer player = session.getRadioPlayer();
		if (player == null || !player.isPlaying()) {
			if (runner == null) {
				runner = new RadioRunner();
				runner.start();
			}
		} else {
			session.shutdownRadio();
		}
	}

	/**
	 * listens for web services state changes.
	 * <p>
	 * clear the runner so that it can be constructed again to start the radio
	 */
	public void serviceStateChanged(ServiceEvent event) {
		if (event == ServiceEvent.RADIO_STOPPED) {
			session.removeWebServiceListener(runner);
			runner = null;
		}
	}

	/**
	 * runner thread that responsible of starting the radio. if any of the
	 * resources needed to start the radio (i.e. radio object, playlist) is not
	 * ready, it is acquired before the radio starts.
	 * 
	 * @author kaiyi
	 * 
	 */
	private class RadioRunner extends Thread implements
			ServiceEventListener {
		
		public void run() {
			session.addWebServiceListener(runner);
			if (session.getRadio() == null) {
				session.tune(session.getPreviousStation());
			} else {
				Playlist playlist = session.getPlaylist();
				if (playlist == null || playlist.getTracklist().isEmpty()) {
					session.fetchPlayList();
				}
			}
		}

		/**
		 * listens for service changes. plays radio when all necessary resources
		 * are acquired.
		 */
		public void serviceStateChanged(ServiceEvent event) {
			if (event == ServiceEvent.RADIO_TUNED) {
				Playlist playlist = session.getPlaylist();
				if (playlist == null || playlist.getTracklist().isEmpty()) {
					session.fetchPlayList();
				}
			} else if (event == ServiceEvent.PLAYLIST_FETCHED) {
				session.removeWebServiceListener(this);
				session.playRadio();
			}
		}
	}
}