package org.unclesniper.choreo;

import org.xml.sax.Locator;

public class UnknownChoreoAttributeException extends ChoreoGraphException {

	private final String namespace;

	private final String localName;

	public UnknownChoreoAttributeException(XMLLocation location, String namespace, String localName) {
		super(location, "Unrecognized attribute '{" + namespace + '}' + localName + '\''
				+ (location == null ? "" : " at " + location));
		this.namespace = namespace;
		this.localName = localName;
	}

	public UnknownChoreoAttributeException(Locator location, String namespace, String localName) {
		this(location == null ? null : new XMLLocation(location), namespace, localName);
	}

	public String getNamespace() {
		return namespace;
	}

	public String getLocalName() {
		return localName;
	}

}
