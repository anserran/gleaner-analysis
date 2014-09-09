package es.eucm.gleaner.analysis.analysis.derivedvarsfunctions;

import es.eucm.gleaner.analysis.analysis.traceanalyzers.TraceAnalyzer;
import es.eucm.gleaner.analysis.analysis.traceanalyzers.ZoneTime;
import org.bson.BSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Returns time spent in a zone
 */
public class MsFunction implements DerivedVarFunction {

	@Override
	public String getFunctionName() {
		return "ms";
	}

    @Override
    public String getResultType() {
        return "double";
    }

    @Override
	public List<TraceAnalyzer> getTraceAnalyzers(
			List<List<Object>> argumentsList) {
		ArrayList<TraceAnalyzer> traceAnalyzers = new ArrayList<TraceAnalyzer>();
		for (List<Object> arguments : argumentsList) {
			String zone = (String) arguments.get(0);
			traceAnalyzers.add(new ZoneTime("_z_ms_" + zone, zone));
		}
		return traceAnalyzers;
	}

	@Override
	public Object call(BSONObject gameplayResult, List<Object> arguments) {
		String zone = (String) arguments.get(0);
		return gameplayResult.get("_z_ms_" + zone);
	}
}
