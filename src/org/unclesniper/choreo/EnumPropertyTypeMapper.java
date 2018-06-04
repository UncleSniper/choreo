package org.unclesniper.choreo;

import java.util.Map;
import java.util.HashMap;

public class EnumPropertyTypeMapper implements PropertyTypeMapper {

	public static final EnumPropertyTypeMapper instance = new EnumPropertyTypeMapper();

	private final Map<Class<?>, Boolean> allCapital = new HashMap<Class<?>, Boolean>();

	public EnumPropertyTypeMapper() {}

	public boolean canMapPropertyType(Class<?> fromType, Class<?> toType) {
		return String.class.equals(fromType) && Enum.class.isAssignableFrom(toType) && !toType.equals(Enum.class);
	}

	@SuppressWarnings("unchecked")
	public Object mapPropertyValue(BuildContext context, Object value, Class<?> toType) {
		if(value == null)
			throw new IllegalArgumentException("Enum constant name cannot be null");
		String name = (String)value;
		Boolean cap = allCapital.get(toType);
		if(cap == null) {
			cap = EnumPropertyTypeMapper.areAllCapital((Class)toType);
			allCapital.put(toType, cap);
		}
		if(cap)
			name = name.toUpperCase();
		return Enum.valueOf((Class)toType, name);
	}

	private static boolean areAllCapital(Class<? extends Enum<?>> clazz) {
		Enum<?>[] constants = clazz.getEnumConstants();
		if(constants == null)
			return false;
		for(Enum<?> constant : constants) {
			String name = constant.name();
			if(!name.equals(name.toUpperCase()))
				return false;
		}
		return true;
	}

}
