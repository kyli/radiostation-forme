/*
 * Generated on Feb 3, 2009. 
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

package fm.radiostation;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.microedition.io.HttpConnection;
import javax.microedition.media.MediaException;

import net.rim.blackberry.api.browser.URLEncodedPostData;
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.PersistentStore;
import net.rim.device.api.util.Persistable;
import net.rim.device.api.util.StringUtilities;
import fm.radiostation.RSFMUtils.URLUTF8Encoder;
import fm.radiostation.handler.AlbumArtHandler;
import fm.radiostation.handler.HandshakeResponseHandler;
import fm.radiostation.handler.NowPlayingResponseHandler;
import fm.radiostation.handler.SubmissionResponseHandler;
import fm.radiostation.handler.xml.MobileSessionHandler;
import fm.radiostation.handler.xml.PlaylistHandler;
import fm.radiostation.handler.xml.SimpleResponseHandler;
import fm.radiostation.handler.xml.TuneResponseHandler;
import fm.radiostation.player.RadioPlayer;
import fm.radiostation.player.RadioPlayerEvent;
import fm.radiostation.player.RadioPlayerEventListener;

/**
 * The RadioStation.ForMe session represents a single user's interaction with
 * both the application and the Last.fm API. It contains settings, user
 * information and session keys received from successful authentication to the
 * Last.fm API. This is also the place where everything bind and work together.
 * 
 * @author kaiyi
 * 
 */
public class RSFMSession implements RadioPlayerEventListener {
	
	/**
	 * client identifier that identifies RadioStation.ForMe with last.fm
	 */
	private static final String CLIENT_ID = "rsf";
	
	/*
	 * key to access persistent store. these keys are private keys and are
	 * not provided as a part of the source code release
	 */
	private static final String api_key;
	private static final String secret;
	private static final String settingsKey;
	private static final String sessionsKey;
	private Vector queuedSubmittingTracks;
	
	/*
	 * load persistent access keys from file
	 */
	static {
		try {
			Hashtable ht = RSFMUtils.loadProperties(RSFMSession.class
					.getResourceAsStream("/rsfm.keys"));
			api_key = (String) ht.get("apiKey");
			secret = (String) ht.get("secret");
			settingsKey = (String) ht.get("settingKey");
			sessionsKey = (String) ht.get("sessionsKey");
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Error: Unable to load rsfm.keys.");
		}
	}

	/*
	 * static user session data
	 */
	private String username;
	private String password;
	private int failureCounter;
	private long lastFailTime;
	
	/*
	 * dynamic session data 
	 */
	private Radio radio;
	private Playlist playlist;
	private MobileSession mobileSession;
	private HandshakeResponse handshake;
	private String authToken;
	private String station;
	
	/*
	 * modules for http connection and streamed content
	 */
	private ConnectionManager connMan;
	private RadioPlayer radioPlayer;
	
	public RSFMSession() {
		connMan = new ConnectionManager();
		radioPlayer = new RadioPlayer();
		queuedSubmittingTracks = new Vector();
	}

	/**
	 * Retrieves user settings from persistent layer.
	 */
	public void loadSettings() {
		PersistentObject store = PersistentStore.getPersistentObject(StringUtilities
				.stringHashToLong(settingsKey));
		synchronized (store) {
			RSFMHashMap currentinfo = (RSFMHashMap) store.getContents();
			if (currentinfo == null) {
				fireStatusEvent(new StatusEvent(StatusEvent.USER_INFO_UNAVAIL));
			} else {
				username = (String) currentinfo.get("username");
				password = (String) currentinfo.get("password");
				Boolean useWifi = (Boolean) currentinfo.get("useWifi");
				if (useWifi == null) {
					RSFMUtils.debug("useWifi setting not found, default to true and use wifi when possible.");
					UrlFactory.forceWifi = false;
				} else {
					UrlFactory.forceWifi = useWifi.booleanValue();
				}
			}
		}
	}
	
