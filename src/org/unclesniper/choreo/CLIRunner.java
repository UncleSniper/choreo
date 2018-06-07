package org.unclesniper.choreo;

import org.unclesniper.choreo.clirun.CLIOptions;
import org.unclesniper.choreo.parseopt.OptionLogic;
import org.unclesniper.choreo.parseopt.OptionParser;
import org.unclesniper.choreo.parseopt.StopExecution;
import org.unclesniper.choreo.parseopt.CommandLineException;

public class CLIRunner {

	public static final String THIS_COMMAND = "choreo";

	private static final String[] STRING_ARRAY_TEMPLATE = new String[0];

	public static void main(String[] args) throws Exception {
		CLIOptions options = new CLIOptions(CLIRunner.THIS_COMMAND);
		OptionLogic logic = options.createOptionLogic();
		OptionParser optParser = new OptionParser(logic);
		try {
			optParser.parseWords(args);
			optParser.endCommandLine();
		}
		catch(StopExecution se) {
			System.exit(se.getStatus());
		}
		catch(CommandLineException cle) {
			System.err.println(CLIRunner.THIS_COMMAND + ": " + cle.getMessage());
			System.exit(1);
		}
		String[] rest = optParser.getNonOptionWords().toArray(CLIRunner.STRING_ARRAY_TEMPLATE);
		BuildContext bctx = options.toBuildContext(rest);
		ChoreoTask task = bctx.getRootObject(ChoreoTask.class, false);
		task.execute(bctx.run());
	}

}
