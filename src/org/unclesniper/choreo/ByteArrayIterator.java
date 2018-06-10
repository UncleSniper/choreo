package org.unclesniper.choreo;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class ByteArrayIterator implements Iterator<Byte> {

	private final byte[] array;

	private int index;

	public ByteArrayIterator(byte[] array) {
		this.array = array;
	}

	public byte[] getArray() {
		return array;
	}

	public boolean hasNext() {
		return index < array.length;
	}

	public Byte next() {
		if(index >= array.length)
			throw new NoSuchElementException();
		return array[index++];
	}

	public void remove() {
		throw new UnsupportedOperationException();
	}

}
