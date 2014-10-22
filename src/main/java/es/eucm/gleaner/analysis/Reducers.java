package es.eucm.gleaner.analysis;

import es.eucm.gleaner.analysis.reducers.Aggregate2;
import es.eucm.gleaner.analysis.reducers.Reducer;
import es.eucm.gleaner.analysis.reducers.Sum;
import es.eucm.gleaner.analysis.utils.Q;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.bson.BSONObject;
import org.bson.BasicBSONObject;
import scala.Tuple2;

import java.io.Serializable;
import java.util.ArrayList;

public class Reducers implements
		Function<Tuple2<Object, BSONObject>, BSONObject>,
		Function2<BSONObject, BSONObject, BSONObject>, Serializable {

	private ArrayList<Reducer> reducers = new ArrayList<Reducer>();

	public void add(BSONObject dataReducers) {
		for (String var : dataReducers.keySet()) {
			BSONObject reducer = Q.get(var, dataReducers);
			String type = Q.get("type", reducer);
			if (Sum.ID.equals(type)) {
				this.reducers.add(new Sum(var));
			} else if (Aggregate2.ID.equals(type)) {
				this.reducers.add(new Aggregate2(var));
			}
		}
	}

	@Override
	public BSONObject call(Tuple2<Object, BSONObject> v1) throws Exception {
		BSONObject result = new BasicBSONObject();
		for (Reducer reducer : reducers) {
			try {
				reducer.one(v1._2, result);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	@Override
	public BSONObject call(BSONObject v1, BSONObject v2) throws Exception {
		BSONObject result = new BasicBSONObject();
		for (Reducer reducer : reducers) {
			try {
				reducer.aggregate(v1, v2, result);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	public BSONObject zero() {
		BSONObject result = new BasicBSONObject();
		for (Reducer reducer : reducers) {
			try {
				reducer.zero(result);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	public void extraOperations(BSONObject segmentResult) {
		for (Reducer reducer : reducers) {
			try {
				reducer.extraOperations(segmentResult);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
