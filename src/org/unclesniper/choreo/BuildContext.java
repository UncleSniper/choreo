package org.unclesniper.choreo;

import java.net.URL;
import java.io.File;
import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.io.IOException;
import org.xml.sax.Locator;
import java.util.LinkedList;
import org.xml.sax.XMLReader;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import java.net.URLClassLoader;
import java.util.logging.Level;
import org.xml.sax.SAXException;
import java.lang.reflect.Method;
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
			if(charBuffer != null)
				processText();
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

		private Module getModuleFromXML(String ns) {
			try {
				return getModule(new URL(ns));
			}
			catch(ChoreoException ce) {
				currentError = ce;
				return null;
			}
			catch(MalformedURLException mue) {
				currentError = new ChoreoMalformedModuleURLException(documentLocator, ns, mue);
				return null;
			}
		}

		private void beginObject(String ns, String name, Attributes attributes) {
			Module module = getModuleFromXML(ns);
			if(module == null)
				return;
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
			for(Method injector : classInfo.getContextInjectors()) {
				try {
					injector.invoke(object, BuildContext.this);
				}
				catch(IllegalAccessException iae) {
					currentError = new PropertyAccessException(documentLocator,
							classInfo.getSubject().getName(), null, injector.toString(), iae);
					return;
				}
				catch(IllegalArgumentException iae) {
					currentError = new PropertyAccessException(documentLocator,
							classInfo.getSubject().getName(), null, injector.toString(), iae);
					return;
				}
				catch(InvocationTargetException ite) {
					currentError = new PropertyAccessException(documentLocator,
							classInfo.getSubject().getName(), null, injector.toString(), ite);
					return;
				}
			}
			boolean skip = processObjectAttributes(object, classInfo, attributes);
			levelStack.addLast(new PendingObject(object, classInfo, skip));
		}

		private boolean processObjectAttributes(Object object, ClassInfo classInfo, Attributes attributes) {
			boolean skip = false;
			final int count = attributes.getLength();
			for(int i = 0; i < count && currentError == null; ++i) {
				String name = attributes.getLocalName(i);
				if(name.length() == 0)
					attributes.getQName(i);
				String ns = attributes.getURI(i);
				if(ns.length() == 0)
					ns = null;
				String value = attributes.getValue(i);
				if(ns == null)
					processPropertyAttribute(object, classInfo, name, value);
				else if(ns.equals(BuildContext.LANG_XML_NS))
					skip = processLangAttribute(object, name, value) || skip;
				else
					skip = processCustomAttribute(object, classInfo, ns, name, value) || skip;
			}
			return skip;
		}

		private void processPropertyAttribute(Object object, ClassInfo classInfo, String name, String value) {
			setObjectProperty(object, classInfo,
					classInfo.getSetter(name), ClassInfo.AccessorType.SETTER, name, value, false);
		}

		private boolean processLangAttribute(Object object, String name, String value) {
			if(name.equals(BuildContext.LANG_SERVICE_ATTR)) {
				putServiceObject(value, object);
				return false;
			}
			if(name.equals(BuildContext.LANG_GLOBAL_SERVICE_ATTR)) {
				putServiceObject(value, object);
				return true;
			}
			currentError = new UnknownChoreoAttributeException(documentLocator, BuildContext.LANG_XML_NS, name);
			return false;
		}

		private boolean processCustomAttribute(Object object, ClassInfo classInfo,
				String ns, String name, String value) {
			Module module = getModuleFromXML(ns);
			if(module == null)
				return false;
			CustomAttributeHandler handler = module.getCustomAttributeHandler(name);
			if(handler == null) {
				currentError = new UnhandledCustomAttributeException(documentLocator, ns, name);
				return false;
			}
			try {
				return handler.handleAttribute(BuildContext.this, object, classInfo, name, value);
			}
			catch(ChoreoException ce) {
				currentError = ce;
				return false;
			}
		}

		private void beginProperty(String name, Attributes attributes, PendingObject pendingObject) {
			PropertyInfo propertyInfo = pendingObject.classInfo.getAdder(name);
			if(propertyInfo == null) {
				currentError = new NoSuchPropertyException(documentLocator,
						pendingObject.classInfo.getSubject().getName(), name, ClassInfo.AccessorType.ADDER);
				return;
			}
			if(attributes.getLength() > 0) {
				currentError = new PropertyWithAttributesException(documentLocator, name);
				return;
			}
			levelStack.addLast(new PendingProperty(pendingObject.object, pendingObject.classInfo, propertyInfo));
		}

		public void endElement(String ns, String name, String qname) {
			if(currentError != null)
				return;
			if(charBuffer != null)
				processText();
			Level oldTop = levelStack.removeLast();
			if(levelStack.isEmpty())
				rootObject = oldTop.asObject().object;
			else {
				PendingObject object = oldTop.asObject();
				if(object != null) {
					// popping an object
					Level newTop = levelStack.getLast();
					PendingProperty outerProperty = newTop.asProperty();
					if(outerProperty != null) {
						PendingObject outerObject = newTop.asObject();
						PropertyInfo propertyInfo = outerObject.classInfo.getDefaultAdder();
						if(propertyInfo == null)
							propertyInfo = BuildContext.EMPTY_DEFAULT_ADDER_PROPERTY_INFO;
						setObjectProperty(outerObject.object, outerObject.classInfo, propertyInfo,
								ClassInfo.AccessorType.ADDER, null, object.object, false);
					}
					else
						setObjectProperty(outerProperty.object, outerProperty.classInfo,
								outerProperty.propertyInfo, ClassInfo.AccessorType.ADDER,
								outerProperty.propertyInfo.getName(), object.object, false);
				}
				else {
					// popping a property
				}
			}
		}

		private void setObjectProperty(Object object, ClassInfo classInfo, PropertyInfo propertyInfo,
				ClassInfo.AccessorType accessorType, String name, Object value, boolean isIgnorableWhitespace) {
			if(propertyInfo == null) {
				currentError = new NoSuchPropertyException(documentLocator,
						classInfo.getSubject().getName(), name, accessorType);
				return;
			}
			List<AccessorInfo> candidates = new LinkedList<AccessorInfo>();
			for(Class<?> propertyType : propertyInfo.getAccessorTypes()) {
				AccessorInfo propertyAccessor = propertyInfo.getAccessor(propertyType);
				if(propertyType.isPrimitive()) {
					if(value == null)
						continue;
					propertyType = BuildContext.VALUE_TYPE_TO_REFERENCE_TYPE.get(propertyType);
				}
				if(value != null && !propertyType.isAssignableFrom(value.getClass()))
					continue;
				Iterator<AccessorInfo> cid = candidates.iterator();
				boolean add = true;
				while(cid.hasNext()) {
					AccessorInfo candidate = cid.next();
					if(candidate.getType().isAssignableFrom(propertyType))
						cid.remove();
					else if(propertyType.isAssignableFrom(candidate.getType()))
						add = false;
				}
				if(add)
					candidates.add(propertyAccessor);
			}
			String fauxName;
			if(name != null)
				fauxName = name;
			else if(accessorType == ClassInfo.AccessorType.ADDER)
				fauxName = "<default adder>";
			else
				fauxName = "???";
			switch(candidates.size()) {
				case 0:
					break;
				case 1:
					{
						AccessorInfo accessor = candidates.get(0);
						try {
							accessor.getMethod().invoke(object, value);
						}
						catch(IllegalAccessException iae) {
							currentError = new PropertyAccessException(documentLocator,
									classInfo.getSubject().getName(), fauxName,
									accessor.getMethod().toString(), iae);
						}
						catch(IllegalArgumentException iae) {
							currentError = new PropertyAccessException(documentLocator,
									classInfo.getSubject().getName(), fauxName,
									accessor.getMethod().toString(), iae);
						}
						catch(InvocationTargetException ite) {
							currentError = new PropertyAccessException(documentLocator,
									classInfo.getSubject().getName(), fauxName,
									accessor.getMethod().toString(), ite);
						}
						return;
					}
				default:
					currentError = new AmbiguousAccessorException(documentLocator,
							accessorType, fauxName, classInfo, value == null ? null : value.getClass());
					return;
			}
			//TODO: check mappers
		}

		public void characters(char[] chars, int offset, int length) {
			if(currentError != null)
				return;
			if(charBuffer == null)
				charBuffer = new StringBuilder();
			charBuffer.append(chars, offset, length);
		}

		private void processText() {
			String text = charBuffer.toString();
			charBuffer = null;
			if(levelStack.isEmpty())
				return;
			Level top = levelStack.getLast();
			PendingObject object = top.asObject();
			if(object != null)
				setObjectProperty(object.object, object.classInfo, BuildContext.EMPTY_DEFAULT_ADDER_PROPERTY_INFO,
						ClassInfo.AccessorType.ADDER, null, text, text.trim().isEmpty());
			else {
				PendingProperty property = top.asProperty();
				setObjectProperty(property.object, property.classInfo, property.propertyInfo,
						ClassInfo.AccessorType.ADDER, property.propertyInfo.getName(), text, text.trim().isEmpty());
			}
		}

	}

	private interface Level {

		PendingObject asObject();

		PendingProperty asProperty();

	}

	private static final class PendingObject implements Level {

		public final Object object;

		public final ClassInfo classInfo;

		public final boolean skip;

		public PendingObject(Object object, ClassInfo classInfo, boolean skip) {
			this.object = object;
			this.classInfo = classInfo;
			this.skip = skip;
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

		private final ClassInfo classInfo;

		public final PropertyInfo propertyInfo;

		public PendingProperty(Object object, ClassInfo classInfo, PropertyInfo propertyInfo) {
			this.object = object;
			this.classInfo = classInfo;
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

	private static final String LANG_SERVICE_ATTR = "service";

	private static final String LANG_GLOBAL_SERVICE_ATTR = "globalService";

	private static final ThreadLocal<BuildContext> THREAD_LOCAL_CONTEXT = new ThreadLocal<BuildContext>();

	private static final URL[] URL_ARRAY_TEMPLATE = new URL[0];

	private static final PropertyInfo EMPTY_DEFAULT_ADDER_PROPERTY_INFO = new PropertyInfo(null);

	private static final Map<Class<?>, Class<?>> VALUE_TYPE_TO_REFERENCE_TYPE;

	static {
		VALUE_TYPE_TO_REFERENCE_TYPE = new HashMap<Class<?>, Class<?>>();
		VALUE_TYPE_TO_REFERENCE_TYPE.put(Byte.TYPE, Byte.class);
		VALUE_TYPE_TO_REFERENCE_TYPE.put(Short.TYPE, Short.class);
		VALUE_TYPE_TO_REFERENCE_TYPE.put(Integer.TYPE, Integer.class);
		VALUE_TYPE_TO_REFERENCE_TYPE.put(Long.TYPE, Long.class);
		VALUE_TYPE_TO_REFERENCE_TYPE.put(Character.TYPE, Character.class);
		VALUE_TYPE_TO_REFERENCE_TYPE.put(Float.TYPE, Float.class);
		VALUE_TYPE_TO_REFERENCE_TYPE.put(Double.TYPE, Double.class);
		VALUE_TYPE_TO_REFERENCE_TYPE.put(Boolean.TYPE, Boolean.class);
	}

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

	private final Map<String, Object> serviceObjects = new HashMap<String, Object>();

	private StringBuilder charBuffer;

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

	public Iterable<String> getServiceObjectKeys() {
		return serviceObjects.keySet();
	}

	public Object getServiceObject(String key) {
		return serviceObjects.get(key);
	}

	public void putServiceObject(String key, Object value) {
		if(key == null)
			throw new IllegalArgumentException("Service object key cannot be null");
		if(value == null)
			serviceObjects.remove(key);
		else
			serviceObjects.put(key, value);
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
