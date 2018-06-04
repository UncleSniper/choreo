package org.unclesniper.choreo.parseopt;

public class UnrecognizedOptionException extends OptionException {

	public UnrecognizedOptionException(String optionName, OptionType optionType, String rendition) {
		super(optionName, optionType, rendition, "Unrecognized command line option: " + rendition);
	}

}
