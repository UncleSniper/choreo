package org.unclesniper.choreo;

public class InvalidElementClassException extends IllegalModuleException {

	private final String elementClassName;

	public InvalidElementClassException(String module, String elementClassName, String message) {
		super(module, message);
		this.elementClassName = elementClassName;
	}

	public InvalidElementClassException(String module, String elementClassName, String message, Throwable cause) {
		super(module, message, cause);
		this.elementClassName = elementClassName;
	}

	public String getElementClassName() {
		return elementClassName;
	}

}
