package org.unclesniper.choreo;

import java.io.File;
import org.xml.sax.InputSource;

public class CLIRunner {

	public static void main(String[] args) throws Exception {
		BuildContext bctx = new BuildContext();
		bctx.parseDocument(new InputSource(new File(args[0]).toURI().toURL().toString()));
	}

}
