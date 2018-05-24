package org.unclesniper.choreo;

public class DecodeIntPropertyTypeMapper extends StaticOneToManyPropertyTypeMapper<String, Object> {

	public static final DecodeIntPropertyTypeMapper instance = new DecodeIntPropertyTypeMapper();

	public DecodeIntPropertyTypeMapper() {
		super(String.class);
		addDestinationType(Integer.class, null);
		addDestinationType(Integer.TYPE, null);
	}

	protected Integer map(BuildContext context, String value, Object symbol) {
		if(value == null)
			throw new IllegalArgumentException("Literal specifier cannot be null");
		try {
			return Integer.decode(value);
		}
		catch(NumberFormatException nfe) {
			throw new IllegalArgumentException("Malformed int literal: " + value);
		}
	}

}
