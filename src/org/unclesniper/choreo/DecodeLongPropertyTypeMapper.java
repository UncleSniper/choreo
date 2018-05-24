package org.unclesniper.choreo;

public class DecodeLongPropertyTypeMapper extends StaticOneToManyPropertyTypeMapper<String, Object> {

	public static final DecodeLongPropertyTypeMapper instance = new DecodeLongPropertyTypeMapper();

	public DecodeLongPropertyTypeMapper() {
		super(String.class);
		addDestinationType(Long.class, null);
		addDestinationType(Long.TYPE, null);
	}

	protected Long map(BuildContext context, String value, Object symbol) {
		if(value == null)
			throw new IllegalArgumentException("Literal specifier cannot be null");
		try {
			return Long.decode(value);
		}
		catch(NumberFormatException nfe) {
			throw new IllegalArgumentException("Malformed long literal: " + value);
		}
	}

}