	/**
	 * Saves user settings to persistent layer.
	 */
	public void saveSettings(String username, String password) {
		if (queuedSubmittingTracks == null) {
			return;
		}
		PersistentObject store = PersistentStore.getPersistentObject(StringUtilities
				.stringHashToLong(settingsKey));
		RSFMHashMap userinfo = new RSFMHashMap(3);
		userinfo.put("username", username);
		userinfo.put("password", password);
		userinfo.put("useWifi", new Boolean(UrlFactory.forceWifi));
		synchronized(store) {
			store.setContents(userinfo); 
			store.commit();
		}
	}
	
	/**
	 * loads session data from persistent store
	 */
	public void loadSession() {
		PersistentObject store = PersistentStore.getPersistentObject(StringUtilities
				.stringHashToLong(sessionsKey));
		synchronized (store) {
			RSFMHashMap currentinfo = (RSFMHashMap) store.getContents();
			if (currentinfo != null) {
				station = (String) currentinfo.get("station");
				if (station == null) {
					station = Radio.DEFAULT_STATION;
				}
				queuedSubmittingTracks = (Vector) currentinfo.get("queuedtracks");
				if (queuedSubmittingTracks == null) {
					queuedSubmittingTracks = new Vector();
				}
			} else {
				station = Radio.DEFAULT_STATION;
			}
		}
	}
	
	/**
	 * saves session data to persistent store
	 */
	public void saveSession() {
		PersistentObject store = PersistentStore.getPersistentObject(StringUtilities
				.stringHashToLong(sessionsKey));
		RSFMHashMap userinfo = new RSFMHashMap(2);
		userinfo.put("station", station);
		userinfo.put("queuedtracks", queuedSubmittingTracks);
		synchronized(store) {
			store.setContents(userinfo); 
			store.commit();
		}
	}
	
	/**
	 * Delete all stored data from persistence layer
	 */
	public void cleanup() {
		username = null;
		password = null;
		station = Radio.DEFAULT_STATION;
		queuedSubmittingTracks.removeAllElements();
		PersistentStore.destroyPersistentObject(StringUtilities
				.stringHashToLong(settingsKey));
		PersistentStore.destroyPersistentObject(StringUtilities
				.stringHashToLong(sessionsKey));
		fireStatusEvent(new StatusEvent(StatusEvent.USER_INFO_CLEARED));
	}

	/**
	 * Authenticate through <a href="http://www.last.fm/api/mobileauth">Last.fm
	 * Authentication API</a> and create the mobile user session.
	 * <p>
	 * If either username or password information is unavailable, return
	 * immediatelly. When mobile session is successfully created,
	 * {@link ServiceEvent#SESSION_ACQUIRED} is fired. An observer should be
	 * registered as a {@link ServiceEventListener} if wish to be notified when
	 * mobile session is successfuly created.
	 */
	public void fetchMobileSession() {
		if (username == null || password == null) {
			return;
		}
		authToken = RSFMUtils.md5(username + RSFMUtils.md5(password));

		Vector paramList = new Vector();
		paramList.addElement("api_key" + api_key);
		paramList.addElement("authToken" + authToken);
		paramList.addElement("method" + MobileSession.METHOD);
		paramList.addElement("username" + URLUTF8Encoder.encode(username));
		String api_sig = RSFMUtils.createApiSignature(paramList, secret);

		Hashtable params = new Hashtable();
		params.put("username", username);
		params.put("authToken", authToken);
		params.put("api_sig", api_sig);
		fireStatusEvent(new StatusEvent(StatusEvent.GETTING_MOBILE_SESSION));

		MobileSessionHandler handler = new MobileSessionHandler();
		mobileSession = (MobileSession) connMan.getXMLResponse(api_key,
				MobileSession.METHOD, params, handler,
				HttpConnection.POST);
		if (mobileSession != null && mobileSession.isSuccess()) {
			fireWebServiceEvent(ServiceEvent.SESSION_ACQUIRED);
		} 
	}
	
