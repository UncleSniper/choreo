package org.unclesniper.choreo;

import java.util.Map;
import java.util.HashMap;

public class TypeMap {

	private final Map<Class<?>, Object> bindings = new HashMap<Class<?>, Object>();

	public TypeMap() {}

	public <T> void put(Class<T> key, T value) {
		key.getName();
		bindings.put(key, value);
	}

	public <T> T get(Class<T> key) {
		Object value = bindings.get(key);
		return value == null ? null : key.cast(value);
	}

	public void remove(Class<?> key) {
		bindings.remove(key);
	}

}
