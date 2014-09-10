package es.eucm.gleaner.analysis.analysis.reports;

import es.eucm.gleaner.analysis.analysis.MapReducers;
import es.eucm.gleaner.analysis.analysis.TracesAnalyzer;
import org.bson.BSONObject;

import java.util.List;

public interface Report {

    void readReportData(BSONObject reportData);
    
    List<String> addTracesAnalyzers(TracesAnalyzer tracesAnalyzer);

    void addMapReducers(MapReducers mapReducers);

}
