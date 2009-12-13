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

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import javax.microedition.media.Manager;
import javax.microedition.media.MediaException;
import javax.microedition.media.Player;
import javax.microedition.media.PlayerListener;
import javax.microedition.media.control.VolumeControl;

import fm.radiostation.Playlist;
import fm.radiostation.RSFMUtils;
import fm.radiostation.Track;

/**
 * RadioPlayer controls defines audio playback behaviours, and provides an
 * interface for user to control volume, play/skip track or shut down radio.
 * 
 * @author kaiyi
 * 
 */
public class RadioPlayer implements PlayerListener {

	private Player player;
	private Playlist playlist;
	
	private int volume = -1;
	private int audioSourceConnectionFailures;
	
	/**
	 * Start the playing as soon as possible.
	 */
	public void play(Playlist playlist) throws MediaException {
		this.playlist = playlist;
		if (playlist == null) {
			throw new NullPointerException(
					"Trying to start RadioPlayer while playlist is null");
		}
		if (!playlist.getTracklist().isEmpty()) {
			Track tk = (Track) playlist.getTracklist().elementAt(0);
			RSFMUtils.debug("Current track: " + tk.getTitle() + " by: "
					+ tk.getCreator() + " duration: " + RSFMUtils.timeFromSeconds(tk.getDuration() / 1000));
			AudioDataSource ads = new AudioDataSource(tk.getLocation());
			try {
				player = Manager.createPlayer(ads);
			} catch (IOException e) {
				String responseCode = e.getMessage();
				RSFMUtils.debug("Exception while connecting to source, " +
						"with Response code: "+responseCode);
				if ("403".equals(responseCode)) {
					Player oldPlayer = player;
					playlist.getTracklist().removeElementAt(0);
					play(playlist);
					oldPlayer.close();
				} else if ("503".equals(responseCode)) {
					audioSourceConnectionFailures++;
					if (audioSourceConnectionFailures >= 3) {
						shutdown();
						audioSourceConnectionFailures = 0;
					}
					Player oldPlayer = player;
					play(playlist);
					oldPlayer.close();
				}
				return;
			} 
			player.addPlayerListener(this);
			player.start();
			VolumeControl contrl = (VolumeControl) player.getControl("VolumeControl");
			if (volume == -1) {
				volume = contrl.getLevel();
			} else {
				contrl.setLevel(volume);
			}
		} else {
			RadioPlayerEvent event = new RadioPlayerEvent(
					RadioPlayerEvent.OUT_OF_TRACKS);
			fireRadioPlayerEvent(event);
		}
	}

	/**
	 * Stop current track.
	 */
	public void stopCurrent() throws MediaException {
		if (player != null) {
			player.stop();
		}
	}

	/**
	 * Shut down radio. This release all of resources, detaches all listeners
	 * and empties tracklist
	 */
	public void shutdown() throws MediaException {
		if (player == null) {
			return;
		}
		// this is important to unregister event reception from player,
		// otherwise the radio player would go on forever.
		player.removePlayerListener(this);
		player.stop();
		Vector tracklist = playlist.getTracklist();
		playerStopped(player, tracklist);
		
		player.close();
		playlist.getTracklist().removeAllElements();
		fireRadioPlayerEvent(new RadioPlayerEvent(RadioPlayerEvent.RADIO_OFF));
	}

	public boolean incVolume() {
		if (player == null || player.getState() == Player.UNREALIZED) {
			return false;
		} else {
			VolumeControl contrl = (VolumeControl) player
					.getControl("VolumeControl");
			volume += 10;
			contrl.setLevel(volume);
			RSFMUtils.debug("Volume increased to: " + contrl.getLevel());
			return true;
		}
	}

	public boolean decVolume() {
		if (player == null || player.getState() == Player.UNREALIZED) {
			return false;
		} else {
			VolumeControl contrl = (VolumeControl) player
					.getControl("VolumeControl");
			volume -= 10;
			contrl.setLevel(volume);
			RSFMUtils.debug("Volume decreased to: " + contrl.getLevel());
			return true;
		}
	}

	public boolean isPlaying() {
		return player != null && player.getState() == Player.STARTED;
	}

