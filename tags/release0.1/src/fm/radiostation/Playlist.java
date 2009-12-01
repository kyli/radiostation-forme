/*
 * 2009-02-12
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

import java.util.Vector;

public class Playlist extends ResponseObject {
	
	private String title;
	private String creator;
	
	private Vector tracklist;
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getCreator() {
		return creator;
	}
	public void setCreator(String creator) {
		this.creator = creator;
	}
	public Vector getTracklist() {
		return tracklist;
	}
	public void setTracklist(Vector tracklist) {
		this.tracklist = tracklist;
	}
}
