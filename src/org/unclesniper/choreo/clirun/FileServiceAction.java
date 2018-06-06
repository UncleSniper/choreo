package org.unclesniper.choreo.clirun;

import java.net.URL;
import java.io.File;
import java.net.MalformedURLException;
import org.unclesniper.choreo.parseopt.IllegalOptionArgumentException;

public class FileServiceAction extends AbstractServiceAction {

	public FileServiceAction(CLIOptions options) {
		super(options, "--service", "-s");
	}

	protected void setService(String key, String value, boolean isShort) throws IllegalOptionArgumentException {
		URL url;
		try {
			url = new File(value).toURI().toURL();
		}
		catch(MalformedURLException mue) {
			throw new IllegalOptionArgumentException(isShort ? shortOptionRendition : longOptionRendition,
					"a key=value pair whose value maps to a valid file URL", value);
		}
		if(key.isEmpty())
			options.addUnmappedService(url);
		else
			options.addMappedService(key, url);
	}

}
