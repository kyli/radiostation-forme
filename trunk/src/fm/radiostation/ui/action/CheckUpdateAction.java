/*
 * 2009-12-18
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

import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import fm.radiostation.RSFMSession;
import fm.radiostation.ServiceEvent;
import fm.radiostation.ServiceEventListener;

/**
 * action that checks for software updates, and prompts user to download updates
 * when available.
 * 
 * @author kaiyi
 * 
 */
public class CheckUpdateAction implements Runnable, ServiceEventListener {

	private final RSFMSession session;

	public CheckUpdateAction(RSFMSession session) {
		this.session = session;
	}

	public void run() {
		session.addWebServiceListener(this);
		session.checkupdate();
	}

	/**
	 * if update available, ask user if an immediate update is desired
	 */
	public void serviceStateChanged(ServiceEvent event) {
		if (event == ServiceEvent.UPDATE_AVAILABLE) {
			session.removeWebServiceListener(this);
			UiApplication.getUiApplication().invokeLater(new Runnable() {
				public void run() {
					int choice = Dialog.ask(Dialog.D_YES_NO, 
							"New software update found. Download latest? (Existing user settings will be reset)", Dialog.YES);
					if (choice == Dialog.YES) {
						session.update();
					}
				}
			});
		} else if (event == ServiceEvent.UPDATE_UNAVAILABLE) {
			session.removeWebServiceListener(this);
			UiApplication.getUiApplication().invokeLater(new Runnable() {
				public void run() {
					Dialog.inform("RadioStation.ForMe is up to date!");
				}
			});
		} else if (event == ServiceEvent.UPDATE_FAILED) {
			session.removeWebServiceListener(this);
		}
	}
}
