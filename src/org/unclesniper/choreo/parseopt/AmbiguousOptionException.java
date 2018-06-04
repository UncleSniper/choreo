package org.unclesniper.choreo.parseopt;

public class AmbiguousOptionException extends OptionException {

	private final String canonicalRenditionA;

	private final String canonicalRenditionB;

	public AmbiguousOptionException(String optionName, OptionType optionType, String rendition,
			String canonicalRenditionA, String canonicalRenditionB) {
		super(optionName, optionType, rendition, "Ambiguous command line option: Could be '" + canonicalRenditionA
				+ "' or '" + canonicalRenditionB + '\'');
		this.canonicalRenditionA = canonicalRenditionA;
		this.canonicalRenditionB = canonicalRenditionB;
	}

	public String getCanonicalRenditionA() {
		return canonicalRenditionA;
	}

	public String getCanonicalRenditionB() {
		return canonicalRenditionB;
	}

}
