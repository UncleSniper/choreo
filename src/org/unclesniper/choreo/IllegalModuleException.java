package org.unclesniper.choreo;

public class IllegalModuleException extends ChoreoException {

	private final String module;

	public IllegalModuleException(String module, String message) {
		super(message);
		this.module = module;
	}

	public IllegalModuleException(String module, String message, Throwable cause) {
		super(message, cause);
		this.module = module;
	}

	public String getModule() {
		return module;
	}

}
