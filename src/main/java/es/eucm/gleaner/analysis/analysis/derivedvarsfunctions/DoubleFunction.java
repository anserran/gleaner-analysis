package es.eucm.gleaner.analysis.analysis.derivedvarsfunctions;

import es.eucm.gleaner.analysis.Q;
import es.eucm.gleaner.analysis.analysis.traceanalyzers.TraceAnalyzer;
import es.eucm.gleaner.analysis.analysis.traceanalyzers.VarValue;
import org.bson.BSONObject;

import java.util.ArrayList;
import java.util.List;

public class DoubleFunction implements DerivedVarFunction {
	@Override
	public String getFunctionName() {
		return "double";
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
			traceAnalyzers.add(new VarValue((String) arguments.get(0)));
		}
		return traceAnalyzers;
	}

	@Override
	public Object call(BSONObject gameplayResult, List<Object> arguments) {
		String field = (String) arguments.get(0);
		return Q.get(field, gameplayResult);
	}
}
