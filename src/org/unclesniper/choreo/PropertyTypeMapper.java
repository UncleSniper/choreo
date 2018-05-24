package org.unclesniper.choreo;

public interface PropertyTypeMapper {

	boolean canMapPropertyType(Class<?> fromType, Class<?> toType);

	Object mapPropertyValue(BuildContext context, Object value, Class<?> toType);

}
