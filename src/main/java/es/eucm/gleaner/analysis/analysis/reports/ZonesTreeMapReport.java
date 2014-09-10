package es.eucm.gleaner.analysis.analysis.reports;

import es.eucm.gleaner.analysis.analysis.MapReducers;
import es.eucm.gleaner.analysis.analysis.TracesAnalyzer;
import es.eucm.gleaner.analysis.analysis.traceanalyzers.AllZonesTime;
import org.bson.BSONObject;

import java.util.List;

public class ZonesTreeMapReport implements Report {

    @Override
    public void readReportData(BSONObject reportData) {

    }

    @Override
	public List<String> addTracesAnalyzers(TracesAnalyzer tracesAnalyzer) {
		tracesAnalyzer.add(new AllZonesTime());
		return null;
	}

	@Override
	public void addMapReducers(MapReducers mapReducers) {

	}
}
