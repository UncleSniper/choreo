package org.unclesniper.choreo;

public abstract class OneToOnePropertyTypeMapper<SourceT, DestinationT> implements PropertyTypeMapper {

	public OneToOnePropertyTypeMapper() {}

	protected abstract Class<SourceT> getSourceType();

	protected abstract Class<DestinationT> getDestinationType();

	protected abstract DestinationT map(BuildContext context, SourceT value);

	public boolean canMapPropertyType(Class<?> fromType, Class<?> toType) {
		Class<SourceT> source = getSourceType();
		if(source != null && (fromType == null || !source.isAssignableFrom(fromType)))
			return false;
		if(fromType == null && toType.isPrimitive())
			return false;
		return toType.isAssignableFrom(getDestinationType());
	}

	public Object mapPropertyValue(BuildContext context, Object value, Class<?> toType) {
		return map(context, value == null ? null : getSourceType().cast(value));
	}

}