	public void playerUpdate(Player player, final String event, Object eventData) {
		RSFMUtils.debug(event);
		if (PlayerListener.STARTED.equals(event)) {
			Vector tracklist = playlist.getTracklist();
			Track tk = (Track) tracklist.elementAt(0);
			int startTime = (int) (System.currentTimeMillis() / 1000);
			tk.setStartTime(startTime);
			RadioPlayerEvent evt = new RadioPlayerEvent(
					RadioPlayerEvent.TRACK_STARTED, tk);
			fireRadioPlayerEvent(evt);

		} else if (PlayerListener.STOPPED.equals(event)
				|| PlayerListener.END_OF_MEDIA.equals(event)) {
			Vector tracklist = playlist.getTracklist();
			playerStopped(player, tracklist);
			if (!tracklist.isEmpty()) {
				tracklist.removeElementAt(0);
				try {
					Player oldPlayer = player;
					play(playlist);
					oldPlayer.removePlayerListener(this);
					oldPlayer.close();
				} catch (Throwable e) {
					e.printStackTrace();
					RSFMUtils.debug("Unable to restart radio player.");
				}
			} else {
				throw new IllegalStateException(
						"Try to remove track from empty playlist after song ends");
			}
		} else if (PlayerListener.BUFFERING_STARTED.equals(event)) {
			RSFMUtils.debug("Buffering started, timeout watcher starting. Thread count="+Thread.activeCount());
			TimeoutWatcher tw = new TimeoutWatcher();
			tw.run();
		} else if (PlayerListener.DEVICE_UNAVAILABLE.equals(event)) {
			try {
				shutdown();
			} catch (MediaException e) {
				e.printStackTrace();
			}
		} else if (PlayerListener.ERROR.equals(event)) {
			RSFMUtils.debug("Error in running player, message: "
					+ eventData.toString());
			try {
				shutdown();
			} catch (MediaException e) {
				e.printStackTrace();
			}
		} 
	}

	private void playerStopped(Player player, Vector tracklist) {
		RadioPlayerEvent evt;
		RSFMUtils.debug("Media Stopped at: " + RSFMUtils.timeFromSeconds((int)(player.getMediaTime() / 1000000))
				+ " Duration is: " + RSFMUtils.timeFromSeconds((int)(player.getDuration() / 1000000)));
		Track tk = (Track) tracklist.elementAt(0);
		if (tk.getDuration() > 30
				&& (player.getMediaTime() >= (0.5 * player.getDuration()) || player
						.getMediaTime() >= 240000000)) {
			evt = new RadioPlayerEvent(RadioPlayerEvent.TRACK_STOPPED, tk);
		} else {
			evt = new RadioPlayerEvent(RadioPlayerEvent.TRACK_STOPPED);
		}
		fireRadioPlayerEvent(evt);
	}

	/*
	 * Event firing facilities for RadioPlayer.
	 */
	private Vector listeners = new Vector();

	public void addRadioPlayerEventListener(RadioPlayerEventListener rpel) {
		listeners.addElement(rpel);
	}

	public void removeRadioPlayerEventListener(RadioPlayerEventListener rpel) {
		listeners.removeElement(rpel);
	}

	private void fireRadioPlayerEvent(final RadioPlayerEvent event) {
		Thread th = new Thread() {
			public void run() {
				for (int i = listeners.size() - 1; i >= 0; i--) {
					RadioPlayerEventListener l = (RadioPlayerEventListener) listeners.elementAt(i);
					l.radioPlayerEventOccurred(event);
				}
			}
		};
		th.start();
	}

	public long getDuration() {
		return player.getDuration();
	}
	
	public long getCurrentTime() {
		return player.getMediaTime();
	}

	/**
	 * Observer class that prevents rare hangs while streaming media. When
	 * player is buffering, there may be some error in connection that prevents
	 * the player from resume to playing. After a limited number of seconds, an
	 * instance of this class will attempt stop the current track/dead
	 * connection, and start the next track.
	 * <p>
	 * Note that there is a bug with earlier BlackBerry devices (earlier than
	 * Bold), where player doesn't fire buffering start event. So this
	 * connection timeout facility will not work in those devices (BlackBerry
	 * 8xxx).
	 * 
	 * @author kaiyi
	 * 
	 */
	private class TimeoutWatcher implements PlayerListener{
		private static final int TIMEOUT_LIMIT = 30;
		private Timer timer;

		public void run() {
			timer = new Timer();
			player.addPlayerListener(this);
			timer.schedule(new TimeoutCounter(), TIMEOUT_LIMIT * 1000);
		}
		
		public void playerUpdate(Player player, String event, Object eventData) {
			if (PlayerListener.STOPPED.equals(event)
					|| PlayerListener.BUFFERING_STOPPED.equals(event)
					|| PlayerListener.END_OF_MEDIA.equals(event)
					|| PlayerListener.CLOSED.equals(event)
					|| PlayerListener.DEVICE_UNAVAILABLE.equals(event)) {
				player.removePlayerListener(this);
				timer.cancel();
			}
		}
		
		private class TimeoutCounter extends TimerTask {
			
			public void run() {
				try {
					stopCurrent();
				} catch (MediaException e) {
					e.printStackTrace();
					player.removePlayerListener(TimeoutWatcher.this);
					timer.cancel();
					RSFMUtils.debug("Timer cancelled");
				}
			}
		}
	}
}