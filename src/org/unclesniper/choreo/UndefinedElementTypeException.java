package org.unclesniper.choreo;

import org.xml.sax.Locator;

public class UndefinedElementTypeException extends ChoreoGraphException {

	private final String moduleURL;

	private final String elementName;

	public UndefinedElementTypeException(XMLLocation location, String moduleURL, String elementName) {
		super(location, "Module '" + moduleURL + "' does not define an element class for the tag '"
				+ elementName + '\'' + (location == null ? "" : " at " + location));
		this.moduleURL = moduleURL;
		this.elementName = elementName;
	}

	public UndefinedElementTypeException(Locator location, String moduleURL, String elementName) {
		this(location == null ? null : new XMLLocation(location), moduleURL, elementName);
	}

}
