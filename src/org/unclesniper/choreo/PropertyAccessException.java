package org.unclesniper.choreo;

import org.xml.sax.Locator;

public class PropertyAccessException extends ChoreoException {

	private final XMLLocation location;

	private final String className;

	private final String propertyName;

	private final String methodName;

	public PropertyAccessException(XMLLocation location, String className,
			String propertyName, String methodName, Throwable cause) {
		super(PropertyAccessException.makeMessage(location, className, propertyName, methodName, cause), cause);
		this.location = location;
		this.className = className;
		this.propertyName = propertyName;
		this.methodName = methodName;
	}

	public PropertyAccessException(Locator location, String className,
			String propertyName, String methodName, Throwable cause) {
		this(location == null ? null : new XMLLocation(location), className, propertyName, methodName, cause);
	}

	public XMLLocation getLocation() {
		return location;
	}

	public String getClassName() {
		return className;
	}

	public String getPropertyName() {
		return propertyName;
	}

	public String getMethodName() {
		return methodName;
	}

	private static String makeMessage(XMLLocation location, String className,
			String propertyName, String methodName, Throwable cause) {
		StringBuilder builder = new StringBuilder();
		builder.append("Failed to set property");
		if(propertyName != null) {
			builder.append(" '");
			builder.append(propertyName);
			builder.append('\'');
		}
		if(methodName != null) {
			builder.append(" using method '");
			builder.append(methodName);
			builder.append('\'');
		}
		builder.append(" on object of type '");
		builder.append(className);
		builder.append('\'');
		if(location != null) {
			builder.append(" at ");
			builder.append(location.toString());
		}
		String msg = cause == null ? null : cause.getMessage();
		if(msg != null && !msg.isEmpty()) {
			builder.append(": ");
			builder.append(msg);
		}
		return builder.toString();
	}

}
