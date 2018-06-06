package org.unclesniper.choreo.clirun;

import org.unclesniper.choreo.StringEntityResolver;

public class StringEntityAction extends AbstractEntityAction {

	private final boolean escape;

	public StringEntityAction(CLIOptions options, boolean escape) {
		super(options, escape ? "--esc-entity" : "--entity", escape ? "-E" : "-e");
		this.escape = escape;
	}

	protected void setResolver(String key, String value, boolean isShort) {
		options.addEntityResolver(key, new StringEntityResolver(value, escape));
	}

}
