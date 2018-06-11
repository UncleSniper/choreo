package org.unclesniper.choreo;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class LongArrayIterator implements Iterator<Long> {

	private final long[] array;

	private int index;

	public LongArrayIterator(long[] array) {
		this.array = array;
	}

	public long[] getArray() {
		return array;
	}

	public boolean hasNext() {
		return index < array.length;
	}

	public Long next() {
		if(index >= array.length)
			throw new NoSuchElementException();
		return array[index++];
	}

	public void remove() {
		throw new UnsupportedOperationException();
	}

}
