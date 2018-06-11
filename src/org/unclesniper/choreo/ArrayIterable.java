package org.unclesniper.choreo;

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

public class ArrayIterable<E> implements Iterable<E> {

	public enum ElementType {
		BYTE,
		SHORT,
		INT,
		LONG,
		FLOAT,
		DOUBLE,
		CHAR,
		BOOLEAN,
		OBJECT
	}

	private static final Map<Class<?>, ElementType> ARRAY_TYPE_MAP;

	static {
		ARRAY_TYPE_MAP = new HashMap<Class<?>, ElementType>();
		ARRAY_TYPE_MAP.put(byte[].class, ElementType.BYTE);
		ARRAY_TYPE_MAP.put(short[].class, ElementType.SHORT);
		ARRAY_TYPE_MAP.put(int[].class, ElementType.INT);
		ARRAY_TYPE_MAP.put(long[].class, ElementType.LONG);
		ARRAY_TYPE_MAP.put(float[].class, ElementType.FLOAT);
		ARRAY_TYPE_MAP.put(double[].class, ElementType.DOUBLE);
		ARRAY_TYPE_MAP.put(char[].class, ElementType.CHAR);
		ARRAY_TYPE_MAP.put(boolean[].class, ElementType.BOOLEAN);
	}

	private final E[] array;

	public ArrayIterable(E[] array) {
		this.array = array;
	}

	public E[] getArray() {
		return array;
	}

	public Iterator<E> iterator() {
		return new ArrayIterator<E>(array);
	}

	@SuppressWarnings("unchecked")
	public static Iterable<?> arrayIterableFromObject(Object object) {
		if(object == null)
			return null;
		Class<?> clazz = object.getClass();
		ElementType etype = ArrayIterable.ARRAY_TYPE_MAP.get(clazz);
		if(etype != null) {
			switch(etype) {
				case BYTE:
					return new ByteArrayIterable((byte[])object);
				case SHORT:
					return new ShortArrayIterable((short[])object);
				case INT:
					return new IntArrayIterable((int[])object);
				case LONG:
					return new LongArrayIterable((long[])object);
				case FLOAT:
					return new FloatArrayIterable((float[])object);
				case DOUBLE:
					return new DoubleArrayIterable((double[])object);
				case CHAR:
					return new CharArrayIterable((char[])object);
				case BOOLEAN:
					return new BoolArrayIterable((boolean[])object);
				default:
					throw new Doom("Unrecognized ElementType: " + etype.name());
			}
		}
		if(!clazz.isArray())
			return null;
		return new ArrayIterable((Object[])object);
	}

}
