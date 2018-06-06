package org.unclesniper.choreo.clirun;

import org.unclesniper.choreo.parseopt.IllegalOptionArgumentException;

public abstract class AbstractEntityAction extends CLIAction {

	protected final String longOptionRendition;

	protected final String shortOptionRendition;

	public AbstractEntityAction(CLIOptions options, String longOptionRendition, String shortOptionRendition) {
		super(options);
		this.longOptionRendition = longOptionRendition;
		this.shortOptionRendition = shortOptionRendition;
	}

	public void wordEncountered(String key, String value) throws IllegalOptionArgumentException {
		int pos = value.indexOf('=');
		boolean isShort = key.length() == 1;
		if(pos <= 0)
			throw new IllegalOptionArgumentException(isShort ? shortOptionRendition : longOptionRendition,
					"a key=value pair", value);
		setResolver(value.substring(0, pos), value.substring(pos + 1), isShort);
	}

	protected abstract void setResolver(String key, String value, boolean isShort)
			throws IllegalOptionArgumentException;

}
