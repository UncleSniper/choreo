package org.unclesniper.choreo;

import org.xml.sax.Locator;

public class NoMatchingAccessorException extends ChoreoGraphException {

	private final String className;

	private final String propertyName;

	private final ClassInfo.AccessorType accessorType;

	private final String valueTypeName;

	public NoMatchingAccessorException(XMLLocation location, String className, String propertyName,
			ClassInfo.AccessorType accessorType, String valueTypeName) {
		super(location, "No matching " + accessorType.name().toLowerCase() + " for property '"
				+ propertyName + "' in element class '" + className + "' for value "
				+ (valueTypeName == null ? "null" : "of type " + valueTypeName)
				+ (location == null ? "" : " at " + location));
		this.className = className;
		this.propertyName = propertyName;
		this.accessorType = accessorType;
		this.valueTypeName = valueTypeName;
	}

	public NoMatchingAccessorException(Locator location, String className, String propertyName,
			ClassInfo.AccessorType accessorType, String valueTypeName) {
		this(location == null ? null : new XMLLocation(location),
				className, propertyName, accessorType, valueTypeName);
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

	public String getValueTypeName() {
		return valueTypeName;
	}

}
