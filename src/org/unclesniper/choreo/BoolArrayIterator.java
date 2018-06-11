package org.unclesniper.choreo;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class BoolArrayIterator implements Iterator<Boolean> {

	private final boolean[] array;

	private int index;

	public BoolArrayIterator(boolean[] array) {
		this.array = array;
	}

	public boolean[] getArray() {
		return array;
	}

	public boolean hasNext() {
		return index < array.length;
	}

	public Boolean next() {
		if(index >= array.length)
			throw new NoSuchElementException();
		return array[index++];
	}

	public void remove() {
		throw new UnsupportedOperationException();
	}

}
