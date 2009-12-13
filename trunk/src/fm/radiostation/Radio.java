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

public class Radio extends ResponseObject {

	public static final String METHOD_RADIO_TUNE = "radio.tune";
	public static final String METHOD_RADIO_GETPLAYLIST = "radio.getplaylist";
	
	public static final String DEFAULT_STATION = "lastfm://globaltags/dance";
	
	private String type;
	private String name;
	private String url;
	private boolean supportdiscovery;
	public void setType(String type) {
		this.type = type;
	}
	public String getType() {
		return type;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getUrl() {
		return url;
	}
	public void setSupportdiscovery(boolean supportdiscovery) {
		this.supportdiscovery = supportdiscovery;
	}
	public boolean isSupportdiscovery() {
		return supportdiscovery;
	}
}
