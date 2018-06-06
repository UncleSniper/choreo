package org.unclesniper.choreo.clirun;

public class PredefAction extends CLIAction {

	public PredefAction(CLIOptions options) {
		super(options);
	}

	public void wordEncountered(String key, String value) {
		options.setGraphSource(value);
		options.setGraphSourceType(CLIOptions.GraphSourceType.PREDEF);
	}

}
