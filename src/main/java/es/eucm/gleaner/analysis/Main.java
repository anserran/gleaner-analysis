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

		Object loading = versionData.containsField("analyzing") ? versionData
				.get("analyzing") : Boolean.FALSE;

		if (loading instanceof Boolean && !(Boolean) loading) {
			// JavaSparkContext sc = new JavaSparkContext();

			db.getCollection("versions").update(
					new BasicDBObject("_id", versionData.get("_id")),
					new BasicDBObject("$set",
							new BasicDBObject("loading", true)));

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

			// Write to mongo gameplays
			db.getCollection("gameplaysresults_" + versionId).drop();

			config.set("mongo.output.uri", "mongodb://" + mongoHost + ":"
					+ mongoPort + "/" + mongoDB + ".gameplaysresults_"
					+ versionId);
			gameplaysResults
					.saveAsNewAPIHadoopFile("file:///bogus", Object.class,
							Object.class, MongoOutputFormat.class, config);

			// Read players

					BSONObject.class);

			gameplaysResults = gameplaysResults
					.mapToPair(new ExtractFieldAsKey("gameplayId"))
					.join(gameplays.mapToPair(new ExtractFieldAsKey("_id")))
					.mapToPair(
							new PairFunction<Tuple2<String, Tuple2<BSONObject, BSONObject>>, Object, BSONObject>() {
								@Override
								public Tuple2<Object, BSONObject> call(
										Tuple2<String, Tuple2<BSONObject, BSONObject>> tuple2)
										throws Exception {
									BSONObject result = tuple2._2._1;
									BSONObject gameplay = tuple2._2._2;
									return new Tuple2<Object, BSONObject>(Q
											.get("playerId", gameplay)
											.toString(), result);
								}
							});

			ArrayList<DBObject> segmentResults = gameplaysAnalysis
					.calculateSegments(gameplaysResults, versionData);
			// Write to mongo segment results
			db.getCollection("segments_" + versionId).drop();
			db.getCollection("segments_" + versionId).insert(segmentResults);
			db.getCollection("versions").update(
					new BasicDBObject("_id", versionData.get("_id")),
					new BasicDBObject("$set", new BasicDBObject("analyzing",
							false)));
		}
	}
}
