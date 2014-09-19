package es.eucm.gleaner.analysis.utils;

import org.apache.spark.api.java.function.PairFunction;
import org.bson.BSONObject;
import scala.Tuple2;

public class Split implements PairFunction<BSONObject, Object, BSONObject> {
	@Override
	public Tuple2<Object, BSONObject> call(BSONObject bsonObject)
			throws Exception {
		return new Tuple2<Object, BSONObject>(null, bsonObject);
	}
}
