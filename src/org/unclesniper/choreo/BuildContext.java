package org.unclesniper.choreo;

import org.xml.sax.helpers.DefaultHandler;

public final class BuildContext {

	private final class BuildHandler extends DefaultHandler {

		public BuildHandler() {}

	}

	private static final ThreadLocal<BuildContext> THREAD_LOCAL_CONTEXT = new ThreadLocal<BuildContext>();

	private final BuildHandler saxHandler = new BuildHandler();

	private ClassLoader currentClassLoader;

	public BuildContext() {}

	public BuildContext(ClassLoader currentClassLoader) {
		this.currentClassLoader = currentClassLoader;
	}

	public ClassLoader getCurrentClassLoader() {
		return currentClassLoader;
	}

	public void setCurrentClassLoader(ClassLoader currentClassLoader) {
		this.currentClassLoader = currentClassLoader;
	}

	public static BuildContext getCurrentContext() {
		return BuildContext.THREAD_LOCAL_CONTEXT.get();
	}

	public static void setCurrentContext(BuildContext context) {
		BuildContext.THREAD_LOCAL_CONTEXT.set(context);
	}

}
