package org.unclesniper.choreo;

public interface ModuleInitializer {

	void initializeModuleBeforeBind(BuildContext context, Module module) throws IllegalModuleException;

	void initializeModuleAfterBind(BuildContext context, Module module) throws IllegalModuleException;

}
