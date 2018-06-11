package org.unclesniper.choreo;

import java.util.Iterator;

public class LongArrayIterable implements Iterable<Long> {

	private final long[] array;

	public LongArrayIterable(long[] array) {
		this.array = array;
	}

	public long[] getArray() {
		return array;
	}

	public Iterator<Long> iterator() {
		return new LongArrayIterator(array);
	}

}
