package es.eucm.gleaner.analysis.analysis.mappers;

import org.bson.BSONObject;

/**
 * Adds the value
 */
public class Sum implements MapReducer {

	protected String field;

	protected String sumField;

	public Sum(String field, String sumField) {
		this.field = field;
		this.sumField = sumField;
	}

	@Override
	public void one(BSONObject gameplayResult, BSONObject result) {
		Object value = gameplayResult.get(field);
		result.put(sumField, value);
	}

	@Override
	public void aggregate(BSONObject v1, BSONObject v2, BSONObject result) {
		Number value1 = (Number) v1.get(sumField);
		Number value2 = (Number) v2.get(sumField);
		boolean isLong = ((value1 instanceof Long) || (value1 instanceof Integer))
				&& ((value2 instanceof Long) || (value2 instanceof Integer));
		result.put(
                sumField,
				isLong ? value1.longValue() + value2.longValue() : value1
						.doubleValue() + value2.doubleValue());
	}
}
