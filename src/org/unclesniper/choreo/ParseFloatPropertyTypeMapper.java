package org.unclesniper.choreo;

public class ParseFloatPropertyTypeMapper extends StaticOneToManyPropertyTypeMapper<String, Object> {

	public static final ParseFloatPropertyTypeMapper instance = new ParseFloatPropertyTypeMapper();

	public ParseFloatPropertyTypeMapper() {
		super(String.class);
		addDestinationType(Float.class, null);
		addDestinationType(Float.TYPE, null);
	}

	protected Float map(BuildContext context, String value, Object symbol) {
		if(value == null)
			throw new IllegalArgumentException("Literal specifier cannot be null");
		try {
			return Float.parseFloat(value);
		}
		catch(NumberFormatException nfe) {
			throw new IllegalArgumentException("Malformed float literal: " + value);
		}
	}

}
