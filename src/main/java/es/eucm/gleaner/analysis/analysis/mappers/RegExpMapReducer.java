package es.eucm.gleaner.analysis.analysis.mappers;

import org.bson.BSONObject;

public abstract class RegExpMapReducer implements MapReducer {

	private String prefix;

	private String regExp;

	public RegExpMapReducer(String prefix, String regExp) {
		this.prefix = prefix;
		this.regExp = regExp;
	}

	@Override
	public void one(BSONObject gameplayResult, BSONObject result) {
		for (String field : gameplayResult.keySet()) {
			if (field.equals(regExp) || field.matches("^" + regExp + "$")) {
				one(field, gameplayResult, result);
			}
		}
	}

	@Override
	public void aggregate(BSONObject v1, BSONObject v2, BSONObject result) {
		for (String field : v1.keySet()) {
			if (field.equals(prefix + regExp)
					|| field.matches("^" + prefix + regExp + "$")) {
				aggregate(field, v1, v2, result);
			}
		}
	}

	protected abstract void one(String field, BSONObject gameplayResult,
			BSONObject result);

	protected abstract void aggregate(String s, BSONObject v1, BSONObject v2,
			BSONObject result);

}
