package es.eucm.gleaner.analysis;

import es.eucm.gleaner.analysis.analysis.MapReducers;
import es.eucm.gleaner.analysis.analysis.TracesAnalyzer;
import es.eucm.gleaner.analysis.analysis.reports.Report;
import org.bson.BSONObject;

import java.util.Arrays;
import java.util.List;

public class MockReport implements Report {

	private String[] derivedVars;

	public MockReport() {
	}

	@Override
	public void readReportData(BSONObject reportData) {
        this.derivedVars = Q.get("vars", reportData);
	}

	@Override
	public List<String> addTracesAnalyzers(TracesAnalyzer tracesAnalyzer) {
		return Arrays.asList(derivedVars);
	}

	@Override
	public void addMapReducers(MapReducers mapReducers) {

	}
}
