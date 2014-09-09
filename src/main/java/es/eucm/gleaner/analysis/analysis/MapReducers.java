package es.eucm.gleaner.analysis.analysis;

import es.eucm.gleaner.analysis.analysis.mappers.MapReducer;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.bson.BSONObject;
import org.bson.BasicBSONObject;
import scala.Tuple2;

import java.io.Serializable;
import java.util.ArrayList;

public class MapReducers implements
		Function<Tuple2<Object, BSONObject>, BSONObject>,
		Function2<BSONObject, BSONObject, BSONObject>, Serializable{

	private ArrayList<MapReducer> mapReducers = new ArrayList<MapReducer>();

	public void addMapper(MapReducer mapReducer) {
		mapReducers.add(mapReducer);
	}

	@Override
	public BSONObject call(Tuple2<Object, BSONObject> v1) throws Exception {
		BSONObject result = new BasicBSONObject();
		for (MapReducer mapReducer : mapReducers) {
			mapReducer.one(v1._2, result);
		}
		return result;
	}

	@Override
	public BSONObject call(BSONObject v1, BSONObject v2) throws Exception {
		BSONObject result = new BasicBSONObject();
		for (MapReducer mapReducer : mapReducers) {
			mapReducer.aggregate(v1, v2, result);
		}
		return result;
	}
}
