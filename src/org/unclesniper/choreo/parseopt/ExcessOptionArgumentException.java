package org.unclesniper.choreo.parseopt;

public class ExcessOptionArgumentException extends OptionException {

	public ExcessOptionArgumentException(String optionName, OptionType optionType, String rendition) {
		super(optionName, optionType, rendition, "Command line option takes no argument: " + rendition);
	}

}
