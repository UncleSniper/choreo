package org.unclesniper.choreo;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;

public final class PropertyUtils {

	public interface EntitySink {

		void addEntityResolver(String key, ChoreoEntityResolver resolver);

	}

	public static class MalformedURLPropertyException extends IOException {

		private final String property;

		private final String url;

		private final String file;

		public MalformedURLPropertyException(String property, String url) {
			this(property, url, null);
		}

		public MalformedURLPropertyException(String property, String url, String file) {
			super("Property '" + property + (file == null ? "" : "' in file '" + file)
					+ "' does not reference a valid URL: " + url);
			this.property = property;
			this.url = url;
			this.file = file;
		}

		public String getProperty() {
			return property;
		}

		public String getUrl() {
			return url;
		}

		public String getFile() {
			return file;
		}

	}

	private PropertyUtils() {}

	public static void parseProperty(String key, String value, EntitySink sink) throws MalformedURLException {
		boolean escape = key.startsWith("q.") && key.length() > 2;
		if(escape)
			key = key.substring(2);
		if(key.startsWith("string.") && key.length() > 7)
			sink.addEntityResolver(key.substring(7), new StringEntityResolver(value, escape));
		else if(key.startsWith("file.") && key.length() > 5)
			sink.addEntityResolver(key.substring(5), new FileEntityResolver(value, escape));
		else if(key.startsWith("url.") && key.length() > 4)
			sink.addEntityResolver(key.substring(4), new URLEntityResolver(value, escape));
		else
			sink.addEntityResolver(key, new StringEntityResolver(value, escape));
	}

	public static void parseProperties(Properties properties, String file, EntitySink sink)
			throws MalformedURLPropertyException {
		for(String key : properties.stringPropertyNames()) {
			String value = properties.getProperty(key);
			try {
				PropertyUtils.parseProperty(key, value, sink);
			}
			catch(MalformedURLException mue) {
				throw new MalformedURLPropertyException(key, value, file);
			}
		}
	}

	public static void parseProperties(File file, EntitySink sink) throws IOException {
		Properties properties = new Properties();
		try(FileInputStream fis = new FileInputStream(file)) {
			properties.load(new InputStreamReader(fis, "UTF-8"));
		}
		PropertyUtils.parseProperties(properties, file.getPath(), sink);
	}

}
