package org.unclesniper.choreo.clirun;

public class SitePropertiesAction extends CLIAction {

	public SitePropertiesAction(CLIOptions options) {
		super(options);
	}

	public void wordEncountered(String key, String value) {
		options.setSiteProperties(value);
	}

}
