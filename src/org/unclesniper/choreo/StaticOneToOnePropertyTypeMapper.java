package org.unclesniper.choreo;

public abstract class StaticOneToOnePropertyTypeMapper<SourceT, DestinationT>
		extends OneToOnePropertyTypeMapper<SourceT, DestinationT> {

	private final Class<SourceT> sourceType;

	private final Class<DestinationT> destinationType;

	public StaticOneToOnePropertyTypeMapper(Class<SourceT> sourceType, Class<DestinationT> destinationType) {
		this.sourceType = sourceType;
		this.destinationType = destinationType;
	}

	public Class<SourceT> getSourceType() {
		return sourceType;
	}

	public Class<DestinationT> getDestinationType() {
		return destinationType;
	}

}
