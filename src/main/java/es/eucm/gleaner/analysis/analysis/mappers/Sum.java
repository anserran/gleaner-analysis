package es.eucm.gleaner.analysis.analysis.mappers;

import org.bson.BSONObject;

/**
 * Adds the value
 */
public class Sum extends RegExpMapReducer {

	public static final String SUM = "sum_";

	public Sum(String regExp) {
		super(SUM, regExp);
	}

	@Override
	public void one(String field, BSONObject gameplayResult, BSONObject result) {
		Object value = gameplayResult.get(field);
		result.put(SUM + field, value);
	}

	@Override
	public void aggregate(String field, BSONObject v1, BSONObject v2,
			BSONObject result) {
		Number value1 = (Number) v1.get(field);
		Number value2 = (Number) v2.get(field);
		boolean isLong = ((value1 instanceof Long) || (value1 instanceof Integer))
				&& ((value2 instanceof Long) || (value2 instanceof Integer));
		result.put(field, isLong ? value1.longValue() + value2.longValue()
				: value1.doubleValue() + value2.doubleValue());
	}
}
