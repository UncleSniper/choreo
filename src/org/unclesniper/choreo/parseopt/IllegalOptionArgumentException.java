package org.unclesniper.choreo.parseopt;

public class IllegalOptionArgumentException extends CommandLineException {

	private final String optionRendition;

	private final String expectedArgument;

	private final String unexpectedArgument;

	public IllegalOptionArgumentException(String optionRendition,
			String expectedArgument, String unexpectedArgument) {
		super("Argument to option '" + optionRendition + "' must be " + expectedArgument
				+ ", not '" + unexpectedArgument + '\'');
		this.optionRendition = optionRendition;
		this.expectedArgument = expectedArgument;
		this.unexpectedArgument = unexpectedArgument;
	}

	public String getOptionRendition() {
		return optionRendition;
	}

	public String getExpectedArgument() {
		return expectedArgument;
	}

	public String getUnexpectedArgument() {
		return unexpectedArgument;
	}

}
