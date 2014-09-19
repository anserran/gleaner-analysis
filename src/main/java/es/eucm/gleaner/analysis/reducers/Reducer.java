package es.eucm.gleaner.analysis.reducers;

import org.bson.BSONObject;

import java.io.Serializable;

public interface Reducer extends Serializable {

    void zero(BSONObject result);

	void one(BSONObject gameplayResult, BSONObject result);

	void aggregate(BSONObject v1, BSONObject v2, BSONObject result);

    void extraOperations(BSONObject segmentResult);
}
