/*
 * Feb 28, 2009
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

package fm.radiostation.ui.action;

import java.util.Vector;

import net.rim.blackberry.api.browser.Browser;
import net.rim.blackberry.api.browser.BrowserSession;
import fm.radiostation.Playlist;
import fm.radiostation.RSFMSession;
import fm.radiostation.RSFMUtils;
import fm.radiostation.Track;

public class BuyTrackAction extends AbstractRSFMAction {
	
	public BuyTrackAction (RSFMSession rsfmSession) {
		super(rsfmSession);
	}

	public void run() {
		Playlist playlist = rsfmSession.getPlaylist();
		if (playlist == null) {
			return;
		}
		Vector tracklist = playlist.getTracklist();
		if (tracklist == null || tracklist.isEmpty()) {
			return;
		}
		String url = ((Track) tracklist.elementAt(0)).getTrackpurchase();
		RSFMUtils.debug("Track purchase request made to: "+url);
		BrowserSession session = Browser.getDefaultSession();
		session.displayPage(url);
	}

}
