package org.unclesniper.choreo;

public class DecodeBytePropertyTypeMapper extends StaticOneToManyPropertyTypeMapper<String, Object> {

	public static final DecodeBytePropertyTypeMapper instance = new DecodeBytePropertyTypeMapper();

	public DecodeBytePropertyTypeMapper() {
		super(String.class);
		addDestinationType(Byte.class, null);
		addDestinationType(Byte.TYPE, null);
	}

	protected Byte map(BuildContext context, String value, Object symbol) {
		if(value == null)
			throw new IllegalArgumentException("Literal specifier cannot be null");
		try {
			return Byte.decode(value);
		}
		catch(NumberFormatException nfe) {
			throw new IllegalArgumentException("Malformed byte literal: " + value);
		}
	}

}
