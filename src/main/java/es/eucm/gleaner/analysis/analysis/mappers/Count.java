package es.eucm.gleaner.analysis.analysis.mappers;

import es.eucm.gleaner.analysis.Q;
import es.eucm.gleaner.analysis.ScriptEvaluator;
import org.bson.BSONObject;

public class Count implements MapReducer {

    private String variable;

    private String condition;

    public Count(String variable, String condition) {
        this.variable = variable;
        this.condition = condition;
    }

    @Override
    public void one(BSONObject gameplayResult, BSONObject result) {
        Object value = ScriptEvaluator.evaluateExpression(gameplayResult.toMap(), condition);
        if ( Boolean.TRUE.equals(value)){
            result.put(variable, 1);
        } else {
            result.put(variable, 0);
        }
    }

    @Override
    public void aggregate(BSONObject v1, BSONObject v2, BSONObject result) {
        Number count1 = Q.get(variable, v1);
        Number count2 = Q.get(variable, v2);
        result.put(variable, count1.intValue() + count2.intValue());
    }
}
