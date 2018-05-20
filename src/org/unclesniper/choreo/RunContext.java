package org.unclesniper.choreo;

public final class RunContext {

	private ServiceRegistryFacade serviceRegistry;

	public RunContext() {}

	public RunContext(ServiceRegistryFacade serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}

	public ServiceRegistryFacade getServiceRegistry() {
		return serviceRegistry;
	}

	public void setServiceRegistry(ServiceRegistryFacade serviceRegistry) {
		this.serviceRegistry = serviceRegistry;
	}

}
