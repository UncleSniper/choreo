package org.unclesniper.choreo;

import java.io.File;
import java.io.Reader;
import java.io.IOException;
import org.xml.sax.InputSource;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class FileEntityResolver implements ChoreoEntityResolver {

	public static final String DEFAULT_ENCODING = "UTF-8";

	private File path;

	private boolean escape;

	private String encoding;

	public FileEntityResolver(File path) {
		this(path, false);
	}

	public FileEntityResolver(File path, boolean escape) {
		this.path = path;
		this.escape = escape;
	}

	public FileEntityResolver(String path) {
		this(path, false);
	}

	public FileEntityResolver(String path, boolean escape) {
		this(path == null ? null : new File(path), escape);
	}

	public File getPath() {
		return path;
	}

	public void setPath(File path) {
		this.path = path;
	}

	public void setPath(String path) {
		this.path = path == null ? null : new File(path);
	}

	public boolean isEscape() {
		return escape;
	}

	public void setEscape(boolean escape) {
		this.escape = escape;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public InputSource resolveEntity(String choreoID) throws IOException {
		FileInputStream fis = new FileInputStream(path);
		try {
			Reader isr = new InputStreamReader(fis,
					encoding == null ? FileEntityResolver.DEFAULT_ENCODING : encoding);
			Reader reader = escape ? new XMLEscapeReader(isr) : isr;
			InputSource source = new InputSource(reader);
			source.setSystemId("choreo:" + choreoID);
			fis = null;
			return source;
		}
		finally {
			if(fis != null)
				fis.close();
		}
	}

}
