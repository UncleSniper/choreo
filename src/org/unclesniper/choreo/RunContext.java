package org.unclesniper.choreo;

public final class RunContext {

	private ServiceRegistryFacade serviceRegistry;

	private ClassLoader classLoader;

	public RunContext() {}

	public RunContext(ServiceRegistryFacade serviceRegistry) {
		this(serviceRegistry, null);
	}

	public RunContext(ClassLoader classLoader) {
		this(null, classLoader);
	}

	public RunContext(ServiceRegistryFacade serviceRegistry, ClassLoader classLoader) {
		this.serviceRegistry = serviceRegistry;
		this.classLoader = classLoader;
	}

	public ServiceRegistryFacade getServiceRegistry() {
		return serviceRegistry;
	}

	public void setServiceRegistry(ServiceRegistryFacade serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}

	public ClassLoader getClassLoader() {
		return classLoader;
	}

	public void setClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

}
