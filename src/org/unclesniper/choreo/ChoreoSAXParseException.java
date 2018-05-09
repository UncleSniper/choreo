package org.unclesniper.choreo;

import org.xml.sax.SAXParseException;

public class ChoreoSAXParseException extends ChoreoSAXException {

	public ChoreoSAXParseException(String message, SAXParseException cause) {
		super(message, cause);
	}

	public SAXParseException getCause() {
		return (SAXParseException)super.getCause();
	}

	public int getLine() {
		SAXParseException cause = getCause();
		return cause == null ? -1 : cause.getLineNumber();
	}

	public int getColumn() {
		SAXParseException cause = getCause();
		return cause == null ? -1 : cause.getColumnNumber();
	}

	public String getPublicID() {
		SAXParseException cause = getCause();
		return cause == null ? null : cause.getPublicId();
	}

	public String getSystemID() {
		SAXParseException cause = getCause();
		return cause == null ? null : cause.getSystemId();
	}

}
