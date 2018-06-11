package org.unclesniper.choreo;

import java.util.Iterator;

public class ShortArrayIterable implements Iterable<Short> {

	private final short[] array;

	public ShortArrayIterable(short[] array) {
		this.array = array;
	}

	public short[] getArray() {
		return array;
	}

	public Iterator<Short> iterator() {
		return new ShortArrayIterator(array);
	}

}
