package org.unclesniper.choreo;

public interface ChoreoTask {

	void execute(RunContext context) throws ChoreoRunException;

}
