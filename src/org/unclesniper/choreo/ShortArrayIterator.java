package org.unclesniper.choreo;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class ShortArrayIterator implements Iterator<Short> {

	private final short[] array;

	private int index;

	public ShortArrayIterator(short[] array) {
		this.array = array;
	}

	public short[] getArray() {
		return array;
	}

	public boolean hasNext() {
		return index < array.length;
	}

	public Short next() {
		if(index >= array.length)
			throw new NoSuchElementException();
		return array[index++];
	}

	public void remove() {
		throw new UnsupportedOperationException();
	}

}
