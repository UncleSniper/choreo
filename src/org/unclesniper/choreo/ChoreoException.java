package org.unclesniper.choreo;

public abstract class ChoreoException extends Exception {

	public ChoreoException(String message) {
		super(message);
	}

	public ChoreoException(String message, Throwable cause) {
		super(message, cause);
	}

}
