/*
 * Feb 2, 2009
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

package fm.radiostation.ui;

import net.rim.device.api.ui.UiApplication;
import fm.radiostation.RSFMSession;

/**
 * Main entry point of rs.fm. It is the application itself. After all the ui
 * components are initialized and rendered, the main thread will become the event
 * dispatching thread and become responsible for ui updates and event processing.
 * 
 * @author kaiyi
 * 
 */
public final class RSFMMainFrame extends UiApplication {

	private RSFMBaseScreen mainScreen;
	private RSFMSession rsfmSession;

	/**
	 * Setup the ui
	 */
	private RSFMMainFrame() {
		rsfmSession = new RSFMSession();
		mainScreen = new RSFMBaseScreen(rsfmSession);
		rsfmSession.loadSettings();
		rsfmSession.loadSession();
		pushScreen(mainScreen);

		// Immediately after GUI is rendered, create mobileSession in a separate
		// thread. This doesn't affect ui performance and prevents a blocking
		// operation.
		Thread rsfmConnector = new Thread() {
			public void run() {
				rsfmSession.fetchMobileSession();
				rsfmSession.handshake();
			}
		};
		rsfmConnector.start();
	}

	/**
	 * Entry point of the application, radiostation.forme
	 */
	public static void main(String[] args) {
		RSFMMainFrame rsfm = new RSFMMainFrame();
		rsfm.enterEventDispatcher();
	}
}
