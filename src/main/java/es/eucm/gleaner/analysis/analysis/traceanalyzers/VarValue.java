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
        return "var".equals(event);
    }

    @Override
    public void analyze(BSONObject trace, BSONObject gameplayResult) {
        String varName = Q.get("target", trace);
        if (target.equals(varName)){
            Object value = Q.get("value", trace);
            gameplayResult.put(target, value);
        }
    }
}
