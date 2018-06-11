package org.unclesniper.choreo;

import java.util.Iterator;

public class BoolArrayIterable implements Iterable<Boolean> {

	private final boolean[] array;

	public BoolArrayIterable(boolean[] array) {
		this.array = array;
	}

	public boolean[] getArray() {
		return array;
	}

	public Iterator<Boolean> iterator() {
		return new BoolArrayIterator(array);
	}

}
