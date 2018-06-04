package org.unclesniper.choreo.parseopt;

public class StopExecution extends RuntimeException {

	private final int status;

	public StopExecution(int status) {
		this.status = status;
	}

	public int getStatus() {
		return status;
	}

}
