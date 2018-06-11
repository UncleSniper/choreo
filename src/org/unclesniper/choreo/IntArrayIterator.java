package org.unclesniper.choreo;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class IntArrayIterator implements Iterator<Integer> {

	private final int[] array;

	private int index;

	public IntArrayIterator(int[] array) {
		this.array = array;
	}

	public int[] getArray() {
		return array;
	}

	public boolean hasNext() {
		return index < array.length;
	}

	public Integer next() {
		if(index >= array.length)
			throw new NoSuchElementException();
		return array[index++];
	}

	public void remove() {
		throw new UnsupportedOperationException();
	}

}
