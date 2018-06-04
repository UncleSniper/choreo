package org.unclesniper.choreo.clirun;

import org.unclesniper.choreo.parseopt.StopExecution;

public class UsageAction extends CLIAction {

	public UsageAction(CLIOptions options) {
		super(options);
	}

	public void wordEncountered(String key, String value) {
		options.usage();
		throw new StopExecution(0);
	}

}
