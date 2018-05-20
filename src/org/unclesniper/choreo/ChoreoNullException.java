package org.unclesniper.choreo;

public class ChoreoNullException extends ChoreoException {

	public ChoreoNullException() {
		super("Object graph yields null");
	}

}
