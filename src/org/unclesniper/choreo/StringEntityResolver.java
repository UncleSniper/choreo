package org.unclesniper.choreo;

import java.io.StringReader;
import org.xml.sax.InputSource;

public class StringEntityResolver implements ChoreoEntityResolver {

	private String value;

	public StringEntityResolver(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public InputSource resolveEntity(String choreoID) {
		InputSource source = new InputSource(new StringReader(value == null ? "" : value));
		source.setSystemId("choreo:" + choreoID);
		return source;
	}

}
