package es.eucm.gleaner.analysis;

import org.bson.BSONObject;

public class Q {

	public static <T> T get(String key, BSONObject bsonObject) {
		return bsonObject.containsField(key) ? (T) bsonObject.get(key) : null;
	}
}
