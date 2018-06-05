package org.unclesniper.choreo.parseopt;

import java.util.List;
import java.util.LinkedList;
import java.util.regex.Pattern;

public class OptionPrinter {

	public static final class OptionHelp {

		private static final Pattern WHITESPACE_RE = Pattern.compile("\\s+");

		private final String usage;

		private final List<String> helpWords = new LinkedList<String>();

		public OptionHelp(String usage, String help) {
			this.usage = usage;
			for(String piece : OptionHelp.WHITESPACE_RE.split(help)) {
				if(!piece.isEmpty())
					helpWords.add(piece);
			}
		}

		public String getUsage() {
			return usage;
		}

		public Iterable<String> getHelpWords() {
			return helpWords;
		}

	}

	public interface Sink {

		void indent(int count);

		void print(String s);

		void endLine();

	}

	public static final int DEFAULT_INDENT = 4;

	public static final int DEFAULT_SEPARATE = 6;

	public static final int DEFAULT_SCREEN_WIDTH = 120;

	private static final int MIN_HELP_WIDTH = 16;

	private static final String SPACES = "                                                                ";

	private static final int SPACE_COUNT = OptionPrinter.SPACES.length();

	private final List<OptionHelp> options = new LinkedList<OptionHelp>();

	private int indentWidth;

	private int separateWidth;

	private int screenWidth;

	private int longestUsage;

	public OptionPrinter() {}

	public Iterable<OptionHelp> getOptions() {
		return options;
	}

	public void addOption(String usage, String help) {
		int ulength = usage.length();
		options.add(new OptionHelp(usage, help));
		if(ulength > longestUsage)
			longestUsage = ulength;
	}

	public int getIndentWidth() {
		return indentWidth;
	}

	public void setIndentWidth(int indentWidth) {
		this.indentWidth = indentWidth;
	}

	public int getSeparateWidth() {
		return separateWidth;
	}

	public void setSeparateWidth(int separateWidth) {
		this.separateWidth = separateWidth;
	}

	public int getScreenWidth() {
		return screenWidth;
	}

	public void setScreenWidth(int screenWidth) {
		this.screenWidth = screenWidth;
	}

	public void printHelp(Sink sink) {
		final int iwidth = indentWidth > 0 ? indentWidth : OptionPrinter.DEFAULT_INDENT;
		final int pwidth = separateWidth > 0 ? separateWidth : OptionPrinter.DEFAULT_SEPARATE;
		final int dtwidth = screenWidth > 0 ? screenWidth : OptionPrinter.DEFAULT_SCREEN_WIDTH;
		final int lcol = iwidth + longestUsage + pwidth;
		int mintwidth = lcol + OptionPrinter.MIN_HELP_WIDTH;
		final int twidth = dtwidth < mintwidth ? mintwidth : dtwidth;
		final int wwidth = twidth - lcol;
		//TODO
	}

}
