package org.unclesniper.choreo;

import org.xml.sax.Locator;

public class NoSuchPropertyException extends ChoreoGraphException {

	private final String className;

	private final String propertyName;

	private final ClassInfo.AccessorType accessorType;

	public NoSuchPropertyException(XMLLocation location, String className, String propertyName,
			ClassInfo.AccessorType accessorType) {
		super(location, "Element class '" + className + "' does not expose a "
				+ accessorType.name().toLowerCase() + " for property '" + propertyName + '\''
				+ (location == null ? "" : " at " + location));
		this.className = className;
		this.propertyName = propertyName;
		this.accessorType = accessorType;
	}

	public NoSuchPropertyException(Locator location, String className, String propertyName,
			ClassInfo.AccessorType accessorType) {
		this(location == null ? null : new XMLLocation(location), className, propertyName, accessorType);
	}

	public String getClassName() {
		return className;
	}

	public String getPropertyName() {
		return propertyName;
	}

	public ClassInfo.AccessorType getAccessorType() {
		return accessorType;
	}

}
