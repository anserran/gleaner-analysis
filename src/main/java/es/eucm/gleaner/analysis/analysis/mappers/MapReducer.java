package es.eucm.gleaner.analysis.analysis.mappers;

import org.bson.BSONObject;

import java.io.Serializable;

public interface MapReducer extends Serializable {

	void one(BSONObject gameplayResult, BSONObject result);

	void aggregate(BSONObject v1, BSONObject v2, BSONObject result);
}
