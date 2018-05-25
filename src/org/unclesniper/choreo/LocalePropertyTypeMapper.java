package org.unclesniper.choreo;

import java.util.Locale;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class LocalePropertyTypeMapper extends StaticOneToOnePropertyTypeMapper<String, Locale> {

	public static final LocalePropertyTypeMapper instance = new LocalePropertyTypeMapper();

	private static final Pattern LOCALE_SPEC_RE = Pattern.compile("([a-z]+)(_([A-Z]+)(_([a-zA-Z]+))?)?");

	public LocalePropertyTypeMapper() {
		super(String.class, Locale.class);
	}

	protected Locale map(BuildContext context, String spec) {
		if(spec == null)
			throw new IllegalArgumentException("Locale specifier cannot be null");
		Matcher match = LocalePropertyTypeMapper.LOCALE_SPEC_RE.matcher(spec);
		if(!match.matches())
			throw new IllegalArgumentException("Malformed locale specifier: " + spec);
		String language = match.group(1), country = match.group(3), variant = match.group(5);
		if(country == null)
			return new Locale(language);
		if(variant == null)
			return new Locale(language, country);
		return new Locale(language, country, variant);
	}

}