	/**
	 * Perform handshake with the submission API.
	 * <p>
	 * if user information is unavailable or previous failure does not permit
	 * immediate request, then call to this method is ignored. if handshake is
	 * successful, then {@link ServiceEvent#HANDSHAKE_COMPLETED} and
	 * {@link StatusEvent#HANDSHAKE_SUCCESSFUL} are fired to notify the service
	 * and ui layers respectively.
	 */
	public void handshake() {
		if (username == null || password == null) {
			return;
		} else if (failureCounter > 0) {
			if (System.currentTimeMillis() < lastFailTime
					+ RSFMUtils.simplePow(2, failureCounter) * 60000) {
				return;
			}
		}
		String timestamp = Long.toString(System.currentTimeMillis() / 1000);
		String authToken = RSFMUtils.md5(RSFMUtils.md5(password)+timestamp);
		
		Hashtable params = new Hashtable(5);
		params.put("c", CLIENT_ID);
		params.put("v", Version.PLAYER_VERSION);
		params.put("u", username);
		params.put("t", timestamp);
		params.put("a", authToken);

		fireStatusEvent(new StatusEvent(StatusEvent.PERFORMING_HANDSHAKE));
		
		HandshakeResponseHandler handler = new HandshakeResponseHandler();
		String url = RSFMUtils.buildURL(ConnectionManager.SUBMISSION_ROOT_URL
				+ ConnectionManager.SUBMISSION_HANDSHAKE, params);
		handshake = (HandshakeResponse) connMan.getResponse(url, handler,
				null);
		
		if (handshake != null && handshake.isSuccess()) {
			failureCounter = 0;
			fireWebServiceEvent(ServiceEvent.HANDSHAKE_COMPLETED);
			fireStatusEvent(new StatusEvent(StatusEvent.HANDSHAKE_SUCCESSFUL));
		} else {
			if (handshake != null) { 
				handleFailure(handshake);
			} else {
				fireStatusEvent(new StatusEvent(StatusEvent.CONNECTION_ERROR));
			}
		}
	}

	/**
	 * handles failure result from communication with lastfm api.
	 * 
	 * @param response response describes failure
	 */
	private void handleFailure(VerboseResponse response) {
		if (response.getResponseMessage() == null) {
			fireStatusEvent(new StatusEvent(StatusEvent.CONNECTION_ERROR));
		} else if ("BADAUTH".equals(response.getResponseMessage())) {
			username = null;
			password = null;
			fireStatusEvent(new StatusEvent(StatusEvent.INCORRECT_USNM_PSWD));
		} else if ("BADTIME".equals(response.getResponseMessage())) {
			fireStatusEvent(new StatusEvent(StatusEvent.INCORRECT_SYS_TIME));
		} else if ("BANNED".equals(response.getResponseMessage())) {
			fireStatusEvent(new StatusEvent(StatusEvent.CLT_ID_BANNED));
		} else if ("BADSESSION".equals(response.getResponseMessage())) {
			handshake();
		} else {
			failureCounter++;
			lastFailTime = System.currentTimeMillis();
			fireStatusEvent(new StatusEvent(response.getResponseMessage()));
		}
	}
	
	/**
	 * Updates nowPlaying information
	 * <p>
	 * if handshake has not yet performed, then request is ignored. if the request
	 * fails, then handshake is performed once again.
	 */
	public void nowPlaying(Track tk) {
		if (handshake != null) {
			Hashtable params = new Hashtable(7);
			params.put("s", handshake.getSessionID());
			params.put("a", tk.getCreator());
			params.put("t", tk.getTitle());
			params.put("b", tk.getAlbum() == null ? "" : tk.getAlbum());
			params.put("l", tk.getDuration() == -1 ? "" : Integer.toString(tk.getDuration()));
			params.put("n", "");
			params.put("m", "");
			String url = handshake.getNowPlayingUrl();
			Enumeration e = params.keys();
			URLEncodedPostData postdata = new URLEncodedPostData(URLEncodedPostData.DEFAULT_CHARSET, false);
			while (e.hasMoreElements()) {
				String key = (String) e.nextElement();
				postdata.append(key, (String) params.get(key));
			}
			NowPlayingResponseHandler handler = new NowPlayingResponseHandler();
			ResponseObject nowPlayingResponse = connMan.getResponse(url,
					handler, postdata.getBytes()); 
			if (nowPlayingResponse == null || !nowPlayingResponse.isSuccess()) {
				handshake();
			} 
		} 
	}
	
