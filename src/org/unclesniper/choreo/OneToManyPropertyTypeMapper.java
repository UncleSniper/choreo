package org.unclesniper.choreo;

public abstract class OneToManyPropertyTypeMapper<SourceT, SymbolT> implements PropertyTypeMapper {

	public static final class Pair<SymbolT> {

		private final Class<?> type;

		private final SymbolT symbol;

		public Pair(Class<?> type, SymbolT symbol) {
			this.type = type;
			this.symbol = symbol;
		}

		public Class<?> getType() {
			return type;
		}

		public SymbolT getSymbol() {
			return symbol;
		}

	}

	public OneToManyPropertyTypeMapper() {}

	protected abstract Class<SourceT> getSourceType();

	protected abstract Iterable<Pair<SymbolT>> getDestinationTypes();

	protected abstract Object map(BuildContext context, SourceT value, SymbolT symbol);

	public boolean canMapPropertyType(Class<?> fromType, Class<?> toType) {
		Class<SourceT> source = getSourceType();
		if(source != null && (fromType == null || !source.isAssignableFrom(fromType)))
			return false;
		if(fromType == null && toType.isPrimitive())
			return false;
		for(Pair<SymbolT> pair : getDestinationTypes()) {
			if(toType.isAssignableFrom(pair.getType()))
				return true;
		}
		return false;
	}

	public Object mapPropertyValue(BuildContext context, Object value, Class<?> toType) {
		for(Pair<SymbolT> pair : getDestinationTypes()) {
			if(toType.isAssignableFrom(pair.getType()))
				return map(context, value == null ? null : getSourceType().cast(value), pair.getSymbol());
		}
		return null;
	}

}
