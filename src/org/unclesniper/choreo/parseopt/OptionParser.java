package org.unclesniper.choreo.parseopt;

import java.util.List;
import java.util.LinkedList;
import org.unclesniper.choreo.Doom;

public class OptionParser implements WordAction {

	private enum PendingInitiation {
		SHORT,
		LONG_SHORT,
		LONG
	}

	private OptionLogic logic;

	private WordAction nonOptionAction;

	private final List<String> nonOptionWords = new LinkedList<String>();

	private char pendingShortName;

	private String pendingLongName;

	private OptionLogic.Option pendingOption;

	private OptionType pendingType;

	private PendingInitiation pendingInitiation;

	private boolean terminated;

	public OptionParser(OptionLogic logic) {
		this(logic, null);
	}

	public OptionParser(OptionLogic logic, WordAction nonOptionAction) {
		this.logic = logic;
		this.nonOptionAction = nonOptionAction;
	}

	public OptionLogic getLogic() {
		return logic;
	}

	public void setLogic(OptionLogic logic) {
		this.logic = logic;
	}

	public WordAction getNonOptionAction() {
		return nonOptionAction;
	}

	public void setNonOptionAction(WordAction nonOptionAction) {
		this.nonOptionAction = nonOptionAction;
	}

	public Iterable<String> getNonOptionWords() {
		return nonOptionWords;
	}

	public void parseWord(String word) throws CommandLineException {
		if(pendingOption == null) {
			parseInitialWord(word);
			return;
		}
		WordAction action = pendingOption.getAction();
		if(pendingOption.getArity() == OptionLogic.Arity.OPTIONAL_ARGUMENT
				&& !word.isEmpty() && word.charAt(0) == logic.getInitiatorSymbol()) {
			pendingOption = null;
			action.wordEncountered(getPendingName(), null);
			parseInitialWord(word);
		}
		else {
			pendingOption = null;
			action.wordEncountered(getPendingName(), word);
		}
	}

	private String getPendingName() {
		return pendingType == OptionType.LONG ? pendingLongName : String.valueOf(pendingShortName);
	}

	private void parseInitialWord(String word) throws CommandLineException {
		// is it '--'?
		if(!terminated && logic.hasFlags(OptionLogic.FL_OPTION_TERMINATOR)
				&& word.equals(logic.getOptionTerminator())) {
			terminated = true;
			return;
		}
		final int length = word.length();
		// is it a bareword?
		if(terminated || length < 2 || word.charAt(0) != logic.getInitiatorSymbol()) {
			if(logic.hasFlags(OptionLogic.FL_BAREWORD_TERMINATES))
				terminated = true;
			(nonOptionAction == null ? this : nonOptionAction).wordEncountered(null, word);
			return;
		}
		// so it's either '--foo'...
		if(logic.hasFlags(OptionLogic.FL_DOUBLE_LONG_OPTIONS) && length > 2
				&& word.charAt(1) == logic.getInitiatorSymbol())
			parseDoubleOption(word);
		// ...or '-foo'
		else
			parseSingleOption(word);
	}

