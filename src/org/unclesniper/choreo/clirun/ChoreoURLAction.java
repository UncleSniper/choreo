package org.unclesniper.choreo.clirun;

public class ChoreoURLAction extends CLIAction {

	public ChoreoURLAction(CLIOptions options) {
		super(options);
	}

	public void wordEncountered(String key, String value) {
		options.setGraphSource(value);
		options.setGraphSourceType(CLIOptions.GraphSourceType.URL);
	}

}
