package es.eucm.gleaner.analysis.utils;

import org.apache.spark.api.java.function.Function;
import org.bson.BSONObject;

import scala.Tuple2;

public class SegmentFilter implements
		Function<Tuple2<Object, BSONObject>, Boolean> {

	private String condition;

	public SegmentFilter(String condition) {
		this.condition = condition;
	}

	@Override
	public Boolean call(Tuple2<Object, BSONObject> v1) {
		Object result = ScriptEvaluator.evaluateExpression(v1._2.toMap(),
				condition);
		return result instanceof Boolean ? (Boolean) result : false;
	}
}
