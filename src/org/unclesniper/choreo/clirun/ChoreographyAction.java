package org.unclesniper.choreo.clirun;

public class ChoreographyAction extends CLIAction {

	public ChoreographyAction(CLIOptions options) {
		super(options);
	}

	public void wordEncountered(String key, String value) {
		options.setGraphSource(value);
		options.setGraphSourceType(CLIOptions.GraphSourceType.FILE);
	}

}
