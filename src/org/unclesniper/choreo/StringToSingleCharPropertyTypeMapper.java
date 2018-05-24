package org.unclesniper.choreo;

public class StringToSingleCharPropertyTypeMapper extends StaticOneToManyPropertyTypeMapper<String, Object> {

	public static final StringToSingleCharPropertyTypeMapper instance = new StringToSingleCharPropertyTypeMapper();

	public StringToSingleCharPropertyTypeMapper() {
		super(String.class);
		addDestinationType(Character.class, null);
		addDestinationType(Character.TYPE, null);
	}

	protected Character map(BuildContext context, String value, Object symbol) {
		if(value == null)
			throw new IllegalArgumentException("String-as-character cannot be null");
		if(value.length() != 1)
			throw new IllegalArgumentException("String-as-character must be exactly one character long, not "
					+ value.length());
		return value.charAt(0);
	}

}