	private void parseSingleOption(String word) throws CommandLineException {
		// is it '-o'?
		if(word.length() == 2) {
			char shortName = word.charAt(1);
			OptionLogic.Option option = logic.getShortOption(shortName);
			boolean optionIsLong;
			String longName = String.valueOf(shortName);
			if(option == null) {
				optionIsLong = true;
				if(logic.hasFlags(OptionLogic.FL_SINGLE_LONG_OPTIONS))
					option = logic.getLongOption(longName);
				if(option == null)
					throw new UnrecognizedOptionException(longName, OptionType.SHORT, word);
			}
			else
				optionIsLong = false;
			if(option.getArity() == OptionLogic.Arity.NO_ARGUMENT)
				option.getAction().wordEncountered(longName, null);
			else if(logic.hasFlags(OptionLogic.FL_WORD_SEPARATORS)) {
				pendingShortName = shortName;
				pendingOption = option;
				pendingType = OptionType.SHORT;
				pendingInitiation = optionIsLong ? PendingInitiation.LONG_SHORT : PendingInitiation.SHORT;
			}
			else if(option.getArity() == OptionLogic.Arity.OPTIONAL_ARGUMENT)
				option.getAction().wordEncountered(longName, null);
			else
				throw new MissingOptionArgumentException(longName,
						optionIsLong ? OptionType.LONG : OptionType.SHORT, word);
		}
		// so it's '-owhatever'
		// could it be long?
		String longName, longArg;
		OptionLogic.Option longOption;
		int sepPosition = word.indexOf(logic.getSeparatorSymbol(), 2);
		if(!logic.hasFlags(OptionLogic.FL_SINGLE_LONG_OPTIONS)) {
			longName = null;
			longOption = null;
			longArg = null;
		}
		else if(sepPosition > 0 && logic.hasFlags(OptionLogic.FL_SYMBOL_SEPARATORS)) {
			// is it '-foo=bar'?
			longName = word.substring(1, sepPosition);
			longOption = logic.getLongOption(longName);
			longArg = word.substring(sepPosition + 1);
		}
		else {
			// so it's '-foo'
			longName = word.substring(1);
			longOption = logic.getLongOption(longName);
			longArg = null;
		}
		// could it be short?
		OptionLogic.Option shortOption;
		boolean restIsArg = false, restIsOptions = false;
		if(!logic.hasFlags(OptionLogic.FL_SHORT_OPTIONS))
			shortOption = null;
		else {
			shortOption = logic.getShortOption(word.charAt(1));
			if(shortOption != null) {
				switch(shortOption.getArity()) {
					case OPTIONAL_ARGUMENT:
						restIsArg = true;
						restIsOptions = areRemainingShortOptionsValid(word);
						break;
					case REQUIRED_ARGUMENT:
						restIsArg = true;
						restIsOptions = false;
						break;
					case NO_ARGUMENT:
						restIsArg = false;
						restIsOptions = areRemainingShortOptionsValid(word);
						break;
					default:
						throw new Doom("Unrecognized option arity: " + shortOption.getArity().name());
				}
			}
		}
		// which is it, long or short?
		if(shortOption != null) {
			if(longOption == null) {
				flushShortOption(word, shortOption, restIsArg, restIsOptions);
				return;
			}
			switch(logic.getInlineLongConflictPolicy()) {
				case LONG_OVER_INLINE:
					break;
				case INLINE_OVER_LONG:
					flushShortOption(word, shortOption, restIsArg, restIsOptions);
					return;
				case INLINE_LONG_CONFLICT_IS_ERROR:
					{
						char initiatorSymbol = logic.getInitiatorSymbol();
						String rendA = word.substring(0, 2);
						String rendB = initiatorSymbol + (initiatorSymbol + longName);
						throw new AmbiguousOptionException(longName, OptionType.LONG, rendA, rendA, rendB);
					}
				default:
					throw new Doom("Unrecognized InlineLongConflictPolicy: "
							+ logic.getInlineLongConflictPolicy().name());
			}
			// long chosen
		}
		else {
			if(longOption == null)
				throw new UnrecognizedOptionException(word.substring(1), OptionType.SHORT, word);
			// definitely long
		}
		// so it's long
		if(longArg != null) {
			if(longOption.getArity() == OptionLogic.Arity.NO_ARGUMENT)
				throw new ExcessOptionArgumentException(longName, OptionType.LONG, word.substring(0, sepPosition));
			longOption.getAction().wordEncountered(longName, longArg);
		}
		else if(longOption.getArity() == OptionLogic.Arity.NO_ARGUMENT)
			longOption.getAction().wordEncountered(longName, null);
		else {
			pendingLongName = longName;
			pendingOption = longOption;
			pendingType = OptionType.LONG;
			pendingInitiation = PendingInitiation.LONG_SHORT;
		}
	}

	private void flushShortOption(String word, OptionLogic.Option option,
			boolean restIsArg, boolean restIsOptions) throws CommandLineException {
		if(!logic.hasFlags(OptionLogic.FL_INLINE_ARGUMENTS))
			restIsArg = false;
		// Perhaps some day we'll allow setting precedence between
		// treating the rest as an argument or further options,
		// but not right now.
		if(restIsArg)
			option.getAction().wordEncountered(word.substring(1, 2), word.substring(2));
		else {
			option.getAction().wordEncountered(word.substring(1, 2), null);
			parseRemainingShortOptions(word);
		}
	}

	private boolean areRemainingShortOptionsValid(String word) {
		final int length = word.length();
		for(int pos = 2; pos < length; ++pos) {
			OptionLogic.Option option = logic.getShortOption(word.charAt(pos));
			if(option == null)
				return false;
			boolean hasMore = pos < length - 1;
			switch(option.getArity()) {
				case REQUIRED_ARGUMENT:
					if(!hasMore && !logic.hasFlags(OptionLogic.FL_WORD_SEPARATORS))
						return false;
				case OPTIONAL_ARGUMENT:
					if(!hasMore)
						break;
					if(logic.hasFlags(OptionLogic.FL_INLINE_ARGUMENTS))
						return true;
					if(option.getArity() == OptionLogic.Arity.REQUIRED_ARGUMENT)
						return false;
					break;
				case NO_ARGUMENT:
					break;
				default:
					throw new Doom("Unrecognized option arity: " + option.getArity().name());
			}
		}
		return true;
	}

