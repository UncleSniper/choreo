package org.unclesniper.choreo.parseopt;

public class ConflictingOptionsException extends CommandLineException {

	private final String nameA;

	private final String nameB;

	public ConflictingOptionsException(String nameA, String nameB) {
		super("Options '" + nameA + "' and '" + nameB + "' cannot be used together");
		this.nameA = nameA;
		this.nameB = nameB;
	}

	public String getNameA() {
		return nameA;
	}

	public String getNameB() {
		return nameB;
	}

}
