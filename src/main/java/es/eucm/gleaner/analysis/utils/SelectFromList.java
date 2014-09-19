package es.eucm.gleaner.analysis.utils;

import org.apache.spark.api.java.function.PairFlatMapFunction;
import org.bson.BSONObject;
import scala.Tuple2;

import java.util.ArrayList;

public class SelectFromList
		implements
		PairFlatMapFunction<Tuple2<Object, Iterable<BSONObject>>, Object, BSONObject> {

	private boolean first;

	public SelectFromList(boolean first) {
		this.first = first;
	}

	@Override
	public Iterable<Tuple2<Object, BSONObject>> call(
			Tuple2<Object, Iterable<BSONObject>> tuple) throws Exception {
		Iterable<BSONObject> iterable;
		if (first) {
			iterable = tuple._2;
		} else {
			ArrayList<BSONObject> auxList = new ArrayList<BSONObject>();
			for (BSONObject bsonObject : tuple._2) {
				auxList.add(0, bsonObject);
			}
			iterable = auxList;
		}

		ArrayList<Tuple2<Object, BSONObject>> result = new ArrayList<Tuple2<Object, BSONObject>>();
		for (BSONObject bsonObject : iterable) {
			result.add(new Tuple2<Object, BSONObject>(tuple._1, bsonObject));
			return result;
		}
		return result;
	}
}
