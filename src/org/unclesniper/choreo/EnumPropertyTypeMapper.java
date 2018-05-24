package org.unclesniper.choreo;

public class EnumPropertyTypeMapper implements PropertyTypeMapper {

	public static final EnumPropertyTypeMapper instance = new EnumPropertyTypeMapper();

	public EnumPropertyTypeMapper() {}

	public boolean canMapPropertyType(Class<?> fromType, Class<?> toType) {
		return String.class.equals(fromType) && Enum.class.isAssignableFrom(toType) && !toType.equals(Enum.class);
	}

	@SuppressWarnings("unchecked")
	public Object mapPropertyValue(BuildContext context, Object value, Class<?> toType) {
		if(value == null)
			throw new IllegalArgumentException("Enum constant name cannot be null");
		return Enum.valueOf((Class)toType, (String)value);
	}

}
