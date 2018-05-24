package org.unclesniper.choreo;

public class DecodeShortPropertyTypeMapper extends StaticOneToManyPropertyTypeMapper<String, Object> {

	public static final DecodeShortPropertyTypeMapper instance = new DecodeShortPropertyTypeMapper();

	public DecodeShortPropertyTypeMapper() {
		super(String.class);
		addDestinationType(Short.class, null);
		addDestinationType(Short.TYPE, null);
	}

	protected Short map(BuildContext context, String value, Object symbol) {
		if(value == null)
			throw new IllegalArgumentException("Literal specifier cannot be null");
		try {
			return Short.decode(value);
		}
		catch(NumberFormatException nfe) {
			throw new IllegalArgumentException("Malformed short literal: " + value);
		}
	}

}
