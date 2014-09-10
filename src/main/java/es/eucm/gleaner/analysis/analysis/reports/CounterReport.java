package es.eucm.gleaner.analysis.analysis.reports;

import es.eucm.gleaner.analysis.Q;
import es.eucm.gleaner.analysis.analysis.MapReducers;
import es.eucm.gleaner.analysis.analysis.TracesAnalyzer;
import es.eucm.gleaner.analysis.analysis.mappers.Count;
import org.bson.BSONObject;

import java.util.Arrays;
import java.util.List;

public class CounterReport implements Report {

	private String variable;

	private Object value;

	@Override
	public void readReportData(BSONObject reportData) {
		this.variable = Q.get("variable", reportData);
		this.value = Q.get("value", reportData);
	}

	@Override
	public List<String> addTracesAnalyzers(TracesAnalyzer tracesAnalyzer) {
		return Arrays.asList(variable);
	}

	@Override
	public void addMapReducers(MapReducers mapReducers) {
		mapReducers.addMapper(new Count(variable, value));
	}
}
