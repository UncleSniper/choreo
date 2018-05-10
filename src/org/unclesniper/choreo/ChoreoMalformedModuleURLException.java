package org.unclesniper.choreo;

import org.xml.sax.Locator;
import java.net.MalformedURLException;

public class ChoreoMalformedModuleURLException extends ChoreoGraphException {

	private final String moduleURL;

	public ChoreoMalformedModuleURLException(XMLLocation location, String moduleURL, MalformedURLException cause) {
		super(location, "Malformed module URL" + (location == null ? "" : " at " + location)
				+ ": " + moduleURL + (cause == null || cause.getMessage() == null ? ""
				: ": " + cause.getMessage()), cause);
		this.moduleURL = moduleURL;
	}

	public ChoreoMalformedModuleURLException(Locator location, String moduleURL, MalformedURLException cause) {
		this(location == null ? null : new XMLLocation(location), moduleURL, cause);
	}

	public String getModuleURL() {
		return moduleURL;
	}

	public MalformedURLException getCause() {
		return (MalformedURLException)super.getCause();
	}

}
