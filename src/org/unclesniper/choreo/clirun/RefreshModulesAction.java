package org.unclesniper.choreo.clirun;

import org.unclesniper.choreo.parseopt.OptionParser;
import org.unclesniper.choreo.parseopt.IllegalOptionArgumentException;

public class RefreshModulesAction extends CLIAction {

	public RefreshModulesAction(CLIOptions options) {
		super(options);
	}

	public void wordEncountered(String key, String value) throws IllegalOptionArgumentException {
		options.setRefreshModules(value == null || OptionParser.requireBoolean("--refresh-modules", value));
	}

}
