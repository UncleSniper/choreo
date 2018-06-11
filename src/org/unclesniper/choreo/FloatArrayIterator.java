package org.unclesniper.choreo;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class FloatArrayIterator implements Iterator<Float> {

	private final float[] array;

	private int index;

	public FloatArrayIterator(float[] array) {
		this.array = array;
	}

	public float[] getArray() {
		return array;
	}

	public boolean hasNext() {
		return index < array.length;
	}

	public Float next() {
		if(index >= array.length)
			throw new NoSuchElementException();
		return array[index++];
	}

	public void remove() {
		throw new UnsupportedOperationException();
	}

}
