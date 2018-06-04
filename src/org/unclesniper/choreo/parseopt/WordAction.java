package org.unclesniper.choreo.parseopt;

public interface WordAction {

	void wordEncountered(String key, String value) throws CommandLineException;

}
