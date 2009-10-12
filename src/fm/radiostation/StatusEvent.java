/*
 * Feb 25, 2009
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

public class StatusEvent {
	
	//TODO Need to be factored into messages.rrh because they are visible to the user
	public static final String CONNECTION_ERROR = "Connection Error";
	public static final String GETTING_MOBILE_SESSION = "Getting Mobile Session";
	public static final String PERFORMING_HANDSHAKE = "Performing Handshake";
	public static final String HANDSHAKE_SUCCESSFUL = "Handshake Successful";
	public static final String CONNECTING_TO_RADIO = "Connecting To Radio";
	public static final String TUNED_TO = "Tuned to";
	public static final String LISTENING_TO = "Listening to";
	public static final String FETCHING_PLAYLIST = "Fetching Playlist";
	public static final String USER_PROFILE_UPDATED = "User Profile Updated";
	
	public static final String USER_INFO_UNAVAIL = "User Information Unavailable";
	public static final String INCORRECT_USNM_PSWD = "Please Revise Username or Password";
	public static final String INCORRECT_SYS_TIME = "System Clock Needs to be Corrected";
	public static final String CLT_ID_BANNED = "Client Banned, Please Get the Latest Software Updates";
	public static final String SUBSCRIBER_ONLY = "Subscriber only. Access denied.";
	public static final String USER_INFO_CLEARED = "User Data Cleared";
	
	private String status;
	
	public StatusEvent(String status) {
		this.status = status;
	}
	
	public String getStatus() {
		return status;
	}
}
