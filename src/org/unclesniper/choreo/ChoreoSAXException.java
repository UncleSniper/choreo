package org.unclesniper.choreo;

import org.xml.sax.SAXException;

public class ChoreoSAXException extends ChoreoException {

	public ChoreoSAXException(String message, SAXException cause) {
		super(message == null && cause != null ? cause.getMessage() : message, cause);
	}

	public SAXException getCause() {
		return (SAXException)super.getCause();
	}

}
