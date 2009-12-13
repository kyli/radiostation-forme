/*
 * Feb 27, 2009
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

import net.rim.device.api.ui.component.Dialog;
import fm.radiostation.RSFMSession;
import fm.radiostation.Version;

public class AboutAction extends AbstractRSFMAction {

	public AboutAction(RSFMSession session) {
		super(session);
	}

	public void run() {
		String message = "RadioStation.ForMe (rs.fm) v"+Version.PLAYER_VERSION+"\n" +
				"by Kaiyi Li\n\n" +
				"RadioStation.ForMe (rs.fm) is free software. Please visit project page for any latest updates.\n" +
				"http://code.google.com/p/radiostation-forme/";
		Dialog.inform(message);
	}
}