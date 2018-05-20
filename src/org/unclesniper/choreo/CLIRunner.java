package org.unclesniper.choreo;

import java.io.File;
import org.xml.sax.InputSource;

public class CLIRunner {

	public static void main(String[] args) throws Exception {
		BuildContext bctx = new BuildContext();
		bctx.setRefreshModules(true);
		bctx.addEntityResolver("narf", new StringEntityResolver("&quot;Heyooo!&quot;"));
		bctx.parseDocument(new InputSource(new File(args[0]).toURI().toURL().toString()));
		bctx.propagateError();
		ChoreoTask task = bctx.getRootObject(ChoreoTask.class, false);
		RunContext rctx = new RunContext(bctx.getServiceRegistry());
		task.execute(rctx);
	}

}
