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

import net.rim.device.api.i18n.ResourceBundle;
import net.rim.device.api.util.StringUtilities;

/**
 * This singleton class controls the access to all of the externalized strings
 * to the ui package.
 * 
 * @author kaiyi
 * 
 */
public final class RSFMResource {

	private static ResourceBundle uiResource;

	public static final String UI_RESOURCE_BUNDLE_NAME = "fm.radiostation.ui.Messages";

	/**
	 * Returns a reference of {@link ResourceBundle} which allows access to
	 * strings displayed from the ui.
	 * 
	 * @return a reference of ResourceBundle containing strings from the ui.
	 */
	public static ResourceBundle getUiResources() {
		if (uiResource == null) {
			uiResource = ResourceBundle.getBundle(StringUtilities
					.stringHashToLong(UI_RESOURCE_BUNDLE_NAME),
					UI_RESOURCE_BUNDLE_NAME);
			return uiResource;
		} else {
			return uiResource;
		}
	}
}