	private void parseRemainingShortOptions(String word) throws CommandLineException {
		final int length = word.length();
		for(int pos = 2; pos < length; ++pos) {
			char shortName = word.charAt(pos);
			OptionLogic.Option option = logic.getShortOption(shortName);
			String longName = String.valueOf(shortName);
			if(option == null)
				throw new UnrecognizedOptionException(longName, OptionType.SHORT,
						logic.getInitiatorSymbol() + longName);
			boolean hasMore = pos < length - 1;
			switch(option.getArity()) {
				case REQUIRED_ARGUMENT:
				case OPTIONAL_ARGUMENT:
					if(!hasMore) {
						if(logic.hasFlags(OptionLogic.FL_WORD_SEPARATORS)) {
							pendingShortName = shortName;
							pendingOption = option;
							pendingType = OptionType.SHORT;
							pendingInitiation = PendingInitiation.SHORT;
						}
						else if(option.getArity() == OptionLogic.Arity.OPTIONAL_ARGUMENT)
							option.getAction().wordEncountered(longName, null);
						else
							throw new MissingOptionArgumentException(longName, OptionType.SHORT,
									logic.getInitiatorSymbol() + longName);
						break;
					}
					if(logic.hasFlags(OptionLogic.FL_INLINE_ARGUMENTS)) {
						option.getAction().wordEncountered(longName, word.substring(pos + 1));
						return;
					}
					if(option.getArity() == OptionLogic.Arity.OPTIONAL_ARGUMENT)
						option.getAction().wordEncountered(longName, null);
					else
						throw new MissingOptionArgumentException(longName, OptionType.SHORT,
								logic.getInitiatorSymbol() + longName);
					break;
				case NO_ARGUMENT:
					option.getAction().wordEncountered(longName, null);
					break;
				default:
					throw new Doom("Unrecognized option arity: " + option.getArity().name());
			}
		}
	}

	private void parseDoubleOption(String word) throws CommandLineException {
		String longName, longArg;
		int sepPosition = word.indexOf(logic.getSeparatorSymbol(), 3);
		if(sepPosition > 0 && logic.hasFlags(OptionLogic.FL_SYMBOL_SEPARATORS)) {
			// is it '--foo=bar'?
			longName = word.substring(2, sepPosition);
			longArg = word.substring(sepPosition + 1);
		}
		else {
			longName = word.substring(2);
			longArg = null;
		}
		OptionLogic.Option option = logic.getLongOption(longName);
		if(option == null)
			throw new UnrecognizedOptionException(longName, OptionType.LONG,
					longArg == null ? word : word.substring(0, sepPosition));
		if(longArg != null) {
			if(option.getArity() == OptionLogic.Arity.NO_ARGUMENT)
				throw new ExcessOptionArgumentException(longName, OptionType.LONG, word.substring(0, sepPosition));
			option.getAction().wordEncountered(longName, longArg);
		}
		else if(option.getArity() == OptionLogic.Arity.NO_ARGUMENT)
			option.getAction().wordEncountered(longName, null);
		else {
			pendingLongName = longName;
			pendingOption = option;
			pendingType = OptionType.LONG;
			pendingInitiation = PendingInitiation.LONG;
		}
	}

	public void parseWords(Iterable<String> words) throws CommandLineException {
		for(String word : words)
			parseWord(word);
	}

	public void parseWords(String[] words, int offset, int count) throws CommandLineException {
		for(; count > 0; --count, ++offset)
			parseWord(words[offset]);
	}

	public void parseWords(String[] words) throws CommandLineException {
		for(String word : words)
			parseWord(word);
	}

	public void endCommandLine() throws CommandLineException {
		if(pendingOption == null)
			return;
		if(pendingOption.getArity() == OptionLogic.Arity.REQUIRED_ARGUMENT) {
			switch(pendingInitiation) {
				case SHORT:
					{
						String name = String.valueOf(pendingShortName);
						throw new MissingOptionArgumentException(name, OptionType.SHORT,
								logic.getInitiatorSymbol() + name);
					}
				case LONG_SHORT:
					throw new MissingOptionArgumentException(pendingLongName, OptionType.LONG,
							logic.getInitiatorSymbol() + pendingLongName);
				case LONG:
					{
						char initiatorSymbol = logic.getInitiatorSymbol();
						throw new MissingOptionArgumentException(pendingLongName, OptionType.LONG,
								initiatorSymbol + (initiatorSymbol + pendingLongName));
					}
				default:
					throw new Doom("Unrecognized PendingInitiation: " + pendingInitiation.name());
			}
		}
		WordAction action = pendingOption.getAction();
		pendingOption = null;
		String pendingName;
		switch(pendingType) {
			case SHORT:
				pendingName = String.valueOf(pendingShortName);
				break;
			case LONG:
				pendingName = pendingLongName;
				break;
			default:
				throw new Doom("Unrecognized pending OptionType: " + pendingType.name());
		}
		action.wordEncountered(pendingName, null);
	}

	public void reset() {
		pendingOption = null;
		terminated = false;
	}

	public void wordEncountered(String key, String value) {
		nonOptionWords.add(value);
	}

}
