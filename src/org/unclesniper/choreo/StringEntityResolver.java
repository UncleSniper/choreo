package org.unclesniper.choreo;

import java.io.StringReader;
import org.xml.sax.InputSource;

public class StringEntityResolver implements ChoreoEntityResolver {

	private String value;

	private boolean escape;

	public StringEntityResolver(String value) {
		this.value = value;
	}

	public StringEntityResolver(String value, boolean escape) {
		this.value = value;
		this.escape = escape;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public boolean isEscape() {
		return escape;
	}

	public void setEscape(boolean escape) {
		this.escape = escape;
	}

	public InputSource resolveEntity(String choreoID) {
		String v;
		if(value == null)
			v = "";
		else if(escape)
			v = XMLUtils.escape(value);
		else
			v = value;
		InputSource source = new InputSource(new StringReader(v));
		source.setSystemId("choreo:" + choreoID);
		return source;
	}

}
