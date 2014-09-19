package es.eucm.gleaner.analysis.utils;

import org.apache.spark.api.java.function.PairFunction;
import org.bson.BSONObject;
import scala.Tuple2;

public class ExtractFieldAsKey implements PairFunction<Tuple2<Object, BSONObject>, String, BSONObject> {

    private String field;

    public ExtractFieldAsKey(String field) {
        this.field = field;
    }

    @Override
    public Tuple2<String, BSONObject> call(Tuple2<Object, BSONObject> tuple2) throws Exception {
        BSONObject trace = tuple2._2;
        return new Tuple2<String, BSONObject>(trace
                .get(field).toString(), trace);
    }
}
