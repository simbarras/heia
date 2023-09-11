package ch.epfl.general_libraries.results;

import java.util.List;

public interface AdvancedDataRetriever extends  AbstractDataRetriever {
	public abstract List<DataSeries> getChartValues(DataRetrievalOptions options, String methodName);
}
