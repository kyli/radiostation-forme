/*
 * Feb 25, 2009
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

package fm.radiostation;

/**
 * A general purpose listener responsible of processing events relating to
 * backend activities. Such as connecting to Radio and etc.
 * 
 * @author kaiyi
 * 
 */
public interface StatusEventListener {

	public void statusChanged(StatusEvent event);
}
