package org.unclesniper.choreo.parseopt;

import java.util.List;
import java.util.LinkedList;

public class UsageWordAction implements WordAction {

	private OptionPrinter printer;

	private OptionPrinter.Sink sink;

	private final List<String> headers = new LinkedList<String>();

	private int status;

	public UsageWordAction(OptionPrinter printer, String... headers) {
		this(printer, null, headers);
	}

	public UsageWordAction(OptionPrinter printer, OptionPrinter.Sink sink, String... headers) {
		this.printer = printer;
		this.sink = sink;
		for(String line : headers)
			this.headers.add(line);
	}

	public OptionPrinter getPrinter() {
		return printer;
	}

	public void setPrinter(OptionPrinter printer) {
		this.printer = printer;
	}

	public OptionPrinter.Sink getSink() {
		return sink;
	}

	public void setSink(OptionPrinter.Sink sink) {
		this.sink = sink;
	}

	public Iterable<String> getHeaders() {
		return headers;
	}

	public void addHeader(String line) {
		headers.add(line);
	}

	public void addHeaders(String... lines) {
		for(String line : lines)
			headers.add(line);
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public void wordEncountered(String key, String value) {
		if(sink != null) {
			for(String line : headers) {
				if(line != null)
					sink.print(line);
				sink.endLine();
			}
			printer.printHelp(sink);
		}
		else {
			for(String line : headers) {
				if(line != null)
					System.err.print(line);
				System.err.println();
			}
			printer.printHelp(System.err);
		}
		if(status >= 0)
			throw new StopExecution(status);
	}

}
