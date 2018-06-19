package org.unclesniper.choreo;

import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;

public final class StreamUtils {

	private StreamUtils() {}

	public static void copy(InputStream in, OutputStream out) throws IOException {
		byte[] buffer = new byte[1024];
		for(;;) {
			int count = in.read(buffer);
			if(count <= 0)
				break;
			out.write(buffer, 0, count);
		}
	}

}
