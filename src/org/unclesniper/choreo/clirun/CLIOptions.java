package org.unclesniper.choreo.clirun;

import java.io.File;
import org.unclesniper.choreo.CLIRunner;
import org.unclesniper.choreo.BuildContext;
import org.unclesniper.choreo.PropertyTypeMapper;
import org.unclesniper.choreo.parseopt.WordAction;
import org.unclesniper.choreo.parseopt.OptionLogic;

public class CLIOptions {

	public static final String DEFAULT_COMMAND_PREFIX = "java " + CLIRunner.class.getName();

	public static final String DEFAULT_CHOREOGRAPHY_FILE = "choreography.xml";

	public static final String DEFAULT_COMMON_PROPERTIES_FILE = "common.choreo.properties";

	public static final String DEFAULT_SITE_PROPERTIES_FILE = "site.choreo.properties";

	private String commandPrefix;

	public CLIOptions() {
		this(null);
	}

	public CLIOptions(String commandPrefix) {
		setCommandPrefix(commandPrefix);
	}

	public String getCommandPrefix() {
		return commandPrefix;
	}

	public void setCommandPrefix(String commandPrefix) {
		this.commandPrefix = commandPrefix == null ? CLIOptions.DEFAULT_COMMAND_PREFIX : commandPrefix;
	}

	public void usage() {
		System.err.println("Usage: " + commandPrefix + " [choreo-options...] [script-arguments...]");
		System.err.println("Options:");
		System.err.println("    --choreography=FILE               Read task object graph from given FILE. Note that only the last among");
		System.err.println("                                      all --choreography, --choreo-url and --predef takes effect.");
		System.err.println("                                      Defaults to '" + CLIOptions.DEFAULT_CHOREOGRAPHY_FILE + "'.");
		System.err.println("    -c FILE                           same as --choreography");
		System.err.println("    --choreo-url=URL                  Read task object graph from given URL. See --choreography for notes.");
		System.err.println("    -u URL                            same as --choreo-url");
		System.err.println("    --predef=NAME                     Read task object graph from predefined (builtin) file by the given");
		System.err.println("                                      NAME. See --choreography for notes.");
		System.err.println("    -p NAME                           same as --predef");
		System.err.println("    --common-properties=FILE          Read project entity definitions from given FILE. If --choreography");
		System.err.println("                                      (or none of --choreography, --choreo-url or --predef) is used,");
		System.err.println("                                      and the FILE name does not contain any file separator ('" + File.separator + "')");
		System.err.println("                                      characters, the FILE is expected to be in the same directory as");
		System.err.println("                                      the choreography file. Otherwise, it is expected to be in the");
		System.err.println("                                      JVM working directory.");
		System.err.println("                                      Defaults to '" + CLIOptions.DEFAULT_COMMON_PROPERTIES_FILE + "'.");
		System.err.println("    --site-properties=FILE            Read site entity definitions from given FILE. The pathname is");
		System.err.println("                                      resolved in the same manner as --common-properties.");
		System.err.println("                                      Defaults to '" + CLIOptions.DEFAULT_SITE_PROPERTIES_FILE + "'.");
		System.err.println("    --search-up=true|false            Whether to search all directories from the JVM working directory to the");
		System.err.println("                                      filesystem root for the file given by --choreography, as opposed to only");
		System.err.println("                                      searching the current working directory. Note that this is always");
		System.err.println("                                      inherently 'false' if the name of that file contains a file separator");
		System.err.println("                                      ('" + File.separator + "') character or if --choreo-url or --predef is used.");
		System.err.println("                                      Defaults to 'true'.");
		System.err.println("    --misc-types=true|false           Whether to use builtin non-primitive property type mappers.");
		System.err.println("                                      At present, this includes:");
		{
			BuildContext ctx = new BuildContext();
			ctx.addMiscTypeMappers();
			for(PropertyTypeMapper mapper : ctx.getTypeMappers())
				System.err.println("                                        - " + mapper.getClass().getName());
		}
		System.err.println("                                      Defaults to 'true'.");
		System.err.println("    --refresh-modules[=true|false]    Whether to retrieve modules even if they are already cached.");
		System.err.println("                                      Defaults to 'false' if the option is not used,");
		System.err.println("                                      and to 'true' of the argument is omitted.");
		System.err.println("    -R                                same as --refresh-modules");
	}

	public WordAction getUsageAction() {
		return new UsageAction(this);
	}

	public OptionLogic createOptionLogic() {
		OptionLogic logic = new OptionLogic(
			OptionLogic.FL_SHORT_OPTIONS
			| OptionLogic.FL_DOUBLE_LONG_OPTIONS
			| OptionLogic.FL_WORD_SEPARATORS
			| OptionLogic.FL_SYMBOL_SEPARATORS
			| OptionLogic.FL_INLINE_ARGUMENTS
			| OptionLogic.FL_OPTION_TERMINATOR
			| OptionLogic.FL_BAREWORD_TERMINATES
			| OptionLogic.FL_UNRECOGNIZED_TERMINATES
		);
		logic.addLongOption("help", getUsageAction());
		return logic;
	}

}
