package org.unclesniper.choreo.clirun;

import org.unclesniper.choreo.parseopt.IllegalOptionArgumentException;

public abstract class AbstractServiceAction extends CLIAction {

	protected final String longOptionRendition;

	protected final String shortOptionRendition;

	public AbstractServiceAction(CLIOptions options, String longOptionRendition, String shortOptionRendition) {
		super(options);
		this.longOptionRendition = longOptionRendition;
		this.shortOptionRendition = shortOptionRendition;
	}

	public void wordEncountered(String key, String value) throws IllegalOptionArgumentException {
		int pos = value.indexOf('=');
		boolean isShort = key.length() == 1;
		if(pos < 0)
			throw new IllegalOptionArgumentException(isShort ? shortOptionRendition : longOptionRendition,
					"a key=value pair", value);
		setService(value.substring(0, pos), value.substring(pos + 1), isShort);
	}

	protected abstract void setService(String key, String value, boolean isShort)
			throws IllegalOptionArgumentException;

}
