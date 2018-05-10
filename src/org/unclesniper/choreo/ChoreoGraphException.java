package org.unclesniper.choreo;

import org.xml.sax.Locator;

public class ChoreoGraphException extends ChoreoException {

	private final XMLLocation location;

	public ChoreoGraphException(XMLLocation location, String message) {
		super(message);
		this.location = location;
	}

	public ChoreoGraphException(XMLLocation location, String message, Throwable cause) {
		super(message, cause);
		this.location = location;
	}

	public ChoreoGraphException(Locator location, String message) {
		super(message);
		this.location = location == null ? null : new XMLLocation(location);
	}

	public ChoreoGraphException(Locator location, String message, Throwable cause) {
		super(message, cause);
		this.location = location == null ? null : new XMLLocation(location);
	}

	public XMLLocation getLocation() {
		return location;
	}

}
