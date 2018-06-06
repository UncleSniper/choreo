package org.unclesniper.choreo.clirun;

import org.unclesniper.choreo.parseopt.OptionParser;
import org.unclesniper.choreo.parseopt.IllegalOptionArgumentException;

public class MiscTypesAction extends CLIAction {

	public MiscTypesAction(CLIOptions options) {
		super(options);
	}

	public void wordEncountered(String key, String value) throws IllegalOptionArgumentException {
		options.setMiscTypes(OptionParser.requireBoolean("--misc-types", value));
	}

}
