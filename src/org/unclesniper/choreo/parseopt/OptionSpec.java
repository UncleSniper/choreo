package org.unclesniper.choreo.parseopt;

public class OptionSpec {

	private OptionLogic logic;

	private OptionPrinter printer;

	public OptionSpec(OptionLogic logic, OptionPrinter printer) {
		this.logic = logic;
		this.printer = printer;
	}

	public OptionLogic getLogic() {
		return logic;
	}

	public void setLogic(OptionLogic logic) {
		this.logic = logic;
	}

	public OptionPrinter getPrinter() {
		return printer;
	}

	public void setPrinter(OptionPrinter printer) {
		this.printer = printer;
	}

	public OptionSpec option(char name, String usage, WordAction action, OptionLogic.Arity arity, String help) {
		if(logic != null)
			logic.addShortOption(name, action, arity);
		if(printer != null) {
			char initiatorSymbol = logic == null ? '-' : logic.getInitiatorSymbol();
			String pfx = String.valueOf(initiatorSymbol) + name;
			printer.addOption(usage == null ? pfx : pfx + usage, help);
		}
		return this;
	}

	public OptionSpec option(char name, String usage, WordAction action, String help) {
		return option(name, usage, action, null, help);
	}

	public OptionSpec option(char name, WordAction action, OptionLogic.Arity arity, String help) {
		return option(name, null, action, arity, help);
	}

	public OptionSpec option(char name, WordAction action, String help) {
		return option(name, null, action, null, help);
	}

	public OptionSpec option(String name, String usage, WordAction action, OptionLogic.Arity arity, String help) {
		if(logic != null)
			logic.addLongOption(name, action, arity);
		if(printer != null) {
			char initiatorSymbol = logic == null ? '-' : logic.getInitiatorSymbol();
			String pfx = String.valueOf(initiatorSymbol);
			if(logic == null || logic.hasFlags(OptionLogic.FL_DOUBLE_LONG_OPTIONS))
				pfx += initiatorSymbol;
			pfx += name;
			printer.addOption(usage == null ? pfx : pfx + usage, help);
		}
		return this;
	}

	public OptionSpec option(String name, String usage, WordAction action, String help) {
		return option(name, usage, action, null, help);
	}

	public OptionSpec option(String name, WordAction action, OptionLogic.Arity arity, String help) {
		return option(name, null, action, arity, help);
	}

	public OptionSpec option(String name, WordAction action, String help) {
		return option(name, null, action, null, help);
	}

	public OptionSpec option(String longName, String longUsage, char shortName, String shortUsage,
			WordAction action, OptionLogic.Arity arity, String help) {
		char initiatorSymbol = logic == null ? '-' : logic.getInitiatorSymbol();
		String pfx = String.valueOf(initiatorSymbol);
		if(logic == null || logic.hasFlags(OptionLogic.FL_DOUBLE_LONG_OPTIONS))
			pfx += initiatorSymbol;
		pfx += longName;
		option(longName, longUsage, action, arity, help);
		option(shortName, shortUsage, action, arity, "same as " + pfx);
		return this;
	}

	public OptionSpec option(String longName, String longUsage, char shortName, String shortUsage,
			WordAction action, String help) {
		return option(longName, longUsage, shortName, shortUsage, action, null, help);
	}

	public OptionSpec option(String longName, char shortName,
			WordAction action, OptionLogic.Arity arity, String help) {
		return option(longName, null, shortName, null, action, arity, help);
	}

	public OptionSpec option(String longName, char shortName, WordAction action, String help) {
		return option(longName, null, shortName, null, action, null, help);
	}

}
