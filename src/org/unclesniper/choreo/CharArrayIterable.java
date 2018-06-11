package org.unclesniper.choreo;

import java.util.Iterator;

public class CharArrayIterable implements Iterable<Character> {

	private final char[] array;

	public CharArrayIterable(char[] array) {
		this.array = array;
	}

	public char[] getArray() {
		return array;
	}

	public Iterator<Character> iterator() {
		return new CharArrayIterator(array);
	}

}
