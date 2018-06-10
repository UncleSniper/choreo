package org.unclesniper.choreo;

import java.util.Iterator;

public class ByteArrayIterable implements Iterable<Byte> {

	private final byte[] array;

	public ByteArrayIterable(byte[] array) {
		this.array = array;
	}

	public byte[] getArray() {
		return array;
	}

	public Iterator<Byte> iterator() {
		return new ByteArrayIterator(array);
	}

}
