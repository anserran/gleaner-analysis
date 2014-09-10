package es.eucm.gleaner.analysis.analysis.mappers;

import es.eucm.gleaner.analysis.Q;
import org.bson.BSONObject;

public class Count implements MapReducer {

    public static final String COUNT = "_count_";

    private String field;

    private Object value;

    public Count(String field, Object value) {
        this.field = field;
        this.value = value;
    }

    @Override
    public void one(BSONObject gameplayResult, BSONObject result) {
        Object resultValue = Q.get(field, gameplayResult);
        if ( value == resultValue || value.equals(resultValue)){
            result.put(COUNT + field + value, 1);
        } else {
            result.put(COUNT + field + value, 0);
        }
    }

    @Override
    public void aggregate(BSONObject v1, BSONObject v2, BSONObject result) {
        Number count1 = Q.get(COUNT + field + value, v1);
        Number count2 = Q.get(COUNT + field + value, v2);
        result.put(COUNT + field + value, count1.intValue() + count2.intValue());
    }
}
