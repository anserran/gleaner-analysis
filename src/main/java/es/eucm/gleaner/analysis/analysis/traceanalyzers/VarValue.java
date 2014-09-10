package es.eucm.gleaner.analysis.analysis.traceanalyzers;

import es.eucm.gleaner.analysis.Q;
import org.bson.BSONObject;

public class VarValue implements TraceAnalyzer{

    private String target;

    public VarValue(String target) {
        this.target = target;
    }

    @Override
    public void defaultValues(BSONObject gameplayResult) {

    }

    @Override
    public boolean interestedIn(String event) {
        return VAR.equals(event);
    }

    @Override
    public void analyze(BSONObject trace, BSONObject gameplayResult) {
        String varName = Q.getTarget(trace);
        if (target.equals(varName)){
            Object value = Q.getValue(trace);
            gameplayResult.put(target, value);
        }
    }

    @Override
    public String getVarsGenerated() {
        return target;
    }
}