	/**
	 * submits listened track information to last.fm
	 * <p>
	 * if handshake is not yet performed, then attempts to handshake instead.
	 */
	public void submission(Track tk) {
		queuedSubmittingTracks.addElement(tk);
		if (handshake != null) {
			Hashtable params = new Hashtable(10);
			params.put("s", handshake.getSessionID());
			for (int i = 0; i < queuedSubmittingTracks.size(); i++) {
				Track track = (Track) queuedSubmittingTracks.elementAt(i);
				params.put("a["+i+"]", track.getCreator());
				params.put("t["+i+"]", track.getTitle());
				params.put("i["+i+"]", Integer.toString(track.getStartTime()));
				params.put("o["+i+"]", "L"+track.getTrackauth());
				params.put("r["+i+"]", (track.getRating() == null) ? "" : track.getRating());
				params.put("l["+i+"]", Integer.toString(track.getDuration()));
				params.put("b["+i+"]", track.getAlbum() == null ? "" : track.getAlbum());
				params.put("n["+i+"]", "");
				params.put("m["+i+"]", "");
			}
			
			String url = handshake.getSubmissionUrl();
			Enumeration e = params.keys();
			URLEncodedPostData postdata = new URLEncodedPostData(URLEncodedPostData.DEFAULT_CHARSET, false);
			while (e.hasMoreElements()) {
				String key = (String) e.nextElement();
				postdata.append(key, (String) params.get(key));
			}
			SubmissionResponseHandler handler = new SubmissionResponseHandler();
			VerboseResponse submissionResponse = (VerboseResponse) connMan.getResponse(url,
					handler, postdata.getBytes()); 
			if (submissionResponse != null && submissionResponse.isSuccess()) {
				queuedSubmittingTracks.removeAllElements();
			} else {
				handleFailure(submissionResponse);
			}
		} else {
			handshake();
		}
	}

	/**
	 * tune to specified station
	 * <p>
	 * if user is not permitted to access radio (i.e. not subscribed) or session has
	 * not yet been created, then the request to this method is ignored. upon success,
	 * {@link ServiceEvent#RADIO_TUNED} is fired.
	 */
	public void tune(String station) {
		if (!mobileSession.isSubscriber()) {
			fireStatusEvent(new StatusEvent(StatusEvent.SUBSCRIBER_ONLY));
			return;
		}
		else if (mobileSession == null) {
			fetchMobileSession();
			return;
		}
		Vector paramList = new Vector();
		paramList.addElement("api_key" + api_key);
		paramList.addElement("lang" + Version.LANGUAGE);
		paramList.addElement("method" + Radio.METHOD_RADIO_TUNE);
		paramList.addElement("sk" + mobileSession.getSk());
		paramList.addElement("station" + station);
		String api_sig = RSFMUtils.createApiSignature(paramList, secret);

		Hashtable params = new Hashtable();
		params.put("lang", Version.LANGUAGE);
		params.put("station", station);
		params.put("sk", mobileSession.getSk());
		params.put("api_sig", api_sig);

		fireStatusEvent(new StatusEvent(StatusEvent.CONNECTING_TO_RADIO));
		TuneResponseHandler handler = new TuneResponseHandler();
		radio = (Radio) connMan.getXMLResponse(api_key,
				Radio.METHOD_RADIO_TUNE, params, handler,
				HttpConnection.POST);
		this.station = station;
		if (radio != null && radio.isSuccess()) {
			fireStatusEvent(new StatusEvent(StatusEvent.TUNED_TO + " "
					+ radio.getName()));
			// Remove all tracks on playlist after the current track,
			// because the player has tuned the radio
			if (playlist != null) {
				for (int i = 1; i < playlist.getTracklist().size(); i++) {
					playlist.getTracklist().removeElementAt(i);
				}
			}
			stopCurrentTrack();
			fireWebServiceEvent(ServiceEvent.RADIO_TUNED);
		}
	}

