package org.unclesniper.choreo;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class ArrayIterator<E> implements Iterator<E> {

	private final E[] array;

	private int index;

	public ArrayIterator(E[] array) {
		this.array = array;
	}

	public E[] getArray() {
		return array;
	}

	public boolean hasNext() {
		return index < array.length;
	}

	public E next() {
		if(index >= array.length)
			throw new NoSuchElementException();
		return array[index++];
	}

	public void remove() {
		throw new UnsupportedOperationException();
	}

}
