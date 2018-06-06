package org.unclesniper.choreo.parseopt;

import java.util.List;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class OptionPrinter {

	public static final class OptionHelp {

		private static final Pattern LINEBREAK_RE = Pattern.compile("\\|{3}");

		private static final Pattern WHITESPACE_RE = Pattern.compile("\\s+");

		private static final Pattern ESC_SPACES_RE = Pattern.compile("^~+");

		private final String usage;

		private final List<String> helpWords = new LinkedList<String>();

		public OptionHelp(String usage, String help) {
			this.usage = usage;
			String[] lines = OptionHelp.LINEBREAK_RE.split(help);
			for(int i = 0; i < lines.length; ++i) {
				if(i > 0)
					helpWords.add(null);
				for(String piece : OptionHelp.WHITESPACE_RE.split(lines[i])) {
					if(!piece.isEmpty()) {
						Matcher m = OptionHelp.ESC_SPACES_RE.matcher(piece);
						if(m.find()) {
							StringBuilder builder = new StringBuilder();
							int count = m.end();
							for(; count > OptionPrinter.SPACE_COUNT; count -= OptionPrinter.SPACE_COUNT)
								builder.append(OptionPrinter.SPACES);
							if(count > 0)
								builder.append(OptionPrinter.SPACES, 0, count);
							builder.append(piece.substring(m.end()));
							piece = builder.toString();
						}
						helpWords.add(piece);
					}
				}
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

	public static class PrintStreamSink implements Sink {

		private PrintStream stream;

		public PrintStreamSink(PrintStream stream) {
			this.stream = stream;
		}

		public PrintStream getStream() {
			return stream;
		}

		public void setStream(PrintStream stream) {
			this.stream = stream;
		}

		public void indent(int count) {
			for(; count > OptionPrinter.SPACE_COUNT; count -= OptionPrinter.SPACE_COUNT)
				stream.print(OptionPrinter.SPACES);
			if(count > 0)
				stream.append(OptionPrinter.SPACES, 0, count);
		}

		public void print(String s) {
			stream.print(s);
		}

		public void endLine() {
			stream.println();
		}

	}

	public static class PrintWriterSink implements Sink {

		private PrintWriter writer;

		public PrintWriterSink(PrintWriter writer) {
			this.writer = writer;
		}

		public PrintWriter getWriter() {
			return writer;
		}

		public void setWriter(PrintWriter writer) {
			this.writer = writer;
		}

		public void indent(int count) {
			for(; count > OptionPrinter.SPACE_COUNT; count -= OptionPrinter.SPACE_COUNT)
				writer.print(OptionPrinter.SPACES);
			if(count > 0)
				writer.write(OptionPrinter.SPACES, 0, count);
		}

		public void print(String s) {
			writer.print(s);
		}

		public void endLine() {
			writer.println();
		}

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
		for(OptionHelp option : options) {
			sink.indent(iwidth);
			String usage = option.getUsage();
			sink.print(usage);
			int at = lcol;
			boolean empty = true;
			for(String word : option.getHelpWords()) {
				if(empty) {
					sink.indent(longestUsage - usage.length() + pwidth);
					empty = false;
				}
				if(word == null) {
					sink.endLine();
					sink.indent(lcol);
					at = lcol;
					continue;
				}
				if(at > lcol) {
					int after = at + word.length() + 1;
					if(after >= twidth) {
						sink.endLine();
						sink.indent(lcol);
						sink.print(word);
						at = lcol + word.length();
					}
					else {
						sink.print(" ");
						sink.print(word);
						at += word.length() + 1;
					}
				}
				else {
					sink.print(word);
					at += word.length();
				}
			}
			sink.endLine();
		}
	}

	public void printHelp(PrintStream sink) {
		printHelp(new PrintStreamSink(sink));
	}

	public void printHelp(PrintWriter sink) {
		printHelp(new PrintWriterSink(sink));
	}

}
