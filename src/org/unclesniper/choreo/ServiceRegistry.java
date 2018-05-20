package org.unclesniper.choreo;

import java.util.Map;
import java.util.HashMap;

public class ServiceRegistry implements ServiceRegistryFacade {

	private final Map<String, Object> serviceObjects = new HashMap<String, Object>();

	public ServiceRegistry() {}

	public Iterable<String> getServiceObjectKeys() {
		return serviceObjects.keySet();
	}

	public Object getServiceObject(String key) {
		return serviceObjects.get(key);
	}

	public void putServiceObject(String key, Object value) {
		if(key == null)
			throw new IllegalArgumentException("Service object key cannot be null");
		if(value == null)
			serviceObjects.remove(key);
		else
			serviceObjects.put(key, value);
	}

}
