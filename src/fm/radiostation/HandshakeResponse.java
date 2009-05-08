/*
 * 2009-02-23
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
package fm.radiostation;

public class HandshakeResponse extends VerboseResponse {

	/**
	 * the scrobble session id, to be used in all following now-playing and
	 * submission requests.
	 */
	private String sessionID;
	
	/**
	 * the URL that should be used for a now-playing request. 
	 */
	private String nowPlayingUrl;
	
	/**
	 * the URL that should be used for submissions.
	 */
	private String submissionUrl;
	
	public void setSessionID(String sessionID) {
		this.sessionID = sessionID;
	}
	public String getSessionID() {
		return sessionID;
	}
	public void setNowPlayingUrl(String nowPlayingUrl) {
		this.nowPlayingUrl = nowPlayingUrl;
	}
	public String getNowPlayingUrl() {
		return nowPlayingUrl;
	}
	public void setSubmissionUrl(String submissionUrl) {
		this.submissionUrl = submissionUrl;
	}
	public String getSubmissionUrl() {
		return submissionUrl;
	}
}
