package org.unclesniper.choreo;

import java.io.Reader;
import java.io.IOException;

public class XMLUtils {

	private XMLUtils() {}

	public static void escape(CharSequence in, Appendable out) throws IOException {
		int length = in.length();
		for(int i = 0; i < length; ++i) {
			char c = in.charAt(i);
			switch(c) {
				case '&':
					out.append("&amp;");
					break;
				case '<':
					out.append("&lt;");
					break;
				case '>':
					out.append("&gt;");
					break;
				case '\'':
					out.append("&apos;");
					break;
				case '"':
					out.append("&quot;");
					break;
				case '\t':
					out.append("&#x9;");
					break;
				case '\n':
					out.append("&#xA;");
					break;
				case '\r':
					out.append("&#xD;");
					break;
				default:
					if(c <= '\u001F')
						out.append('\uFFFD');
					else
						out.append(c);
					break;
			}
		}
	}

	public static String escape(CharSequence in) {
		StringBuilder builder = new StringBuilder(in.length() * 2);
		try {
			XMLUtils.escape(in, builder);
		}
		catch(IOException ioe) {
			throw new IllegalArgumentException(ioe.getMessage(), ioe);
		}
		return builder.toString();
	}

	public static void escape(Reader in, Appendable out) throws IOException {
		char[] buffer = new char[128];
		for(;;) {
			int count = in.read(buffer);
			if(count < 0)
				break;
			for(int i = 0; i < count; ++i) {
				char c = buffer[i];
				switch(c) {
					case '&':
						out.append("&amp;");
						break;
					case '<':
						out.append("&lt;");
						break;
					case '>':
						out.append("&gt;");
						break;
					case '\'':
						out.append("&apos;");
						break;
					case '"':
						out.append("&quot;");
						break;
					case '\t':
						out.append("&#x9;");
						break;
					case '\n':
						out.append("&#xA;");
						break;
					case '\r':
						out.append("&#xD;");
						break;
					default:
						if(c <= '\u001F')
							out.append('\uFFFD');
						else
							out.append(c);
						break;
				}
			}
		}
	}

}
