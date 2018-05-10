package org.unclesniper.choreo;

import org.xml.sax.Locator;

public class ElementClassInstantiationException extends ChoreoException {

	private final String className;

	public ElementClassInstantiationException(XMLLocation location, String className, Throwable cause) {
		super("Failed to instantiate element class '" + className + '\''
				+ (location == null ? "" : " at " + location) + (cause == null || cause.getMessage() == null
				? "" : ": " + cause.getMessage()));
		this.className = className;
	}

	public ElementClassInstantiationException(Locator location, String className, Throwable cause) {
		this(location == null ? null : new XMLLocation(location), className, cause);
	}

	public String getClassName() {
		return className;
	}

}
