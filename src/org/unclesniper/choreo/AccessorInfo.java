package org.unclesniper.choreo;

import java.lang.reflect.Method;

public final class AccessorInfo {

	private final Method method;

	private final Class<?> type;

	public AccessorInfo(Method method) {
		this.method = method;
		type = method.getParameterTypes()[0];
	}

	public Method getMethod() {
		return method;
	}

	public Class<?> getType() {
		return type;
	}

}
