package es.eucm.gleaner.analysis.traces;

import es.eucm.gleaner.analysis.utils.Q;
import org.bson.BSONObject;

public class DoubleValue implements TraceAnalyzer {

    public static final String PREFIX = "_double_value_";

    private String target;

    public DoubleValue(String target) {
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
            gameplayResult.put(PREFIX + target, value);
        }
    }
}
