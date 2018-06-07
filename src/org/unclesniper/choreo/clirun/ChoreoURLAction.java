package org.unclesniper.choreo.clirun;

import java.net.URL;
import java.net.MalformedURLException;
import org.unclesniper.choreo.parseopt.IllegalOptionArgumentException;

public class ChoreoURLAction extends CLIAction {

	public ChoreoURLAction(CLIOptions options) {
		super(options);
	}

	public void wordEncountered(String key, String value) throws IllegalOptionArgumentException {
		try {
			new URL(value);
		}
		catch(MalformedURLException mue) {
			throw new IllegalOptionArgumentException(key.length() == 1 ? "-r" : "--choreo-url",
					"a valid URL", value);
		}
		options.setGraphSource(value);
		options.setGraphSourceType(CLIOptions.GraphSourceType.URL);
	}

}
