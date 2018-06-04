package org.unclesniper.choreo.parseopt;

public class OptionException extends CommandLineException {

	private final String optionName;

	private final OptionType optionType;

	private final String rendition;

	public OptionException(String optionName, OptionType optionType, String rendition, String message) {
		super(message);
		this.optionName = optionName;
		this.optionType = optionType;
		this.rendition = rendition;
	}

	public String getOptionName() {
		return optionName;
	}

	public OptionType getOptionType() {
		return optionType;
	}

	public String getRendition() {
		return rendition;
	}

}
