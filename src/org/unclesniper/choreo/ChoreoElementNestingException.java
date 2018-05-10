package org.unclesniper.choreo;

import org.xml.sax.Locator;

public class ChoreoElementNestingException extends ChoreoGraphException {

	public enum Outer {

		DOCUMENT("the document"),
		OBJECT("an object element"),
		PROPERTY("a property element");

		private final String humanReadable;

		private Outer(String humanReadable) {
			this.humanReadable = humanReadable;
		}

		public String getHumanReadable() {
			return humanReadable;
		}

	}

	private final String namespace;

	private final String elementName;

	private final Outer outerContext;

	public ChoreoElementNestingException(XMLLocation location,
			String namespace, String elementName, Outer outerContext) {
		super(location, ChoreoElementNestingException.makeMessage(location, namespace, elementName, outerContext));
		this.namespace = namespace;
		this.elementName = elementName;
		this.outerContext = outerContext;
	}

	public ChoreoElementNestingException(Locator location,
			String namespace, String elementName, Outer outerContext) {
		super(location, ChoreoElementNestingException.makeMessage(new XMLLocation(location),
				namespace, elementName, outerContext));
		this.namespace = namespace;
		this.elementName = elementName;
		this.outerContext = outerContext;
	}

	public String getNamespace() {
		return namespace;
	}

	public String getElementName() {
		return elementName;
	}

	public Outer getOuterContext() {
		return outerContext;
	}

	private static String makeMessage(XMLLocation location,
			String namespace, String elementName, Outer outerContext) {
		StringBuilder builder = new StringBuilder();
		builder.append("Element '");
		if(namespace != null && namespace.length() > 0) {
			builder.append('{');
			builder.append(namespace);
			builder.append('}');
		}
		builder.append(elementName);
		builder.append("' cannot be nested within ");
		builder.append(outerContext.getHumanReadable());
		return builder.toString();
	}

}
