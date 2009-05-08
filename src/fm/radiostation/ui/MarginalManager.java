/*
 * Mar 18, 2009
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
 * A RSFM implementation of the default Layout Manager. It arranges its
 * components to have a margin of specified size at top, bottom, left and right.
 * Beware that this marginal manager is <strong>incapable of containing more than 1
 * child component</strong>. The user should wrap all desired child components into one
 * wrapper Layout Manager and then add the wrapper into this marginal manager.
 * 
 * @author kaiyi
 * 
 */
public class MarginalManager extends Manager {
	
	private static int MARGINAL_SIZE;

	public MarginalManager(long style, int marginalSize){
		super(style);
		MARGINAL_SIZE = marginalSize;
	}

	/**
	 * Adds given field to manager. This layout manager is only capable of
	 * adding one child field. Having more than one child components, i.e.
	 * invoke {@link #add(Field)} twice will result incorrect rendering
	 */
	public void add(Field field) {
		super.add(field);
	}
	
	protected void sublayout(int width, int height) {
		int x = MARGINAL_SIZE;
		int y = MARGINAL_SIZE;

		for (int i = 0; i < getFieldCount(); i++) {
			Field field = getField(i);
			layoutChild(field, width-(2*MARGINAL_SIZE), height-(2*MARGINAL_SIZE));
			setPositionChild(field, x, y);
			x += field.getWidth();
			y += field.getHeight();
		}
		setExtent(x, y);
	}
};