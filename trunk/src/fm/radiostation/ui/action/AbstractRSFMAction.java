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

package fm.radiostation.ui.action;

import fm.radiostation.RSFMSession;

/**
 * Abstract top level class that defines the behaviours of individual tasks
 * executed by invocation of menu items and buttons. When the respective gui
 * component is invoked, {@link #run()} is called.
 * 
 * @author kaiyi
 * 
 */
abstract public class AbstractRSFMAction {

	/**
	 * A reference of the RSFMSession object. It is accessible to all of its
	 * child classes
	 */
	protected RSFMSession rsfmSession;

	public AbstractRSFMAction(RSFMSession rsfmSession) {
		this.rsfmSession = rsfmSession;
	}

	/**
	 * Invoked when the associated UI component is activated. This method body
	 * contains the necessary and expected procedures to be executed when an
	 * action is to be performed.
	 */
	abstract public void run();

}
