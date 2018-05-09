package org.unclesniper.choreo;

public class InvalidElementClassMethodException extends InvalidElementClassException {

	private String methodName;

	public InvalidElementClassMethodException(String module, String elementClassName, String methodName,
			String message) {
		this(module, elementClassName, methodName, message, null);
	}

	public InvalidElementClassMethodException(String module, String elementClassName, String methodName,
			String message, Throwable cause) {
		super(module, elementClassName, "Method '" + methodName + "' of element class '" + elementClassName
				+ "' in module '" + module + "' is invalid" + (message != null && message.length() > 0
						? ": " + message : (cause != null && cause.getMessage() != null
						&& cause.getMessage().length() > 0 ? ": " + cause.getMessage() : "")));
		this.methodName = methodName;
	}

	public String getMethodName() {
		return methodName;
	}

}
