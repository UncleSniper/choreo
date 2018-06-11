package org.unclesniper.choreo;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class CharArrayIterator implements Iterator<Character> {

	private final char[] array;

	private int index;

	public CharArrayIterator(char[] array) {
		this.array = array;
	}

	public char[] getArray() {
		return array;
	}

	public boolean hasNext() {
		return index < array.length;
	}

	public Character next() {
		if(index >= array.length)
			throw new NoSuchElementException();
		return array[index++];
	}

	public void remove() {
		throw new UnsupportedOperationException();
	}

}
