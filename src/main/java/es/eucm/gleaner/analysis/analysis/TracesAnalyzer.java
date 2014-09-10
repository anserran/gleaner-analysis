package es.eucm.gleaner.analysis.analysis;

import com.mongodb.BasicDBObject;
import es.eucm.gleaner.analysis.analysis.traceanalyzers.TraceAnalyzer;
import org.apache.spark.api.java.function.PairFunction;
import org.bson.BSONObject;
import scala.Tuple2;

import java.util.ArrayList;
import java.util.List;

public class TracesAnalyzer implements
		PairFunction<Tuple2<String, Iterable<BSONObject>>, Object, BSONObject> {

	private DerivedVarsProcessor derivedVarsProcessor;

	private ArrayList<TraceAnalyzer> traceAnalyzers;

	private ArrayList<String> varsAnalyzed = new ArrayList<String>();

	public TracesAnalyzer(DerivedVarsProcessor derivedVarsProcessor) {
		this.derivedVarsProcessor = derivedVarsProcessor;
		traceAnalyzers = new ArrayList<TraceAnalyzer>();
	}

	public void add(TraceAnalyzer traceAnalyzer) {
		String varGenerated = traceAnalyzer.getVarsGenerated();
		for (String var : varsAnalyzed) {
			if (varGenerated.equals(var)
					|| varGenerated.matches("^" + var + "$")) {
				return;
			}
		}
		varsAnalyzed.add(varGenerated);
		traceAnalyzers.add(traceAnalyzer);
	}

    public void addAll(List<TraceAnalyzer> traceAnalyzers) {
        for (TraceAnalyzer tracesAnalyzer: traceAnalyzers) {
            add(tracesAnalyzer);
        }
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
		derivedVarsProcessor.evaluateAllDerivedVars(gameplayResult);
		return new Tuple2<Object, BSONObject>(null, gameplayResult);
	}
}
