package org.unclesniper.choreo;

import java.util.Iterator;

public class ArrayIterable<E> implements Iterable<E> {

	private final E[] array;

	public ArrayIterable(E[] array) {
		this.array = array;
	}

	public E[] getArray() {
		return array;
	}

	public Iterator<E> iterator() {
		return new ArrayIterator<E>(array);
	}

}
