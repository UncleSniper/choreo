package org.unclesniper.choreo;

import org.xml.sax.Locator;

public class XMLLocation {

	private final String publicID;

	private final String systemID;

	private final int line;

	private final int column;

	public XMLLocation(String publicID, String systemID, int line, int column) {
		this.publicID = publicID;
		this.systemID = systemID;
		this.line = line;
		this.column = column;
	}

	public XMLLocation(Locator locator) {
		publicID = locator.getPublicId();
		systemID = locator.getSystemId();
		line = locator.getLineNumber();
		column = locator.getColumnNumber();
	}

	public String getPublicID() {
		return publicID;
	}

	public String getSystemID() {
		return systemID;
	}

	public int getLine() {
		return line;
	}

	public int getColumn() {
		return column;
	}

	public String toString() {
		StringBuilder builder = new StringBuilder();
		boolean empty = true;
		if(publicID != null && publicID.length() > 0) {
			builder.append("pub ");
			builder.append(publicID);
			empty = false;
		}
		if(systemID != null && systemID.length() > 0) {
			if(empty)
				empty = false;
			else
				builder.append(' ');
			builder.append("sys ");
			builder.append(systemID);
		}
		if(!empty)
			builder.append(' ');
		if(line <= 0)
			builder.append("<unknown location>");
		else {
			builder.append("line ");
			builder.append(String.valueOf(line));
			if(column > 0) {
				builder.append(" column ");
				builder.append(String.valueOf(column));
			}
		}
		return builder.toString();
	}

}
