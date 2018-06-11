package org.unclesniper.choreo;

import java.util.Iterator;

public class FloatArrayIterable implements Iterable<Float> {

	private final float[] array;

	public FloatArrayIterable(float[] array) {
		this.array = array;
	}

	public float[] getArray() {
		return array;
	}

	public Iterator<Float> iterator() {
		return new FloatArrayIterator(array);
	}

}
