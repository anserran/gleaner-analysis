package es.eucm.gleaner.analysis.mongo;

import org.apache.spark.api.java.function.PairFunction;
import org.bson.BSONObject;

import scala.Tuple2;

public class AddPlayerData
		implements
		PairFunction<Tuple2<String, Tuple2<BSONObject, BSONObject>>, Object, BSONObject> {
	@Override
	public Tuple2<Object, BSONObject> call(
			Tuple2<String, Tuple2<BSONObject, BSONObject>> tuple2)
			throws Exception {
		BSONObject playerdata = tuple2._2._1;
		BSONObject gameplayResult = tuple2._2._2;
		gameplayResult.put("player", playerdata);
		return new Tuple2<Object, BSONObject>(tuple2._1, gameplayResult);
	}
}
