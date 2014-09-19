package es.eucm.gleaner.analysis;

import com.mongodb.BasicDBObject;
import es.eucm.gleaner.analysis.traces.DoubleValue;
import es.eucm.gleaner.analysis.traces.FunctionEvaluator;
import es.eucm.gleaner.analysis.traces.TraceAnalyzer;
import es.eucm.gleaner.analysis.traces.ZoneReached;
import es.eucm.gleaner.analysis.traces.ZoneTime;
import org.apache.spark.api.java.function.PairFunction;
import org.bson.BSONObject;
import scala.Tuple2;
import sun.org.mozilla.javascript.Context;
import sun.org.mozilla.javascript.ScriptableObject;

import java.util.ArrayList;
import java.util.List;

public class TracesAnalyzer implements
		PairFunction<Tuple2<String, Iterable<BSONObject>>, Object, BSONObject>, FunctionEvaluator {


	private ArrayList<TraceAnalyzer> traceAnalyzers = new ArrayList<TraceAnalyzer>();

	public ArrayList<TraceAnalyzer> readFunctions(List<String> functions) {
		traceAnalyzers.clear();
		try {
			Context context = Context.enter();
			ScriptableObject scope = context.initStandardObjects();
			prepareFunctions(context, scope);
			for (String function : functions) {
				context.evaluateString(scope, function, null, 1, null);
			}
		} finally {
			Context.exit();
		}
		return traceAnalyzers;
	}

	private void prepareFunctions(Context context, ScriptableObject scope) {
		Object wrappedOut = Context.javaToJS(this, scope);
		ScriptableObject.putProperty(scope, "traces", wrappedOut);
		for (String function : FUNCTIONS) {
			context.evaluateString(scope, "function " + function + "(){traces."
					+ function + "(Array.prototype.slice.call(arguments))}",
					null, 1, null);
		}
	}

	public long ms(List<Object> arguments) {
		traceAnalyzers.add(new ZoneTime((String) arguments.get(0)));
        return 0;
	}

	public boolean reach(List<Object> arguments) {
		traceAnalyzers.add(new ZoneReached((String) arguments.get(0)));
        return false;
	}

	public double doubleValue(List<Object> arguments) {
		traceAnalyzers.add(new DoubleValue((String) arguments.get(0)));
        return 0;
	}

	@Override
	public Tuple2<Object, BSONObject> call(
			Tuple2<String, Iterable<BSONObject>> tuple2) {

		BSONObject gameplayResult = new BasicDBObject();
		gameplayResult.put(TraceAnalyzer.GAMEPLAY_ID, tuple2._1);

		for (TraceAnalyzer traceAnalyzer : traceAnalyzers) {
			traceAnalyzer.defaultValues(gameplayResult);
		}

		for (BSONObject trace : tuple2._2) {
			if (trace.containsField(TraceAnalyzer.EVENT)) {
				String event = (String) trace.get(TraceAnalyzer.EVENT);
				for (TraceAnalyzer traceAnalyzer : traceAnalyzers) {
					if (traceAnalyzer.interestedIn(event)) {
						traceAnalyzer.analyze(trace, gameplayResult);
					}
				}
			}
		}
		return new Tuple2<Object, BSONObject>(null, gameplayResult);
	}

}
