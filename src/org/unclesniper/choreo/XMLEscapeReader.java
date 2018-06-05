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
			inoff = 0;
			if(infill <= 0)
				return -1;
		}
		char c = inbuf[inoff];
		String rend;
		switch(c) {
			case '&':
				rend = "&amp;";
				break;
			case '<':
				rend = "&lt;";
				break;
			case '>':
				rend = "&gt;";
				break;
			case '\'':
				rend = "&apos;";
				break;
			case '"':
				rend = "&quot;";
				break;
			case '\t':
				rend = "&#x9;";
				break;
			case '\n':
				rend = "&#xA;";
				break;
			case '\r':
				rend = "&#xD;";
				break;
			default:
				++inoff;
				if(c <= '\u001F')
					return 0xFFFD;
				else
					return (int)c;
		}
		c = rend.charAt(eoff);
		if(++eoff >= rend.length()) {
			eoff = 0;
			++inoff;
		}
		return (int)c;
	}

	public int read(char[] cbuf, int off, int len) throws IOException {
		int i;
		for(i = 0; i < len; ++i) {
			if(inoff >= infill) {
				infill = in.read(inbuf);
				inoff = 0;
				if(infill <= 0)
					break;
			}
			char c = inbuf[inoff];
			String rend;
			int nextStored;
			switch(c) {
				case '&':
					rend = "&amp;";
					break;
				case '<':
					rend = "&lt;";
					break;
				case '>':
					rend = "&gt;";
					break;
				case '\'':
					rend = "&apos;";
					break;
				case '"':
					rend = "&quot;";
					break;
				case '\t':
					rend = "&#x9;";
					break;
				case '\n':
					rend = "&#xA;";
					break;
				case '\r':
					rend = "&#xD;";
					break;
				default:
					++inoff;
					if(c <= '\u001F')
						cbuf[off + i] = '\u001F';
					else
						cbuf[off + i] = c;
					continue;
			}
			int rest = len - i, rlength = rend.length(), erest = rlength - eoff;
			if(erest < rest)
				rest = erest;
			for(int j = 0; j < rest; ++j)
				cbuf[off + i + j] = rend.charAt(eoff + j);
			eoff += rest;
			if(eoff >= rlength) {
				eoff = 0;
				++inoff;
			}
			i += rest - 1;
		}
		return i == 0 ? -1 : i;
	}

	public long skip(long n) throws IOException {
		if(n < 0l)
			throw new IllegalArgumentException("Cannot skip negative number of characters: " + n);
		long skipped = 0l;
		while(skipped < n) {
			long srest = n - skipped;
			int iskip = srest > (long)Integer.MAX_VALUE ? Integer.MAX_VALUE : (int)srest;
			if(inoff >= infill) {
				infill = in.read(inbuf);
				inoff = 0;
				if(infill <= 0)
					return skipped;
			}
			char c = inbuf[inoff];
			int rlength;
			switch(c) {
				case '&':
				case '\t':
				case '\n':
				case '\r':
					rlength = 5;
					break;
				case '<':
				case '>':
					rlength = 4;
					break;
				case '\'':
				case '"':
					rlength = 6;
					break;
				default:
					rlength = 1;
					break;
			}
			int erest = rlength - eoff;
			if(iskip >= erest) {
				eoff = 0;
				++inoff;
				skipped += (long)erest;
			}
			else {
				eoff += iskip;
				skipped += (long)iskip;
			}
		}
		return skipped;
	}

}
