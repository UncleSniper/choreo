package org.unclesniper.choreo;

import java.lang.reflect.Method;
import org.unclesniper.choreo.annotation.Whitespace;
import org.unclesniper.choreo.annotation.EmptyStringPolicy;

public final class AccessorInfo {

	public static final class WhitespaceResult {

		private final String value;

		private final boolean set;

		public WhitespaceResult(String value, boolean set) {
			this.value = value;
			this.set = set;
		}

		public String getValue() {
			return value;
		}

		public boolean isSet() {
			return set;
		}

	}

	private final Method method;

	private final Class<?> type;

	private boolean trimWhitespace;

	private EmptyStringPolicy emptyStringPolicy = EmptyStringPolicy.KEEP;

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

	public boolean isTrimWhitespace() {
		return trimWhitespace;
	}

	public void setTrimWhitespace(boolean trimWhitespace) {
		this.trimWhitespace = trimWhitespace;
	}

	public EmptyStringPolicy getEmptyStringPolicy() {
		return emptyStringPolicy;
	}

	public void setEmptyStringPolicy(EmptyStringPolicy emptyStringPolicy) {
		this.emptyStringPolicy = emptyStringPolicy == null ? EmptyStringPolicy.KEEP : emptyStringPolicy;
	}

	public void setFrom(Whitespace whitespace) {
		if(whitespace == null)
			return;
		trimWhitespace = whitespace.trim();
		emptyStringPolicy = whitespace.empty();
	}

	public WhitespaceResult applyWhitespacePolicy(String value) {
		if(trimWhitespace)
			value = value.trim();
		if(value.length() > 0)
			return new WhitespaceResult(value, true);
		switch(emptyStringPolicy) {
			case KEEP:
				return new WhitespaceResult(value, true);
			case SKIP:
				return new WhitespaceResult(null, false);
			case NULL:
				return new WhitespaceResult(null, true);
			default:
				throw new Doom("Unrecognized empty string policy: " + emptyStringPolicy.name());
		}
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("AccessorInfo { method = ");
		sb.append(method.toString());
		sb.append(", type = ");
		sb.append(type.getName());
		sb.append(", trimWhitespace = ");
		sb.append(String.valueOf(trimWhitespace));
		sb.append(", emptyStringPolicy = ");
		sb.append(emptyStringPolicy.name());
		sb.append(" }");
		return sb.toString();
	}

}
