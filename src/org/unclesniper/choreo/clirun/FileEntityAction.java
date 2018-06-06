package org.unclesniper.choreo.clirun;

import org.unclesniper.choreo.FileEntityResolver;

public class FileEntityAction extends AbstractEntityAction {

	private final boolean escape;

	public FileEntityAction(CLIOptions options, boolean escape) {
		super(options, escape ? "--esc-entity-file" : "--entity-file", escape ? "-F" : "-f");
		this.escape = escape;
	}

	protected void setResolver(String key, String value, boolean isShort) {
		options.addEntityResolver(key, new FileEntityResolver(value, escape));
	}

}
