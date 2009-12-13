/*
 * Generated on Dec 12, 2009. 
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

/**
 * this class provides the enumerated states in communication with last.fm web
 * services. it uses a similar implementation as enum is implemented in j2se.
 * 
 * @author kaiyi
 * 
 */
public class ServiceEvent {
	
	/**
	 * radio started playing
	 */
	public static final ServiceEvent RADIO_PLAYED = new ServiceEvent();
	/**
	 * radio stopped
	 */
	public static final ServiceEvent RADIO_STOPPED = new ServiceEvent();
	/**
	 * radio tuned to specified station
	 */
	public static final ServiceEvent RADIO_TUNED = new ServiceEvent();
	/**
	 * playlist successfully fetched
	 */
	public static final ServiceEvent PLAYLIST_FETCHED = new ServiceEvent();
	
	private ServiceEvent() {};
}
