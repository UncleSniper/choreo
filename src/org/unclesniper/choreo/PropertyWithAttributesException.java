package org.unclesniper.choreo;

import org.xml.sax.Locator;

public class PropertyWithAttributesException extends ChoreoGraphException {

	private final String propertyName;

	public PropertyWithAttributesException(XMLLocation location, String propertyName) {
		super(location, "Property element '" + propertyName + "' may not have any attributes"
				+ (location == null ? "" : " at " + location));
		this.propertyName = propertyName;
	}

	public PropertyWithAttributesException(Locator location, String propertyName) {
		this(location == null ? null : new XMLLocation(location), propertyName);
	}

	public String getPropertyName() {
		return propertyName;
	}

}
