package es.eucm.gleaner.analysis.analysis.mappers;

import org.bson.BSONObject;

public class Mean extends Sum {

	private String meanField;

	public Mean(String aggregatedField) {
		super(aggregatedField + "_sum");
		this.meanField = aggregatedField;
	}

	@Override
	public void one(String sumField, BSONObject gameplayResult,
			BSONObject result) {
		super.one(gameplayResult, result);
		result.put(meanField, gameplayResult.get(sumField));
		result.put(meanField + "_count", 1);
	}

	@Override
	public void aggregate(String sumField, BSONObject v1, BSONObject v2,
			BSONObject result) {
		super.aggregate(v1, v2, result);
		Number count1 = (Number) v1.get(meanField + "_count");
		Number count2 = (Number) v2.get(meanField + "_count");

		long count = count1.longValue() + count2.longValue();
		result.put(meanField + "_count", count);

		Number sum = (Number) result.get(sumField);
		result.put(meanField, sum.doubleValue() / (double) count);
	}
}
