package org.unclesniper.choreo;

public interface VirtualElementClass {

	public interface ObjectState {

		Object getValue();

		void setValue(Object value);

		boolean isSkip();

		void setSkip(boolean skip);

	}

	void mapToActual(ObjectState state) throws ChoreoException;

}
