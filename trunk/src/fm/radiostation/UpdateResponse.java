/*
 * 2009-12-19
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

public class UpdateResponse extends ResponseObject {
	
	private final String version;
	private final String updateurl;
	
	public UpdateResponse(String version, String updateurl) {
		this.version = version;
		this.updateurl = updateurl;
	}
	
	public String getUpdateurl() {
		return updateurl;
	}
	
	public String getVersion() {
		return version;
	}
}
