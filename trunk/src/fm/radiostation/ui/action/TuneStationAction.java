/*
 * 2009-02-19
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

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.DialogClosedListener;
import net.rim.device.api.ui.component.TextField;
import fm.radiostation.RSFMSession;
import fm.radiostation.ui.RSFMResource;

public class TuneStationAction extends AbstractRSFMAction implements DialogClosedListener {

	private TextField stationField;

	public TuneStationAction(RSFMSession session) {
		super(session);
	}

	public void run() {
		ResourceBundle bsrb = RSFMResource.getUiResources();
		String[] choices = new String[] {
			bsrb.getString(4),
			bsrb.getString(5),
			bsrb.getString(9),
			bsrb.getString(11),
			bsrb.getString(10)
		};
		Dialog dialog = new Dialog(bsrb.getString(22), choices, null, 0, null);
		dialog.setDialogClosedListener(this);
		stationField = new TextField();
		stationField.setEditable(true);
		stationField.setLabel(bsrb.getString(23)+": ");
		dialog.add(stationField);
		dialog.show();
	}

	public void dialogClosed(Dialog dialog, int choice) {
		String stationUrl;
		if (choice == 0) {
			stationUrl = getArtistStationURL(stationField.getText());
			session.tune(stationUrl);
		} else if (choice == 1) {
			stationUrl = getTagStationURL(stationField.getText());
			session.tune(stationUrl);
		} else if (choice == 2) {
			String username = session.getUsername();
			if (username == null) {
				return;
			}
			stationUrl = getRecommendedStationURL(username);
			session.tune(stationUrl);
		} else if (choice == 3) {
			String username = session.getUsername();
			if (username == null) {
				return;
			}
			stationUrl = getNeighbourStationURL(username);
			session.tune(stationUrl);
		}
	}

	private static String getArtistStationURL(String artist) {
		return "lastfm://artist/" + artist + "/similarartists";
	}

	private static String getTagStationURL(String tag) {
		return "lastfm://globaltags/" + tag;
	}
	
	private static String getNeighbourStationURL(String user) {
		return "lastfm://user/" + user + "/neighbours";
	}
	
	private static String getRecommendedStationURL(String user) {
		return "lastfm://user/" + user + "/recommended";
	}
}
