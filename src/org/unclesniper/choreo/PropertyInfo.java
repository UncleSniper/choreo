package org.unclesniper.choreo;

import java.util.Map;
import java.util.HashMap;

public final class PropertyInfo {

	private final String name;

	private final Map<Class<?>, AccessorInfo> accessors = new HashMap<Class<?>, AccessorInfo>();

	public PropertyInfo(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void addAccessor(AccessorInfo info) {
		if(info == null)
			return;
		Class<?> newType = info.getType();
		if(!accessors.containsKey(newType))
			accessors.put(newType, info);
	}

	public Iterable<Class<?>> getAccessorTypes() {
		return accessors.keySet();
	}

	public Iterable<AccessorInfo> getAccessors() {
		return accessors.values();
	}

	public AccessorInfo getAccessor(Class<?> type) {
		return accessors.get(type);
	}

}
