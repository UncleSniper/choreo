package org.unclesniper.choreo;

import java.util.Iterator;

public class IntArrayIterable implements Iterable<Integer> {

	private final int[] array;

	public IntArrayIterable(int[] array) {
		this.array = array;
	}

	public int[] getArray() {
		return array;
	}

	public Iterator<Integer> iterator() {
		return new IntArrayIterator(array);
	}

}
