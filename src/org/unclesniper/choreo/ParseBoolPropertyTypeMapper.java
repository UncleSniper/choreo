package org.unclesniper.choreo;

import java.util.Map;
import java.util.HashMap;

public class ParseBoolPropertyTypeMapper extends StaticOneToManyPropertyTypeMapper<String, Object> {

	public static final ParseBoolPropertyTypeMapper instance = new ParseBoolPropertyTypeMapper();

	private static final Map<String, Boolean> KNOWN_RENDITIONS;

	static {
		KNOWN_RENDITIONS = new HashMap<String, Boolean>();
		KNOWN_RENDITIONS.put("false", false);
		KNOWN_RENDITIONS.put("no", false);
		KNOWN_RENDITIONS.put("off", false);
		KNOWN_RENDITIONS.put("true", true);
		KNOWN_RENDITIONS.put("yes", true);
		KNOWN_RENDITIONS.put("on", true);
	}

	public ParseBoolPropertyTypeMapper() {
		super(String.class);
		addDestinationType(Boolean.class, null);
		addDestinationType(Boolean.TYPE, null);
	}

	protected Boolean map(BuildContext context, String value, Object symbol) {
		if(value == null)
			throw new IllegalArgumentException("Literal specifier cannot be null");
		Boolean b = ParseBoolPropertyTypeMapper.KNOWN_RENDITIONS.get(value.toLowerCase());
		if(b == null)
			throw new IllegalArgumentException("Malformed boolean literal: " + value);
		return b;
	}

}
