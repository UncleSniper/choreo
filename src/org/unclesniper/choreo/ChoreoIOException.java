package org.unclesniper.choreo;

import java.io.IOException;

public class ChoreoIOException extends ChoreoException {

	public ChoreoIOException(String message, IOException cause) {
		super(message == null && cause != null ? cause.getMessage() : message, cause);
	}

	public IOException getCause() {
		return (IOException)super.getCause();
	}

}
