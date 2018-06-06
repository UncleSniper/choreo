package org.unclesniper.choreo;

import java.net.URL;
import java.io.Reader;
import java.io.IOException;
import java.io.InputStream;
import org.xml.sax.InputSource;
import java.io.InputStreamReader;
import java.net.MalformedURLException;

public class URLEntityResolver implements ChoreoEntityResolver {

	public static final String DEFAULT_ENCODING = "UTF-8";

	private URL url;

	private boolean escape;

	private String encoding;

	public URLEntityResolver(URL url) {
		this(url, false);
	}

	public URLEntityResolver(URL url, boolean escape) {
		this.url = url;
		this.escape = escape;
	}

	public URLEntityResolver(String url) throws MalformedURLException {
		this(url, false);
	}

	public URLEntityResolver(String url, boolean escape) throws MalformedURLException {
		this(url == null ? null : new URL(url), escape);
	}

	public URL getURL() {
		return url;
	}

	public void setURL(URL url) {
		this.url = url;
	}

	public void setURL(String url) throws MalformedURLException {
		this.url = url == null ? null : new URL(url);
	}

	public boolean isEscape() {
		return escape;
	}

	public void setEscape(boolean escape) {
		this.escape = escape;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public InputSource resolveEntity(String choreoID) throws IOException {
		InputStream is = url.openStream();
		try {
			Reader isr = new InputStreamReader(is,
					encoding == null ? URLEntityResolver.DEFAULT_ENCODING : encoding);
			Reader reader = escape ? new XMLEscapeReader(isr) : isr;
			InputSource source = new InputSource(reader);
			source.setSystemId("choreo:" + choreoID);
			is = null;
			return source;
		}
		finally {
			if(is != null)
				is.close();
		}
	}

	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(URLEntityResolver.class.getName());
		builder.append("(\"");
		builder.append(url.toString());
		builder.append("\", ");
		builder.append(escape);
		builder.append(')');
		return builder.toString();
	}

}
