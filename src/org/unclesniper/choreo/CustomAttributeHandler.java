package org.unclesniper.choreo;

public interface CustomAttributeHandler {

	boolean handleAttribute(BuildContext context, Object object, ClassInfo classInfo, String name, String value)
			throws ChoreoException;

}
