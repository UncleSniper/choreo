package org.unclesniper.choreo.clirun;

import org.unclesniper.choreo.parseopt.OptionParser;
import org.unclesniper.choreo.parseopt.IllegalOptionArgumentException;

public class SearchUpAction extends CLIAction {

	public SearchUpAction(CLIOptions options) {
		super(options);
	}

	public void wordEncountered(String key, String value) throws IllegalOptionArgumentException {
		options.setSearchUp(OptionParser.requireBoolean("--search-up", value));
	}

}
