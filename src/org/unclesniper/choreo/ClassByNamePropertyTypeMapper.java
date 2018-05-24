package org.unclesniper.choreo;

public class ClassByNamePropertyTypeMapper extends StaticOneToOnePropertyTypeMapper<String, Class<?>> {

	public static final PropertyTypeMapper instance = new ClassByNamePropertyTypeMapper();

	private ClassLoader classLoader;

	public ClassByNamePropertyTypeMapper() {
		this(null);
	}

	@SuppressWarnings("unchecked")
	public ClassByNamePropertyTypeMapper(ClassLoader classLoader) {
		super(String.class, (Class)Class.class);
		this.classLoader = classLoader;
	}

	public ClassLoader getClassLoader() {
		return classLoader;
	}

	public void setClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	protected Class<?> map(BuildContext context, String name) {
		if(name == null)
			throw new IllegalArgumentException("Class name cannot be null");
		ClassLoader cl = classLoader == null ? context.getCurrentClassLoader() : classLoader;
		try {
			return cl.loadClass(name);
		}
		catch(ClassNotFoundException cnfe) {
			throw new IllegalArgumentException("No such class: " + name);
		}
	}

}
