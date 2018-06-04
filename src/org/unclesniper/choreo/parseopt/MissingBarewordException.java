package org.unclesniper.choreo.parseopt;

public class MissingBarewordException extends CommandLineException {

	private final String word;

	public MissingBarewordException(String word) {
		super("Missing required non-option argument: " + word);
		this.word = word;
	}

	public String getWord() {
		return word;
	}

}
