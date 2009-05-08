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


public class MobileSession extends ResponseObject{
	
	public static final String METHOD = "auth.getmobilesession";

	private String username;
	private String sk;
	private boolean subscriber;

	public void setSk(String sk) {
		this.sk = sk;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public void setSubscriber(boolean subscriber) {
		this.subscriber = subscriber;
	}
	public String getSk() {
		return sk;
	}
	public String getUsername() {
		return username;
	}
	public boolean isSubscriber() {
		return subscriber;
	}
}
