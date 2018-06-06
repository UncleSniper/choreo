package org.unclesniper.choreo.clirun;

import java.net.MalformedURLException;
import org.unclesniper.choreo.PropertyUtils;
import org.unclesniper.choreo.parseopt.IllegalOptionArgumentException;

public class PropertyAction extends CLIAction {

	public static class MalformedURLPropertyException extends IllegalOptionArgumentException {

		private final String property;

		public MalformedURLPropertyException(String optionRendition, String property, String url) {
			super(optionRendition, "a valid property definition, with a valid URL for property '"
					+ property + '\'', url);
			this.property = property;
		}

		public String getProperty() {
			return property;
		}

	}

	public PropertyAction(CLIOptions options) {
		super(options);
	}

	public void wordEncountered(String key, String value) throws IllegalOptionArgumentException {
		int pos = value.indexOf('=');
		if(pos <= 0)
			throw new IllegalOptionArgumentException(key.length() == 1 ? "-P" : "--property",
					"a key=value pair", value);
		String pkey = value.substring(0, pos), pvalue = value.substring(pos + 1);
		try {
			PropertyUtils.parseProperty(pkey, pvalue, options);
		}
		catch(MalformedURLException mue) {
			throw new MalformedURLPropertyException(key.length() == 1 ? "-P" : "--property", pkey, pvalue);
		}
	}

}
