/*
 * Feb 22, 2009
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
package fm.radiostation.player;

/**
 * The listener for RadioPlayer events. RadioPlayer event defines several event
 * types, which involves playback changes. An instance implementing this class
 * will be notified when RadioPlayerEvent occurs. Appropriate actions can then
 * be taken depending on the event type and the enclosing track.
 * 
 * @author kaiyi
 * 
 */
public interface RadioPlayerEventListener {

	/**
	 * Process the event and perform the appropriate procedures depending on the
	 * event type and the enclosing track.
	 * 
	 * @param event
	 */
	public void radioPlayerEventOccurred(RadioPlayerEvent event);
}
