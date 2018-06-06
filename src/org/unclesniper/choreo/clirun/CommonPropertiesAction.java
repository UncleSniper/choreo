package org.unclesniper.choreo.clirun;

public class CommonPropertiesAction extends CLIAction {

	public CommonPropertiesAction(CLIOptions options) {
		super(options);
	}

	public void wordEncountered(String key, String value) {
		options.setCommonProperties(value);
	}

}
