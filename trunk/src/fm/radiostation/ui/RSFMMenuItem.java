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
import net.rim.device.api.ui.MenuItem;
import fm.radiostation.ui.action.AbstractRSFMAction;

public class RSFMMenuItem extends MenuItem {

	private AbstractRSFMAction embedingAction;

	public RSFMMenuItem(ResourceBundle bundle, int id, int ordinal,
			int priority, AbstractRSFMAction action) {
		super(bundle, id, ordinal, priority);
		embedingAction = action;
	}

	public void run() {
		embedingAction.run();
	}
}
