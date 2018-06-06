package org.unclesniper.choreo.clirun;

import java.io.File;
import java.io.IOException;
import org.unclesniper.choreo.PropertyUtils;
import org.unclesniper.choreo.parseopt.CommandLineException;

public class PropertiesAction extends CLIAction {

	public static class PropertiesFileIOException extends CommandLineException {

		public PropertiesFileIOException(IOException cause) {
			super("I/O error in properties file: " + cause.getMessage(), cause);
		}

		public IOException getCause() {
			return (IOException)super.getCause();
		}

	}

	public PropertiesAction(CLIOptions options) {
		super(options);
	}

	public void wordEncountered(String key, String value) throws PropertiesFileIOException {
		try {
			PropertyUtils.parseProperties(new File(value), options);
		}
		catch(IOException ioe) {
			throw new PropertiesFileIOException(ioe);
		}
	}

}
