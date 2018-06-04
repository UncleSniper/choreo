package org.unclesniper.choreo.parseopt;

public class UnexpectedBarewordException extends CommandLineException {

	private final String word;

	public UnexpectedBarewordException(String word) {
		super("Unexpected non-option argument: " + word);
		this.word = word;
	}

	public String getWord() {
		return word;
	}

}
