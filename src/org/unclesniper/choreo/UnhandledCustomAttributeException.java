package org.unclesniper.choreo;

import org.xml.sax.Locator;

public class UnhandledCustomAttributeException extends ChoreoGraphException {

	private final String moduleURL;

	private final String attributeName;

	public UnhandledCustomAttributeException(XMLLocation location, String moduleURL, String attributeName) {
		super(location, "Module '" + moduleURL + "' does not define a handler for the attribute '"
				+ attributeName + '\'' + (location == null ? "" : " at " + location));
		this.moduleURL = moduleURL;
		this.attributeName = attributeName;
	}

	public UnhandledCustomAttributeException(Locator location, String moduleURL, String attributeName) {
		this(location == null ? null : new XMLLocation(location), moduleURL, attributeName);
	}

	public String getModuleURL() {
		return moduleURL;
	}

	public String getAttributeName() {
		return attributeName;
	}

}
