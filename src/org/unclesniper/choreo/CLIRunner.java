package org.unclesniper.choreo;

import java.io.File;
import org.xml.sax.InputSource;

public class CLIRunner {

	public static void main(String[] args) throws Exception {
		BuildContext bctx = new BuildContext();
		bctx.addDecodePrimitiveTypeMappers();
		bctx.addMiscTypeMappers();
		bctx.setRefreshModules(true);
		bctx.addEntityResolver("narf", new StringEntityResolver("&quot;Heyooo!&quot;"));
		bctx.parseFile(args[0]);
		bctx.propagateError();
		ChoreoTask task = bctx.getRootObject(ChoreoTask.class, false);
		task.execute(bctx.run());
	}

}
