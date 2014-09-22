package es.eucm.gleaner.analysis.mongo;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import es.eucm.gleaner.analysis.utils.Q;
import org.apache.spark.api.java.function.VoidFunction;
import org.bson.BSONObject;

import scala.Tuple2;

public class Update implements VoidFunction<Tuple2<Object, BSONObject>> {

	private String mongoHost;

	private int mongoPort;

	private String mongoDB;

	private String collectionName;

	private String fieldKey;

	public Update(String mongoHost, int mongoPort, String mongoDB,
			String collectionName, String fieldKey) {
		this.mongoHost = mongoHost;
		this.mongoPort = mongoPort;
		this.mongoDB = mongoDB;
		this.collectionName = collectionName;
		this.fieldKey = fieldKey;
	}

	@Override
	public void call(Tuple2<Object, BSONObject> tuple2) throws Exception {
		DBCollection collection = Q.getCollection(mongoHost, mongoPort,
				mongoDB, collectionName);

		BSONObject dbObject = tuple2._2;
		if (dbObject.containsField("_id")) {
			dbObject.removeField("_id");
		}

		collection.update(
				new BasicDBObject(fieldKey, Q.get(fieldKey, dbObject)),
				new BasicDBObject("$set", new BasicDBObject(dbObject.toMap())),
				true, false);
	}
}
