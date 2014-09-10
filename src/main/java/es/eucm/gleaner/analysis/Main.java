package es.eucm.gleaner.analysis;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.hadoop.MongoInputFormat;
import com.mongodb.hadoop.MongoOutputFormat;
import es.eucm.gleaner.analysis.analysis.GameplaysAnalysis;
import org.apache.hadoop.conf.Configuration;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.bson.BSONObject;
import org.bson.types.ObjectId;

import java.net.UnknownHostException;

public class Main {
	public static void main(final String[] args) throws UnknownHostException {
		JavaSparkContext sc = new JavaSparkContext();
		String versionId = args[0];

		String mongoHost = System.getenv("MONGO_HOST");
		Integer mongoPort = Integer.parseInt(System.getenv("MONGO_PORT"));
		String mongoDB = System.getenv("MONGO_DB");

		DB db;
		try {
			db = new Mongo(mongoHost, mongoPort).getDB(mongoDB);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return;
		}

		DBObject versionData = db.getCollection("versions").findOne(
				new BasicDBObject("_id", new ObjectId(versionId)));

		Configuration config = new Configuration();
		config.set("mongo.input.uri", "mongodb://" + mongoHost + ":"
				+ mongoPort + "/" + mongoDB + ".traces_" + versionId);

		// Collection of traces<null, trace>
		JavaPairRDD<Object, BSONObject> traces = sc.newAPIHadoopRDD(config,
				MongoInputFormat.class, Object.class, BSONObject.class);

		GameplaysAnalysis gameplaysAnalysis = new GameplaysAnalysis();
		gameplaysAnalysis.read(versionData);
		JavaPairRDD<Object, BSONObject> gameplaysResults = gameplaysAnalysis
				.calculateGameplayResults(traces);

		DBObject versionResult = gameplaysAnalysis
				.calculateSegmentResult(gameplaysResults);

		versionResult.put("versionId", new ObjectId(versionId));
		BasicDBObject update = new BasicDBObject("versionId", new ObjectId(
				versionId));
		db.getCollection("versionsresults").update(update, versionResult, true,
				false);

		// Write to mongo
		db.getCollection("gameplaysresults_" + versionId).drop();

		config.set("mongo.output.uri", "mongodb://" + mongoHost + ":"
				+ mongoPort + "/" + mongoDB + ".gameplaysresults_" + versionId);
		gameplaysResults.saveAsNewAPIHadoopFile("file:///bogus", Object.class,
				Object.class, MongoOutputFormat.class, config);
	}
}
