package org.unclesniper.choreo;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class DoubleArrayIterator implements Iterator<Double> {

	private final double[] array;

	private int index;

	public DoubleArrayIterator(double[] array) {
		this.array = array;
	}

	public double[] getArray() {
		return array;
	}

	public boolean hasNext() {
		return index < array.length;
	}

	public Double next() {
		if(index >= array.length)
			throw new NoSuchElementException();
		return array[index++];
	}

	public void remove() {
		throw new UnsupportedOperationException();
	}

}
