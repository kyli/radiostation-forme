/*
 * Feb 13, 2009
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

import net.rim.device.api.util.Persistable;

public class Track extends ResponseObject implements Persistable {
	
	public static final String LOVE = "L";
	public static final String SKIP = "S";
	public static final String BAN = "B";
	
	private String location;
	private String title;
	private String album;
	private String creator;
	private int duration;
	private String image;
	private String trackauth;
	private String trackpurchase;
	private String rating;
	
	
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getAlbum() {
		return album;
	}
	public void setAlbum(String album) {
		this.album = album;
	}
	public String getCreator() {
		return creator;
	}
	public void setCreator(String creator) {
		this.creator = creator;
	}
	public int getDuration() {
		return duration;
	}
	public void setDuration(int duration) {
		this.duration = duration;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public void setTrackauth(String trackauth) {
		this.trackauth = trackauth;
	}
	public String getTrackauth() {
		return trackauth;
	}

	private int startTime;
	
	public void setStartTime(int startTime) {
		this.startTime = startTime;
	}
	public int getStartTime() {
		return startTime;
	}
	public void setTrackpurchase(String trackpurchase) {
		this.trackpurchase = trackpurchase;
	}
	public String getTrackpurchase() {
		return trackpurchase;
	}
	public void setRating(String rating) {
		this.rating = rating;
	}
	public String getRating() {
		return rating;
	}
}
