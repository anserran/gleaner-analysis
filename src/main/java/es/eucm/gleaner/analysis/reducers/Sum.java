package es.eucm.gleaner.analysis.reducers;

import org.bson.BSONObject;

/**
 * Adds the value
 */
public class Sum implements Reducer {

    public static String ID = "sum";

	protected String sumField;

	public Sum(String sumField) {
		this.sumField = sumField;
	}

	@Override
	public void zero(BSONObject result) {
		result.put(sumField, 0);
	}

	@Override
	public void one(BSONObject gameplayResult, BSONObject result) {
		Object value = gameplayResult.get(sumField);
		result.put(sumField, value);
	}

	@Override
	public void aggregate(BSONObject v1, BSONObject v2, BSONObject result) {
		Number value1 = (Number) v1.get(sumField);
		Number value2 = (Number) v2.get(sumField);
		result.put(sumField, value1.doubleValue() + value2.doubleValue());
	}

	@Override
	public void extraOperations(BSONObject segmentResult) {

	}
}
