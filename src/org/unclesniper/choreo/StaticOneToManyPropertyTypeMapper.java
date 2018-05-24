package org.unclesniper.choreo;

import java.util.List;
import java.util.LinkedList;

public abstract class StaticOneToManyPropertyTypeMapper<SourceT, SymbolT>
		extends OneToManyPropertyTypeMapper<SourceT, SymbolT> {

	private final Class<SourceT> sourceType;

	private final List<Pair<SymbolT>> destinationTypes = new LinkedList<Pair<SymbolT>>();

	public StaticOneToManyPropertyTypeMapper(Class<SourceT> sourceType) {
		this.sourceType = sourceType;
	}

	public Class<SourceT> getSourceType() {
		return sourceType;
	}

	public Iterable<Pair<SymbolT>> getDestinationTypes() {
		return destinationTypes;
	}

	protected final void addDestinationType(Class<?> type, SymbolT symbol) {
		destinationTypes.add(new Pair<SymbolT>(type, symbol));
	}

}
