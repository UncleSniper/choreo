package org.unclesniper.choreo;

import java.util.List;
import java.util.LinkedList;

public class ChoreoCyclicDependencyException extends ChoreoException {

	private final List<String> cyclePath = new LinkedList<String>();

	public ChoreoCyclicDependencyException(Iterable<String> cyclePath) {
		super("Module '" + cyclePath.iterator().next() + "' ultimately depends on itself");
		for(String ns : cyclePath)
			this.cyclePath.add(ns);
	}

	public Iterable<String> getCyclePath() {
		return cyclePath;
	}

}
