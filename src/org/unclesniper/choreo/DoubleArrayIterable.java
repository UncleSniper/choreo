package org.unclesniper.choreo;

import java.util.Iterator;

public class DoubleArrayIterable implements Iterable<Double> {

	private final double[] array;

	public DoubleArrayIterable(double[] array) {
		this.array = array;
	}

	public double[] getArray() {
		return array;
	}

	public Iterator<Double> iterator() {
		return new DoubleArrayIterator(array);
	}

}
