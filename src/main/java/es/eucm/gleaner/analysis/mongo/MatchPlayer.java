package es.eucm.gleaner.analysis.mongo;

import org.apache.spark.api.java.function.PairFunction;
import org.bson.BSONObject;

import scala.Tuple2;
import es.eucm.gleaner.analysis.utils.Q;

public class MatchPlayer
		implements
		PairFunction<Tuple2<String, Tuple2<BSONObject, BSONObject>>, Object, BSONObject> {
	@Override
	public Tuple2<Object, BSONObject> call(
			Tuple2<String, Tuple2<BSONObject, BSONObject>> tuple2)
			throws Exception {
		BSONObject result = tuple2._2._1;
		BSONObject gameplay = tuple2._2._2;
		return new Tuple2<Object, BSONObject>(Q.get("playerId", gameplay)
				.toString(), result);
	}
}
