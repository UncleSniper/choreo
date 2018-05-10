package org.unclesniper.choreo;

import java.net.URL;
import java.io.File;
import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.LinkedList;
import java.util.jar.JarFile;
import java.net.URLConnection;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.regex.Pattern;
import java.io.FileOutputStream;
import java.util.jar.Attributes;
import java.io.InputStreamReader;
import java.net.MalformedURLException;

public final class Module {

	public static final String MANIFEST_SECTION_NAME = "Choreo";

	public static final String MANIFEST_ELEMENT_MAP_KEY = "Element-Map";

	public static final String MANIFEST_DEPENDENCIES_KEY = "Requires";

	private static final char[] HEX_DIGITS = "0123456789ABCDEF".toCharArray();

	private static final Pattern WHITESPACE_SPLIT_PATTERN = Pattern.compile("\\s+");

	private final URL url;

	private final Map<String, ClassInfo> classes = new HashMap<String, ClassInfo>();

	private File jarFile;

	private final Properties elementMap = new Properties();

	private String elementMapEntry;

	private final List<String> dependencies = new LinkedList<String>();

	public Module(URL url) {
		this.url = url;
	}

	public URL getURL() {
		return url;
	}

	public File getJarFile() {
		return jarFile;
	}

	public Properties getElementMap() {
		return elementMap;
	}

	public String getElementMapEntry() {
		return elementMapEntry;
	}

	public Iterable<String> getDependencies() {
		return dependencies;
	}

	public URL getClassPathURL() throws ChoreoIOException {
		try {
			return jarFile == null ? null : jarFile.toURI().toURL();
		}
		catch(MalformedURLException mue) {
			String msg = mue.getMessage();
			throw new ChoreoIOException(null, new IOException("Failed to create URL for module JAR"
					+ (msg == null || msg.length() == 0 ? "" : ": " + msg), mue));
		}
	}

	public void load(BuildContext context) throws ChoreoIOException, IllegalModuleException {
		try {
			File cache = context.getModuleCache();
			byte[] urlBytes = url.toString().getBytes("UTF-8");
			char[] hexChars = new char[urlBytes.length * 2];
			for(int i = 0; i < urlBytes.length; ++i) {
				int v = urlBytes[i] & 0xFF;
				hexChars[i * 2] = Module.HEX_DIGITS[v >>> 4];
				hexChars[i * 2 + 1] = Module.HEX_DIGITS[v & 0x0F];
			}
			jarFile = new File(cache, new String(hexChars) + ".jar");
			if(!jarFile.exists())
				download();
			JarFile jar = new JarFile(jarFile);
			readManifest(jar);
			readElementMap(jar);
		}
		catch(IOException ioe) {
			throw new ChoreoIOException(null, ioe);
		}
	}

	private void download() throws IOException {
		boolean finished = false;
		try(FileOutputStream outs = new FileOutputStream(jarFile)) {
			URLConnection conn = url.openConnection();
			conn.connect();
			try(InputStream ins = conn.getInputStream()) {
				byte[] buffer = new byte[256];
				for(;;) {
					int count = ins.read(buffer);
					if(count <= 0)
						break;
					outs.write(buffer, 0, count);
				}
			}
		}
		if(!finished)
			jarFile.delete();
	}

	private void readManifest(JarFile jar) throws IOException {
		Manifest mf = jar.getManifest();
		if(mf == null)
			return;
		Attributes attrs = mf.getAttributes(Module.MANIFEST_SECTION_NAME);
		if(attrs == null)
			return;
		elementMapEntry = attrs.getValue(Module.MANIFEST_ELEMENT_MAP_KEY);
		String requires = attrs.getValue(Module.MANIFEST_DEPENDENCIES_KEY);
		if(requires != null) {
			for(String dep : Module.WHITESPACE_SPLIT_PATTERN.split(requires)) {
				if(!dep.isEmpty())
					dependencies.add(dep);
			}
		}
	}

	private void readElementMap(JarFile jar) throws IllegalModuleException, IOException {
		if(elementMapEntry == null)
			return;
		ZipEntry entry = jar.getEntry(elementMapEntry);
		if(entry == null)
			throw new IllegalModuleException(url.toString(), "Missing element map zip entry ("
					+ elementMapEntry + ") in module " + url);
		try(InputStream is = jar.getInputStream(entry)) {
			elementMap.load(new InputStreamReader(is, "UTF-8"));
		}
	}

	public void bind(BuildContext context) throws IllegalModuleException {
		ClassLoader loader = context.getCurrentClassLoader();
		if(loader == null)
			loader = BuildContext.class.getClassLoader();
		String modname = url.toString();
		for(String key : elementMap.stringPropertyNames()) {
			String value = elementMap.getProperty(key);
			if(value.startsWith("$")) {
				ClassInfo ref = classes.get(value.substring(1));
				if(ref == null) {
					classes.put(key, ref);
					continue;
				}
			}
			Class<?> clazz;
			try {
				clazz = loader.loadClass(value);
			}
			catch(ClassNotFoundException cnfe) {
				throw new IllegalModuleException(url.toString(), "Undefined element class '"
						+ value + "' in module " + url);
			}
			classes.put(key, new ClassInfo(clazz, modname));
		}
	}

	public ClassInfo getClassByElementName(String name) {
		return classes.get(name);
	}

}
