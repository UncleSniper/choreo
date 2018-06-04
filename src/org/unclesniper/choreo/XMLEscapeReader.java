package org.unclesniper.choreo;

import java.io.Reader;
import java.io.IOException;
import java.io.FilterReader;

public class XMLEscapeReader extends FilterReader {

	private final char[] inbuf = new char[128];

	private int infill;

	private int inoff;

	private int eoff;

	public XMLEscapeReader(Reader slave) {
		super(slave);
	}

	public void mark(int readAheadLimit) throws IOException {
		throw new IOException("Mark not supported");
	}

	public boolean markSupported() {
		return false;
	}

	public void reset() throws IOException {
		throw new IOException("Mark not supported");
	}

	public int read() throws IOException {
		if(inoff >= infill) {
			infill = in.read(inbuf);
			if(infill < 0)
				return -1;
		}
		//TODO
		return 0;
	}

	public int read(char[] cbuf, int off, int len) throws IOException {
		//TODO
		return 0;
	}

	public long skip(long n) throws IOException {
		//TODO
		return 0l;
	}

}
