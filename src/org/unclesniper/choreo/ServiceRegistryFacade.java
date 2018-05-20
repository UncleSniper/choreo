package org.unclesniper.choreo;

public interface ServiceRegistryFacade {

	Iterable<String> getServiceObjectKeys();

	Object getServiceObject(String key);

	void putServiceObject(String key, Object value);

}
