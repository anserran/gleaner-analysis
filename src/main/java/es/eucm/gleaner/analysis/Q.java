package es.eucm.gleaner.analysis;

import org.bson.BSONObject;

public class Q {

	public static <T> T get(String key, BSONObject bsonObject) {
		return bsonObject.containsField(key) ? (T) bsonObject.get(key) : null;
	}

    public static <T> T getValue(BSONObject bsonObject){
        return get("value", bsonObject);
    }
}
