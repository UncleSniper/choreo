package org.unclesniper.choreo.clirun;

import java.net.URL;
import java.net.MalformedURLException;
import org.unclesniper.choreo.parseopt.IllegalOptionArgumentException;

public class URLServiceAction extends AbstractServiceAction {

	public URLServiceAction(CLIOptions options) {
		super(options, "--service-url", "-S");
	}

	protected void setService(String key, String value, boolean isShort) throws IllegalOptionArgumentException {
		URL url;
		try {
			url = new URL(value);
		}
		catch(MalformedURLException mue) {
			throw new IllegalOptionArgumentException(isShort ? shortOptionRendition : longOptionRendition,
					"a key=value pair whose value is a valid URL", value);
		}
		if(key.isEmpty())
			options.addUnmappedService(url);
		else
			options.addMappedService(key, url);
	}

}
