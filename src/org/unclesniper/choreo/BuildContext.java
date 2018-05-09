package org.unclesniper.choreo;

import java.net.URL;
import java.io.File;
import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.io.IOException;
import java.util.LinkedList;
import org.xml.sax.XMLReader;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import java.net.URLClassLoader;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import java.net.MalformedURLException;
import org.xml.sax.helpers.DefaultHandler;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.ParserConfigurationException;

public final class BuildContext {

	private final class BuildHandler extends DefaultHandler {

		public BuildHandler() {}

		public void fatalError(SAXParseException spe) throws SAXParseException {
			if(currentError == null)
				currentError = new ChoreoSAXParseException(null, spe);
			throw spe;
		}

		public void startElement(String ns, String name, String qname, Attributes attributes) {
			if(currentError != null)
				return;
			//TODO
		}

		public void endElement(String ns, String name, String qname) {
			if(currentError != null)
				return;
			//TODO
		}

		public void characters(char[] chars, int offset, int length) {
			if(currentError != null)
				return;
			//TODO
		}

	}

	public static final File DEFAULT_MODULE_DIRECTORY
			= new File(new File(System.getProperty("user.home"), ".choreo"), "modules");

	private static final ThreadLocal<BuildContext> THREAD_LOCAL_CONTEXT = new ThreadLocal<BuildContext>();

	private static final URL[] URL_ARRAY_TEMPLATE = new URL[0];

	private final BuildHandler saxHandler = new BuildHandler();

	private ClassLoader currentClassLoader;

	private ChoreoException currentError;

	private final Map<String, Module> modules = new HashMap<String, Module>();

	private final Set<String> loadingSet = new HashSet<String>();

	private final Deque<String> loadingStack = new LinkedList<String>();

	private final Deque<Module> pendingBinds = new LinkedList<Module>();

	private File moduleDirectory;

	public BuildContext() {}

	public BuildContext(ClassLoader currentClassLoader) {
		this.currentClassLoader = currentClassLoader;
	}

	public ClassLoader getCurrentClassLoader() {
		return currentClassLoader;
	}

	public void setCurrentClassLoader(ClassLoader currentClassLoader) {
		this.currentClassLoader = currentClassLoader;
	}

	public boolean isContextBroken() {
		return currentError != null;
	}

	public File getModuleDirectory() {
		return moduleDirectory;
	}

	public void setModuleDirectory(File moduleDirectory) {
		this.moduleDirectory = moduleDirectory;
	}

	public void parseDocument(InputSource source) throws ChoreoException, IOException {
		SAXParserFactory spf = SAXParserFactory.newInstance();
		spf.setNamespaceAware(true);
		XMLReader xmlReader;
		try {
			xmlReader = spf.newSAXParser().getXMLReader();
			xmlReader.setContentHandler(saxHandler);
			xmlReader.setErrorHandler(saxHandler);
			xmlReader.parse(source);
		}
		catch(ParserConfigurationException pce) {
			String msg = pce.getMessage();
			throw new IOException("No XML parser available"
					+ (msg == null || msg.length() == 0 ? "" : ": " + msg), pce);
		}
		catch(SAXParseException spe) {
			throw new ChoreoSAXParseException(null, spe);
		}
		catch(SAXException se) {
			throw new ChoreoSAXException(null, se);
		}
	}

	public Module getModule(URL url)
			throws ChoreoCyclicDependencyException, ChoreoIOException, IllegalModuleException {
		String ns = url.toString();
		Module module = modules.get(ns);
		if(module != null)
			return module;
		if(loadingSet.contains(ns)) {
			List<String> cyclePath = new LinkedList<String>();
			boolean found = false;
			for(String cpns : loadingStack) {
				if(found)
					cyclePath.add(cpns);
				else if(cpns.equals(ns)) {
					found = true;
					cyclePath.add(cpns);
				}
			}
			throw new ChoreoCyclicDependencyException(cyclePath);
		}
		module = new Module(url);
		loadingSet.add(ns);
		try {
			loadingStack.addLast(ns);
			try {
				module.load(this);
				pendingBinds.add(module);
				for(String dep : module.getDependencies()) {
					URL depurl;
					try {
						depurl = new URL(dep);
					}
					catch(MalformedURLException mue) {
						String msg = mue.getMessage();
						throw new IllegalModuleException(ns, "Module '" + ns + "' declares dependency on '"
								+ dep + "', which is not a valid URL"
								+ (msg == null || msg.length() == 0 ? "" : ": " + msg));
					}
					getModule(depurl);
				}
			}
			finally {
				loadingStack.removeLast();
			}
		}
		finally {
			loadingSet.remove(ns);
		}
		modules.put(ns, module);
		if(loadingSet.isEmpty()) {
			try {
				List<URL> cpelems = new LinkedList<URL>();
				for(Module bmod : pendingBinds) {
					URL cp = bmod.getClassPathURL();
					if(cp != null)
						cpelems.add(cp);
					URL[] cpurls = cpelems.toArray(BuildContext.URL_ARRAY_TEMPLATE);
					currentClassLoader = new URLClassLoader(cpurls,
							currentClassLoader == null ? BuildContext.class.getClassLoader() : currentClassLoader);
				}
				for(Module bmod : pendingBinds)
					bmod.bind(this);
			}
			finally {
				pendingBinds.clear();
			}
		}
		return module;
	}

	public File getModuleCache() throws IOException {
		File cache = moduleDirectory == null ? BuildContext.DEFAULT_MODULE_DIRECTORY : moduleDirectory;
		if(!cache.isDirectory()) {
			if(!cache.mkdirs())
				throw new IOException("Failed to create directory: " + cache.getAbsolutePath());
		}
		return cache;
	}

	public static BuildContext getCurrentContext() {
		return BuildContext.THREAD_LOCAL_CONTEXT.get();
	}

	public static void setCurrentContext(BuildContext context) {
		BuildContext.THREAD_LOCAL_CONTEXT.set(context);
	}

}
