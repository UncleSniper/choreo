package org.unclesniper.choreo;

import java.util.Iterator;

public class EmptyIterable<E> implements Iterable<E> {

	public EmptyIterable() {}

	public Iterator<E> iterator() {
		return new EmptyIterator<E>();
	}

}
