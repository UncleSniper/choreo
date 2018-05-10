package org.unclesniper.choreo;

import java.util.Map;
import java.util.HashMap;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Constructor;
import org.unclesniper.choreo.annotation.Adder;
import org.unclesniper.choreo.annotation.Setter;
import org.unclesniper.choreo.annotation.PropertyName;
import org.unclesniper.choreo.annotation.DefaultAdder;

public final class ClassInfo {

	private enum AccessorType {
		SETTER,
		ADDER
	}

	private final Class<?> subject;

	private Constructor<?> leafConstructor;

	private final Map<String, PropertyInfo> setters = new HashMap<String, PropertyInfo>();

	private final Map<String, PropertyInfo> adders = new HashMap<String, PropertyInfo>();

	private PropertyInfo defaultAdder;

	public ClassInfo(Class<?> subject, String module) throws InvalidElementClassException {
		this.subject = subject;
		scanClass(module);
	}

	public Class<?> getSubject() {
		return subject;
	}

	public Constructor<?> getLeafConstructor() {
		return leafConstructor;
	}

	public PropertyInfo getDefaultAdder() {
		return defaultAdder;
	}

	private void scanClass(String module) throws InvalidElementClassException {
		try {
			leafConstructor = subject.getConstructor();
		}
		catch(NoSuchMethodException nsme) {
			throw new InvalidElementClassException(module, subject.getName(), "Element class '"
					+ subject.getName() + "' in module '" + module + "' does not expose a nilary constructor",
					nsme);
		}
		for(Method method : subject.getMethods()) {
			if((method.getModifiers() & Modifier.STATIC) == 0)
				scanMethod(method, module);
		}
	}

	private void scanMethod(Method method, String module) throws InvalidElementClassMethodException {
		PropertyName nameAnn = method.getAnnotation(PropertyName.class);
		String propname;
		if(nameAnn == null)
			propname = null;
		else {
			propname = nameAnn.value();
			if(propname != null && propname.length() == 0)
				propname = null;
		}
		if(propname == null)
			propname = ClassInfo.guessPropertyName(method.getName());
		Setter setterAnn = method.getAnnotation(Setter.class);
		if(setterAnn != null)
			sinkSetter(module, method, propname, setterAnn);
		Adder adderAnn = method.getAnnotation(Adder.class);
		if(adderAnn != null)
			sinkAdder(module, method, propname, adderAnn);
		DefaultAdder defAdderAnn = method.getAnnotation(DefaultAdder.class);
		if(defAdderAnn != null)
			sinkDefaultAdder(module, method);
		if(setterAnn == null && adderAnn == null && defAdderAnn == null)
			sinkMethod(module, method, propname, nameAnn != null);
	}

	private void sinkSetter(String module, Method method, String propname, Setter annotation)
			throws InvalidElementClassMethodException {
		Class<?>[] parameters = method.getParameterTypes();
		if(parameters.length != 1)
			throw new InvalidElementClassMethodException(module, subject.getName(), method.toString(),
					"Method cannot be a @Setter, as it has " + parameters.length + " parameters");
		String altname = annotation.value();
		if(altname != null && (altname.length() == 0 || altname.equals(propname)))
			altname = null;
		AccessorInfo accInfo = new AccessorInfo(method);
		sinkAccessor(accInfo, propname, setters);
		if(altname != null)
			sinkAccessor(accInfo, altname, setters);
	}

	private void sinkAdder(String module, Method method, String propname, Adder annotation)
			throws InvalidElementClassMethodException {
		Class<?>[] parameters = method.getParameterTypes();
		if(parameters.length != 1)
			throw new InvalidElementClassMethodException(module, subject.getName(), method.toString(),
					"Method cannot be an @Adder, as it has " + parameters.length + " parameters");
		String altname = annotation.value();
		if(altname != null && (altname.length() == 0 || altname.equals(propname)))
			altname = null;
		AccessorInfo accInfo = new AccessorInfo(method);
		sinkAccessor(accInfo, propname, adders);
		if(altname != null)
			sinkAccessor(accInfo, altname, adders);
	}

	private void sinkMethod(String module, Method method, String propname, boolean hasName)
			throws InvalidElementClassMethodException {
		Class<?>[] parameters = method.getParameterTypes();
		if(parameters.length != 1) {
			if(hasName)
				throw new InvalidElementClassMethodException(module, subject.getName(), method.toString(),
						"Method cannot be an accessor, as it has " + parameters.length + " parameters");
			return;
		}
		AccessorInfo accInfo = new AccessorInfo(method);
		sinkAccessor(accInfo, propname, setters);
		sinkAccessor(accInfo, propname, adders);
	}

	private void sinkDefaultAdder(String module, Method method) throws InvalidElementClassMethodException {
		Class<?>[] parameters = method.getParameterTypes();
		if(parameters.length != 1)
			throw new InvalidElementClassMethodException(module, subject.getName(), method.toString(),
					"Method cannot be a @DefaultAdder, as it has " + parameters.length + " parameters");
		AccessorInfo accessor = new AccessorInfo(method);
		if(defaultAdder == null)
			defaultAdder = new PropertyInfo(null);
		defaultAdder.addAccessor(accessor);
	}

	private void sinkAccessor(AccessorInfo accessor, String propname, Map<String, PropertyInfo> map) {
		PropertyInfo property = map.get(propname);
		if(property == null) {
			property = new PropertyInfo(propname);
			map.put(propname, property);
		}
		property.addAccessor(accessor);
	}

	private static String guessPropertyName(String methodName) {
		if(
			methodName.length() < 3
			|| !(
				methodName.startsWith("set")
				|| methodName.startsWith("add")
			)
		)
			return methodName;
		char c = methodName.charAt(3);
		if(Character.isLetter(c) && Character.isUpperCase(c))
			return Character.toLowerCase(c) + methodName.substring(4);
		return methodName;
	}

	private static AccessorType isAccessorName(String methodName) {
		if(methodName.length() < 3)
			return null;
		AccessorType type;
		if(methodName.startsWith("set"))
			type = AccessorType.SETTER;
		else if(methodName.startsWith("add"))
			type = AccessorType.ADDER;
		else
			return null;
		char c = methodName.charAt(3);
		if(Character.isLetter(c) && Character.isUpperCase(c))
			return type;
		return null;
	}

}
