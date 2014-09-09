package es.eucm.gleaner.analysis.analysis.derivedvarsfunctions;

import es.eucm.gleaner.analysis.analysis.traceanalyzers.TraceAnalyzer;
import es.eucm.gleaner.analysis.analysis.traceanalyzers.ZoneReached;
import org.bson.BSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * If a gameplay passes through a concrete zone
 */
public class ReachFunction implements DerivedVarFunction {

	@Override
	public String getFunctionName() {
		return "reach";
	}

    @Override
    public String getResultType() {
        return "boolean";
    }

    @Override
	public List<TraceAnalyzer> getTraceAnalyzers(
			List<List<Object>> argumentsList) {
		ArrayList<TraceAnalyzer> traceAnalyzers = new ArrayList<TraceAnalyzer>();
		for (List<Object> arguments : argumentsList) {
			String zone = (String) arguments.get(0);
			String varName = "_z_reached_" + zone;
			traceAnalyzers.add(new ZoneReached(varName, zone));
		}
		return traceAnalyzers;
	}

	@Override
	public Object call(BSONObject gameplayResult, List<Object> arguments) {
		String zone = (String) arguments.get(0);
		return gameplayResult.get("_z_reached_" + zone);
	}
}
