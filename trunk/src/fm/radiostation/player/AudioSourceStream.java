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

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.media.Control;
import javax.microedition.media.Player;
import javax.microedition.media.protocol.ContentDescriptor;
import javax.microedition.media.protocol.SourceStream;

/**
 * The underlying audio source stream that would be fed into the player. An
 * instance of this class wraps one inputstream pointing to the audio data
 * source and reads data from it when requested by player.
 * 
 * @see {@link AudioDataSource}, {@link Player}
 * @author kaiyi
 * 
 */
public class AudioSourceStream implements SourceStream {
	
	private final ContentDescriptor descriptor;
	private final InputStream baseSharedStream;
	private final int length;
	private int totalRead;
	
	public AudioSourceStream(InputStream baseSharedStream,
			String contentType, int length) {
		this.baseSharedStream = baseSharedStream;
		this.length = length;
		descriptor = new ContentDescriptor(contentType);
	}

	public void close() throws IOException {
		baseSharedStream.close();
	}

	public ContentDescriptor getContentDescriptor() {
		return descriptor;
	}

	public long getContentLength() {
		return length;
	}

	/**
	 * Any form of random access is not allowed
	 */
	public int getSeekType() {
		return NOT_SEEKABLE;
	}

	public int getTransferSize() {
		return 58000;
	}

	public int read(byte[] b, int off, int len) throws IOException {
		int read = baseSharedStream.read(b, off, len);
		totalRead += read;
		return read;
	}

	public long seek(long where) throws IOException {
		return totalRead;
	}

	public long tell() {
		return totalRead;
	}

	public Control getControl(String controlType) {
		throw new UnsupportedOperationException(
				"AudioSourceStream.getControl Not implemented");
	}

	public Control[] getControls() {
		throw new UnsupportedOperationException(
				"AudioSourceStream.getControls Not implemented");
	}
}