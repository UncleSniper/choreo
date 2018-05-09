package org.unclesniper.choreo;

import java.lang.reflect.Method;
import java.lang.reflect.Constructor;
import org.unclesniper.choreo.annotation.Adder;
import org.unclesniper.choreo.annotation.Setter;

public final class ClassInfo {

	private enum AccessorType {
		SETTER,
		ADDER
	}

	private final Class<?> subject;

	private Constructor<?> leafConstructor;

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

	private void scanClass(String module) throws InvalidElementClassException {
		try {
			leafConstructor = subject.getConstructor();
		}
		catch(NoSuchMethodException nsme) {
			throw new InvalidElementClassException(module, subject.getName(), "Element class '"
					+ subject.getName() + "' in module '" + module + "' does not expose a nilary constructor",
					nsme);
		}
		for(Method method : subject.getMethods())
			scanMethod(method, module);
	}

	private void scanMethod(Method method, String module) throws InvalidElementClassMethodException {
		Setter setterAnn = method.getAnnotation(Setter.class);
		if(setterAnn != null)
			sinkSetter(module, method, setterAnn);
		Adder adderAnn = method.getAnnotation(Adder.class);
		if(adderAnn != null)
			sinkAdder(module, method, adderAnn);
		if(setterAnn == null && adderAnn == null)
			sinkMethod(method);
	}

	private void sinkSetter(String module, Method method, Setter annotation)
			throws InvalidElementClassMethodException {
		Class<?>[] parameters = method.getParameterTypes();
		if(parameters.length != 1)
			throw new InvalidElementClassMethodException(module, subject.getName(), method.toString(),
					"Method cannot be a @Setter, as it has " + parameters.length + " parameters");
		//TODO
	}

	private void sinkAdder(String module, Method method, Adder annotation)
			throws InvalidElementClassMethodException {
		Class<?>[] parameters = method.getParameterTypes();
		if(parameters.length != 1)
			throw new InvalidElementClassMethodException(module, subject.getName(), method.toString(),
					"Method cannot be an @Adder, as it has " + parameters.length + " parameters");
		//TODO
	}

	private void sinkMethod(Method method) {
		//TODO
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
