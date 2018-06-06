package org.unclesniper.choreo.clirun;

import java.net.MalformedURLException;
import org.unclesniper.choreo.URLEntityResolver;
import org.unclesniper.choreo.parseopt.IllegalOptionArgumentException;

public class URLEntityAction extends AbstractEntityAction {

	private final boolean escape;

	public URLEntityAction(CLIOptions options, boolean escape) {
		super(options, escape ? "--esc-entity-url" : "--entity-url", escape ? "-U" : "-u");
		this.escape = escape;
	}

	protected void setResolver(String key, String value, boolean isShort) throws IllegalOptionArgumentException {
		URLEntityResolver resolver;
		try {
			resolver = new URLEntityResolver(value, escape);
		}
		catch(MalformedURLException mue) {
			throw new IllegalOptionArgumentException(isShort ? shortOptionRendition : longOptionRendition,
					"a key=value pair whose value is a valid URL", value);
		}
		options.addEntityResolver(key, resolver);
	}

}
