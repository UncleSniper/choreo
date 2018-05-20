package org.unclesniper.choreo;

public abstract class ChoreoRunException extends ChoreoException {

	public ChoreoRunException(String message) {
		super(message);
	}

	public ChoreoRunException(String message, Throwable cause) {
		super(message, cause);
	}

}
