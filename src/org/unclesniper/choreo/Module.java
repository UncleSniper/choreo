package org.unclesniper.choreo;

import java.net.URL;
import java.util.Map;
import java.util.HashMap;

public final class Module {

	private final URL url;

	private final Map<String, ClassInfo> classes = new HashMap<String, ClassInfo>();

	public Module(URL url) {
		this.url = url;
	}

	public URL getURL() {
		return url;
	}

}