	/**
	 * fetch playlist from last.fm
	 * <p>
	 * if user is not permitted to access radio (i.e. not subscribed) or radio has
	 * not yet been initialized, then the request to this method is ignored. upon success,
	 * {@link ServiceEvent#PLAYLIST_FETCHED} is fired.
	 */
	public void fetchPlayList() {
		if (!mobileSession.isSubscriber()) {
			fireStatusEvent(new StatusEvent(StatusEvent.SUBSCRIBER_ONLY));
			return;
		}
		if (radio == null) {
			tune(station);
			return;
		}
		fireStatusEvent(new StatusEvent(StatusEvent.FETCHING_PLAYLIST));
		
		Vector paramList = new Vector();
		paramList.addElement("api_key" + api_key);
		paramList.addElement("discovery" + (radio.isSupportdiscovery() ? "1" : "0"));
		paramList.addElement("method" + Radio.METHOD_RADIO_GETPLAYLIST);
		paramList.addElement("rtp" + "1");
		paramList.addElement("sk" + mobileSession.getSk());
		String api_sig = RSFMUtils.createApiSignature(paramList, secret);
		
		Hashtable params = new Hashtable();
		params.put("discovery", (radio.isSupportdiscovery() ? "1" : "0"));
		params.put("rtp", "1");
		params.put("sk", mobileSession.getSk());
		params.put("api_sig", api_sig);
		
		PlaylistHandler handler = new PlaylistHandler();
		playlist = (Playlist) connMan.getXMLResponse(api_key,
				Radio.METHOD_RADIO_GETPLAYLIST, params, handler,
				HttpConnection.POST);
		if (playlist != null) {
			fireWebServiceEvent(ServiceEvent.PLAYLIST_FETCHED);
		}
	}

	/**
	 * play radio as soon as possible
	 * <p>
	 * if user is not permitted to access radio (i.e. not subscribed) or
	 * playlist is not available, then the request to this method is ignored.
	 */
	public void playRadio() {
		if (!mobileSession.isSubscriber()) {
			fireStatusEvent(new StatusEvent(StatusEvent.SUBSCRIBER_ONLY));
			return;
		}
		if (playlist == null) {
			return;
		}
		radioPlayer.addRadioPlayerEventListener(this);
		try {
			radioPlayer.play(playlist);
		} catch (Exception e) {
			e.printStackTrace();
			RSFMUtils.debug("Exception when trying to play radio: "+e.getMessage(), this);
		} 
	}
	
	/**
	 * tells radio player to stop current track
	 * <p>
	 * if the radio is not playing, then request is ignored.
	 * 
	 * @see RadioPlayer#stopCurrent()
	 */
	public void stopCurrentTrack() {
		if (!radioPlayer.isPlaying()) {
			return;
		}
		if (radioPlayer != null) {
			try {
				radioPlayer.stopCurrent();
			} catch (MediaException e) {
				e.printStackTrace();
				RSFMUtils.debug("Exception when trying to stop current track: "+e.getMessage(), this);
			} 
		} else {
			RSFMUtils.debug("Attempt to stop radio when there is no radio.");
			throw new IllegalStateException(
					"Attempt to stop radio where radio is null");
		}
	}
	
	/**
	 * attempts to shut down radio through radioplayer. if radio is off, then
	 * calls to this method returns immediatelly.
	 * 
	 * @see RadioPlayer#shutdown()
	 */
	public void shutdownRadio() {
		if (!radioPlayer.isPlaying()) {
			return;
		}
		if (radioPlayer != null) {
			try {
				radioPlayer.shutdown();
			} catch (Throwable e) {
				e.printStackTrace();
				RSFMUtils.debug("Exception when trying to shutdown radio: "+e.getMessage(), this);
			} 
		} else {
			throw new IllegalStateException("Attempt to turn off radio where radio is null");
		}
	}
	
	public EncodedImage fetchAlbumArt(String url) {
		AlbumArtHandler handler = new AlbumArtHandler();
		ResponseObject response = connMan.getResponse(url, handler, null);
		if (response != null && response.isSuccess()) {
			return handler.getImage();
		} else {
			return null;
		}
	}

