package org.unclesniper.choreo;

public class ServiceObjectPropertyTypeMapper implements PropertyTypeMapper {

	public static final ServiceObjectPropertyTypeMapper instance = new ServiceObjectPropertyTypeMapper();

	public boolean canMapPropertyType(Class<?> fromType, Class<?> toType) {
		return String.class.equals(fromType);
	}

	public Object mapPropertyValue(BuildContext context, Object value, Class<?> toType) {
		if(value == null)
			throw new IllegalArgumentException("Service object key cannot be null");
		String key = (String)value;
		Object sobj = context.getServiceObject(key);
		if(sobj == null)
			throw new IllegalArgumentException("No such service object: " + key);
		if(!toType.isAssignableFrom(sobj.getClass()))
			throw new IllegalArgumentException("Service object '" + key + "' is a " + sobj.getClass().getName()
					+ ", not a " + toType.getName());
		return sobj;
	}

}
