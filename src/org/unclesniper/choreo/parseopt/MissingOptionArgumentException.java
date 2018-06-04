package org.unclesniper.choreo.parseopt;

public class MissingOptionArgumentException extends OptionException {

	public MissingOptionArgumentException(String optionName, OptionType optionType, String rendition) {
		super(optionName, optionType, rendition, "Command line option is missing required argument: " + rendition);
	}

}
