package org.unclesniper.choreo;

public class ChoreoRootTypeException extends ChoreoException {

	private final Class<?> requiredType;

	private final Class<?> foundType;

	public ChoreoRootTypeException(Class<?> requiredType, Class<?> foundType) {
		super("Object graph is expected to yield a root object of type '" + requiredType.getName()
				+ "', but yielded one of type '" + foundType.getName() + '\'');
		this.requiredType = requiredType;
		this.foundType = foundType;
	}

	public Class<?> getRequiredType() {
		return requiredType;
	}

	public Class<?> getFoundType() {
		return foundType;
	}

}
