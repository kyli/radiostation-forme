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
package fm.radiostation.ui;

import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Manager;

/**
 * A simplified implementation of boarder manager. An instance of this layout
 * manager will attempt to place two of its child components as farther apart as
 * possible, either horizontally or vertically, as specified at construction.
 * <p>
 * An instance of this layout manager <strong>must have exactly two child
 * components</strong>. Having less than two will throw an
 * IndexOutOfBoundException at runtime, and having more than two will result
 * incorrect rendering
 * 
 * @author kaiyi
 * 
 */
public class BipartBorderManager extends Manager {

	private final boolean isHorizontal;

	protected BipartBorderManager(long style, boolean isHorizontal){
		super(style);
		this.isHorizontal = isHorizontal;
	}

	/**
	 * Adds given field to manager. An instance of this layout manager must have
	 * exactly two child components. Having less than two will throw an
	 * IndexOutOfBoundException at runtime, and having more than two will result
	 * incorrect rendering. Therefore, {@link #add(Field)} must be invoked
	 * exactly twice.
	 */
	public void add(Field field) {
		super.add(field);
	}

	protected void sublayout(int width, int height) {
		int x = 0;
		int y = 0;
		Field field = getField(0);
		layoutChild(field, width, height);
		setPositionChild(field, x, y);
		if (isHorizontal) {
			field = getField(1);
			layoutChild(field, width, height);
			setPositionChild(field, width-field.getWidth(), y);
			setExtent(width, field.getHeight());
		} else {
			field = getField(1);
			layoutChild(field, width, height);
			setPositionChild(field, x, height-field.getHeight());
			setExtent(field.getWidth(), height);
		}
	}

}
