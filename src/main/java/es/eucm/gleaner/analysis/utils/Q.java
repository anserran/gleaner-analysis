package es.eucm.gleaner.analysis.utils;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;
import org.bson.BSONObject;

import java.net.UnknownHostException;

public class Q {

	public static <T> T get(String key, BSONObject bsonObject) {
		return bsonObject.containsField(key) ? (T) bsonObject.get(key) : null;
	}

	public static <T> T getOrSet(String key, BSONObject bsonObject,
			T defaultValue) {
		T value = get(key, bsonObject);
		if (value == null) {
			bsonObject.put(key, defaultValue);
			return defaultValue;
		} else {
			return value;
		}
	}

	public static <T> T getValue(BSONObject bsonObject) {
		return get("value", bsonObject);
	}

	public static <T> T getTarget(BSONObject bsonObject) {
		return get("target", bsonObject);
	}

	public static long getLong(String key, BSONObject bsonObject) {
		Object o = Q.get(key, bsonObject);
		if (o instanceof Number) {
			return ((Number) o).longValue();
		}
		return 0;
	}

	public static boolean getBoolean(String key, BSONObject bsonObject) {
		Object o = Q.get(key, bsonObject);
		if (o instanceof Boolean) {
			return ((Boolean) o);
		}
		return false;
	}

	public static double getDouble(String key, BSONObject bsonObject) {
		Object o = Q.get(key, bsonObject);
		if (o instanceof Number) {
			return ((Number) o).doubleValue();
		}
		return 0;
	}

	public static DBCollection getCollection(String mongoHost, int mongoPort,
			String mongoDB, String collection) {
		try {
			return new Mongo(mongoHost, mongoPort).getDB(mongoDB)
					.getCollection(collection);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static DB getDB(String mongoHost, int mongoPort, String mongoDB) {
		try {
			return new Mongo(mongoHost, mongoPort).getDB(mongoDB);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return null;
		}
	}
}
