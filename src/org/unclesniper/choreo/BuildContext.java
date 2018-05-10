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
import org.xml.sax.Locator;
import java.util.LinkedList;
import org.xml.sax.XMLReader;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import java.net.URLClassLoader;
import java.util.logging.Level;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import java.net.MalformedURLException;
import org.xml.sax.helpers.DefaultHandler;
import javax.xml.parsers.SAXParserFactory;
import java.lang.reflect.InvocationTargetException;
import javax.xml.parsers.ParserConfigurationException;

public final class BuildContext {

	private final class BuildHandler extends DefaultHandler {

		private Locator documentLocator;

		public BuildHandler() {}

		public Locator getDocumentLocator() {
			return documentLocator;
		}

		public void setDocumentLocator(Locator documentLocator) {
			this.documentLocator = documentLocator;
		}

		public void fatalError(SAXParseException spe) throws SAXParseException {
			if(currentError == null)
				currentError = new ChoreoSAXParseException(null, spe);
			throw spe;
		}

		public void startElement(String ns, String name, String qname, Attributes attributes) {
			if(currentError != null)
				return;
			if(ns == null || ns.length() == 0 || ns.equals(BuildContext.LANG_XML_NS)) {
				ChoreoElementNestingException.Outer outer;
				PendingObject object;
				if(levelStack.isEmpty()) {
					outer = ChoreoElementNestingException.Outer.DOCUMENT;
					object = null;
				}
				else {
					object = levelStack.getLast().asObject();
					if(object == null)
						outer = ChoreoElementNestingException.Outer.PROPERTY;
					else
						outer = null;
				}
				if(outer != null) {
					currentError = new ChoreoElementNestingException(documentLocator, ns, name, outer);
					return;
				}
				beginProperty(name, attributes, object);
			}
			else
				beginObject(ns, name, attributes);
		}

		private void beginObject(String ns, String name, Attributes attributes) {
			Module module;
			try {
				module = getModule(new URL(ns));
			}
			catch(ChoreoException ce) {
				currentError = ce;
				return;
			}
			catch(MalformedURLException mue) {
				currentError = new ChoreoMalformedModuleURLException(documentLocator, ns, mue);
				return;
			}
			ClassInfo classInfo = module.getClassByElementName(name);
			if(classInfo == null) {
				currentError = new UndefinedElementTypeException(documentLocator, ns, name);
				return;
			}
			Object object;
			try {
				object = classInfo.getLeafConstructor().newInstance();
			}
			catch(InstantiationException ie) {
				currentError = new ElementClassInstantiationException(documentLocator,
						classInfo.getSubject().getName(), ie);
				return;
			}
			catch(IllegalAccessException iae) {
				currentError = new ElementClassInstantiationException(documentLocator,
						classInfo.getSubject().getName(), iae);
				return;
			}
			catch(InvocationTargetException ite) {
				currentError = new ElementClassInstantiationException(documentLocator,
						classInfo.getSubject().getName(), ite);
				return;
			}
			levelStack.addLast(new PendingObject(object, classInfo));
		}

		private void beginProperty(String name, Attributes attributes, PendingObject pendingObject) {
			//TODO
		}

		public void endElement(String ns, String name, String qname) {
			if(currentError != null)
				return;
			Level oldTop = levelStack.removeLast();
			if(levelStack.isEmpty())
				rootObject = oldTop.asObject().object;
			else {
				PendingObject object = oldTop.asObject();
				if(object != null) {
					Level newTop = levelStack.getLast();
					PendingProperty outerProperty = newTop.asProperty();
					if(outerProperty != null) {
						//TODO
					}
					else {
						//TODO
					}
				}
			}
		}

		public void characters(char[] chars, int offset, int length) {
			if(currentError != null)
				return;
			//TODO
		}

	}

	private interface Level {

		PendingObject asObject();

		PendingProperty asProperty();

	}

	private static final class PendingObject implements Level {

		public final Object object;

		public final ClassInfo classInfo;

		public PendingObject(Object object, ClassInfo classInfo) {
			this.object = object;
			this.classInfo = classInfo;
		}

		public PendingObject asObject() {
			return this;
		}

		public PendingProperty asProperty() {
			return null;
		}

	}

	private static final class PendingProperty implements Level {

		public final Object object;

		public final PropertyInfo propertyInfo;

		public PendingProperty(Object object, PropertyInfo propertyInfo) {
			this.object = object;
			this.propertyInfo = propertyInfo;
		}

		public PendingObject asObject() {
			return null;
		}

		public PendingProperty asProperty() {
			return this;
		}

	}

	public static final File DEFAULT_MODULE_DIRECTORY
			= new File(new File(System.getProperty("user.home"), ".choreo"), "modules");

	public static final String LANG_XML_NS = "http://xml.unclesniper.org/choreo/lang/";

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

	private final Deque<Level> levelStack = new LinkedList<Level>();

	private Object rootObject;

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

	public ChoreoException getCurrentError() {
		return currentError;
	}

	public void propagateError() throws ChoreoException {
		if(currentError != null)
			throw currentError;
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
		Locator oldLocator = saxHandler.getDocumentLocator();
		saxHandler.setDocumentLocator(null);
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
		finally {
			saxHandler.setDocumentLocator(oldLocator);
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

	public Object getRootObject() {
		return rootObject;
	}

	public static BuildContext getCurrentContext() {
		return BuildContext.THREAD_LOCAL_CONTEXT.get();
	}

	public static void setCurrentContext(BuildContext context) {
		BuildContext.THREAD_LOCAL_CONTEXT.set(context);
	}

}