	/**
	 * submits request to indicate love or ban a track
	 */
	public void loveOrBanTrack(Track track, String method) {
		String title = track.getTitle();
		String creator = track.getCreator();
		String sk = mobileSession.getSk();
		
		Vector paramList = new Vector();
		paramList.addElement("api_key" + api_key);
		paramList.addElement("artist" + creator);
		paramList.addElement("method" + method);
		paramList.addElement("sk" + sk);
		paramList.addElement("track" + title);
		
		Hashtable params = new Hashtable(4);
		params.put("track", title);
		params.put("artist", creator);
		params.put("api_sig", RSFMUtils.createApiSignature(paramList, secret));
		params.put("sk", sk);
		
		SimpleResponseHandler handler = new SimpleResponseHandler();
		ResponseObject response = connMan.getXMLResponse(api_key, method, params, handler, HttpConnection.POST);
		if (response != null && response.isSuccess()) {
			fireStatusEvent(new StatusEvent(StatusEvent.USER_PROFILE_UPDATED));
		} else {
			fireStatusEvent(new StatusEvent(StatusEvent.CONNECTION_ERROR));
		}
	}
	
	/**
	 * handles events fired by the rsfm radio player, including
	 * {@link RadioPlayerEvent#OUT_OF_TRACKS}, {@link RadioPlayerEvent#TRACK_STARTED}
	 * and {@link RadioPlayerEvent#TRACK_STOPPED}
	 */
	public void radioPlayerEventOccurred(final RadioPlayerEvent event) {
		if (event.getEvent() == RadioPlayerEvent.OUT_OF_TRACKS) {
			fetchPlayList();
			playlist = getPlaylist();
			try {
				radioPlayer.play(playlist);
			} catch (Throwable e) {
				e.printStackTrace();
			}
		} else if (event.getEvent() == RadioPlayerEvent.TRACK_STARTED) {
			if (event.getTrack() != null) {
				Track tk = event.getTrack();
				nowPlaying(tk);
			}
			fireStatusEvent(new StatusEvent(StatusEvent.LISTENING_TO + " "
					+ radio.getName()));
		} else if (event.getEvent() == RadioPlayerEvent.TRACK_STOPPED) {
			Track tk = event.getTrack();
			if (tk != null) {
				submission(tk);
			}
			fireStatusEvent(new StatusEvent(""));
		} else if (event.getEvent() == RadioPlayerEvent.RADIO_OFF) {
			radioPlayer.removeRadioPlayerEventListener(this);
			fireWebServiceEvent(ServiceEvent.RADIO_STOPPED);
		}
	} 

	/*
	 * web service event facilities
	 */
	private final Vector webserviceEventListeners = new Vector();
	
	public void addWebServiceListener(ServiceEventListener l) {
		webserviceEventListeners.addElement(l);
	}
	
	public void removeWebServiceListener(ServiceEventListener l) {
		webserviceEventListeners.removeElement(l);
	}
	
	private void fireWebServiceEvent(ServiceEvent event) {
		for (int i = webserviceEventListeners.size() - 1; i >= 0; i--) {
			((ServiceEventListener) webserviceEventListeners.elementAt(i))
					.serviceStateChanged(event);
		}
	}
	
	/*
	 * status event facilities
	 */
	private final Vector statusEventListeners = new Vector();

	public void addStatusEventListener(StatusEventListener sel) {
		statusEventListeners.addElement(sel);
	}
	
	public void removeStatusEventListener(StatusEventListener sel) {
		statusEventListeners.removeElement(sel);
	}
	
	private void fireStatusEvent(StatusEvent event) {
		for (int i = statusEventListeners.size() - 1; i >= 0; i--) {
			((StatusEventListener) statusEventListeners.elementAt(i))
					.statusChanged(event);
		}
	}
	
	/*
	 * uninteresting getters
	 */
	public Playlist getPlaylist() {
		return playlist;
	}

	public Radio getRadio() {
		return radio;
	}
	
	public RadioPlayer getRadioPlayer() {
		return radioPlayer;
	}
	
	public String getUsername() {
		return username;
	}
	
	public String getPassword() {
		return password;
	}
	
	public String getPreviousStation() {
		return station;
	}

	/**
	 * Custom extension of hashtable that is used to store user settings. The advantage of
	 * using such an extension is that it will be removed after the application
	 * gets uninstalled.
	 */
	private static class RSFMHashMap extends Hashtable implements Persistable {
		public RSFMHashMap() {}
		public RSFMHashMap(int initCap) {
			super(initCap);
		}
	}
}