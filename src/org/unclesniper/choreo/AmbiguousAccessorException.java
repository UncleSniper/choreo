package org.unclesniper.choreo;

import org.xml.sax.Locator;

public class AmbiguousAccessorException extends ChoreoGraphException {

	private final ClassInfo.AccessorType accessorType;

	private final String propertyName;

	private final ClassInfo enclosingClass;

	private final Class<?> valueClass;

	public AmbiguousAccessorException(XMLLocation location, ClassInfo.AccessorType accessorType,
			String propertyName, ClassInfo enclosingClass, Class<?> valueClass) {
		super(location, "Ambiguous " + accessorType.name().toLowerCase() + " for property '"
				+ propertyName + "' of class '" + enclosingClass.getSubject().getName()
				+ "' with value of type " + (valueClass == null ? "<null>" : '\'' + valueClass.getName() + '\''));
		this.accessorType = accessorType;
		this.propertyName = propertyName;
		this.enclosingClass = enclosingClass;
		this.valueClass = valueClass;
	}

	public AmbiguousAccessorException(Locator location, ClassInfo.AccessorType accessorType,
			String propertyName, ClassInfo enclosingClass, Class<?> valueClass) {
		this(location == null ? null : new XMLLocation(location),
				accessorType, propertyName, enclosingClass, valueClass);
	}

	public ClassInfo.AccessorType getAccessorType() {
		return accessorType;
	}

	public String getPropertyName() {
		return propertyName;
	}

	public ClassInfo getEnclosingClass() {
		return enclosingClass;
	}

	public Class<?> getValueClass() {
		return valueClass;
	}

}
