package es.eucm.gleaner.analysis.analysis.reports;

import es.eucm.gleaner.analysis.Q;
import es.eucm.gleaner.analysis.analysis.MapReducers;
import es.eucm.gleaner.analysis.analysis.TracesAnalyzer;
import es.eucm.gleaner.analysis.analysis.mappers.Count;
import org.bson.BSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CounterReport implements Report {

	private String condition;

	private String counterVariable;

	@Override
	public void readReportData(BSONObject reportData) {
		this.condition = Q.get("condition", reportData);
		this.counterVariable = Q.get("counterVariable", reportData);
	}

	@Override
	public List<String> addTracesAnalyzers(TracesAnalyzer tracesAnalyzer) {
        ArrayList<String> vars = new ArrayList<String>();
        if (condition != null) {
            Pattern pattern = Pattern.compile("([a-zA-Z$_][a-zA-Z$_0-9]*)");
            Matcher matcher = pattern.matcher(condition);
            while (matcher.find()) {
                vars.add(matcher.group());
            }
        }
		return vars;
	}

	@Override
	public void addMapReducers(MapReducers mapReducers) {
		mapReducers.addMapper(new Count(counterVariable, condition));
	}
}
