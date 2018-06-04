package org.unclesniper.choreo.parseopt;

import java.util.Map;
import java.util.HashMap;

public class OptionLogic {

	public enum Arity {
		NO_ARGUMENT,
		REQUIRED_ARGUMENT,
		OPTIONAL_ARGUMENT
	}

	enum InlineLongConflictPolicy {
		LONG_OVER_INLINE,
		INLINE_OVER_LONG,
		INLINE_LONG_CONFLICT_IS_ERROR
	}

	public static class Option {

		private Arity arity;

		private WordAction action;

		public Option(Arity arity, WordAction action) {
			this.arity = arity;
			this.action = action;
		}

		public Arity getArity() {
			return arity;
		}

		public void setArity(Arity arity) {
			this.arity = arity;
		}

		public WordAction getAction() {
			return action;
		}

		public void setAction(WordAction action) {
			this.action = action;
		}

	}

	public static final int FL_SHORT_OPTIONS           = 0001;
	public static final int FL_DOUBLE_LONG_OPTIONS     = 0002;
	public static final int FL_SINGLE_LONG_OPTIONS     = 0004;
	public static final int FL_WORD_SEPARATORS         = 0010;
	public static final int FL_SYMBOL_SEPARATORS       = 0020;
	public static final int FL_INLINE_ARGUMENTS        = 0040;
	public static final int FL_OPTION_TERMINATOR       = 0100;
	public static final int FL_BAREWORD_TERMINATES     = 0200;
	public static final int FL_UNRECOGNIZED_TERMINATES = 0400;

	public static final int DEFAULT_FLAGS
			= FL_SHORT_OPTIONS | FL_DOUBLE_LONG_OPTIONS
			| FL_WORD_SEPARATORS | FL_SYMBOL_SEPARATORS
			| FL_INLINE_ARGUMENTS | FL_OPTION_TERMINATOR;

	private int flags;

	private char initiatorSymbol = '-';

	private char separatorSymbol = '=';

	private String optionTerminator = "--";

	private InlineLongConflictPolicy inlineLongConflict = InlineLongConflictPolicy.INLINE_LONG_CONFLICT_IS_ERROR;

	private final Map<Character, Option> shortOptions = new HashMap<Character, Option>();

	private final Map<String, Option> longOptions = new HashMap<String, Option>();

	public OptionLogic() {
		this(OptionLogic.DEFAULT_FLAGS);
	}

	public OptionLogic(int flags) {
		this.flags = flags;
	}

	public int getFlags() {
		return flags;
	}

	public void setFlags(int flags) {
		this.flags = flags;
	}

	public void enableFlags(int mask) {
		flags |= mask;
	}

	public void disableFlags(int mask) {
		mask &= ~mask;
	}

	public boolean hasFlags(int mask) {
		return (flags & mask) == mask;
	}

	public char getInitiatorSymbol() {
		return initiatorSymbol;
	}

	public void setInitiatorSymbol(char initiatorSymbol) {
		this.initiatorSymbol = initiatorSymbol;
	}

	public char getSeparatorSymbol() {
		return separatorSymbol;
	}

	public void setSeparatorSymbol(char separatorSymbol) {
		this.separatorSymbol = separatorSymbol;
	}

	public String getOptionTerminator() {
		return optionTerminator;
	}

	public void setOptionTerminator(String optionTerminator) {
		this.optionTerminator = optionTerminator;
	}

	public InlineLongConflictPolicy getInlineLongConflictPolicy() {
		return inlineLongConflict;
	}

	public void setInlineLongConflictPolicy(InlineLongConflictPolicy inlineLongConflict) {
		this.inlineLongConflict = inlineLongConflict == null
				? InlineLongConflictPolicy.INLINE_LONG_CONFLICT_IS_ERROR : inlineLongConflict;
	}

	public OptionLogic addShortOption(char name, WordAction action, Arity arity) {
		if(arity == null)
			arity = Arity.NO_ARGUMENT;
		shortOptions.put(name, new Option(arity, action));
		return this;
	}

	public OptionLogic addShortOption(char name, WordAction action) {
		return addShortOption(name, action, null);
	}

	public OptionLogic addLongOption(String name, WordAction action, Arity arity) {
		name.length();
		if(arity == null)
			arity = Arity.NO_ARGUMENT;
		longOptions.put(name, new Option(arity, action));
		return this;
	}

	public OptionLogic addLongOption(String name, WordAction action) {
		return addLongOption(name, action, null);
	}

	public Option getShortOption(char name) {
		return shortOptions.get(name);
	}

	public Option getLongOption(String name) {
		return longOptions.get(name);
	}

}
