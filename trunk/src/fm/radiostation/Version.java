/*
 * April 18, 2009
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

import java.util.Hashtable;

public class Version {
	
	/**
	 * Name of the Last.fm radio player
	 */
	public static String PLAYER_NAME;
	
	/**
	 * Version number of RadioStation.ForMe
	 */
	public static String PLAYER_VERSION;
	
	/**
	 * Language of the player
	 */
	public static String LANGUAGE;
	
	static {
		try {
			Hashtable ht = RSFMUtils.loadProperties(Version.class
					.getResourceAsStream("/version.properties"));
			PLAYER_NAME = (String) ht.get("fm.radiostation.player_name");
			PLAYER_VERSION = (String) ht.get("fm.radiostation.player_version");
			LANGUAGE = (String) ht.get("fm.radiostation.language");
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Unable to load version.properties");
		}
	}

}
