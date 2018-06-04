package org.unclesniper.choreo.clirun;

import org.unclesniper.choreo.parseopt.WordAction;

public abstract class CLIAction implements WordAction {

	protected final CLIOptions options;

	public CLIAction(CLIOptions options) {
		this.options = options;
	}

	public CLIOptions getOptions() {
		return options;
	}

}
