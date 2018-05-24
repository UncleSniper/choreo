package org.unclesniper.choreo;

public class ParseDoublePropertyTypeMapper extends StaticOneToManyPropertyTypeMapper<String, Object> {

	public static final ParseDoublePropertyTypeMapper instance = new ParseDoublePropertyTypeMapper();

	public ParseDoublePropertyTypeMapper() {
		super(String.class);
		addDestinationType(Double.class, null);
		addDestinationType(Double.TYPE, null);
	}

	protected Double map(BuildContext context, String value, Object symbol) {
		if(value == null)
			throw new IllegalArgumentException("Literal specifier cannot be null");
		try {
			return Double.parseDouble(value);
		}
		catch(NumberFormatException nfe) {
			throw new IllegalArgumentException("Malformed double literal: " + value);
		}
	}

}
