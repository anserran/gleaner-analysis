package es.eucm.gleaner.analysis.mongo;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.hadoop.MongoInputFormat;
import es.eucm.gleaner.analysis.Analysis;
import es.eucm.gleaner.analysis.utils.ExtractFieldAsKey;
import org.apache.hadoop.conf.Configuration;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.bson.BSONObject;
import org.bson.types.ObjectId;

import java.net.UnknownHostException;
import java.util.List;

public class MongoAnalysis extends Analysis {

	private String versionId;

	private String mongoHost;

	private Integer mongoPort;

	private String mongoDB;

	private DB db;

	private JavaSparkContext sc;

	public MongoAnalysis(String versionId, String mongoHost, Integer mongoPort,
			String mongoDB) {
		this.versionId = versionId;
		this.mongoHost = mongoHost;
		this.mongoPort = mongoPort;
		this.mongoDB = mongoDB;
	}

	public void execute(SparkConf conf) {
		execute(conf, false);
	}

	public void execute(SparkConf conf, boolean force) {
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

		if (force || (loading instanceof Boolean && !(Boolean) loading)) {
			setVersionLoading(true);
			try {
				if (sc == null) {
					sc = conf == null ? new JavaSparkContext()
							: new JavaSparkContext(conf);
				}

				Configuration config = new Configuration();
				config.set("mongo.input.uri", "mongodb://" + mongoHost + ":"
						+ mongoPort + "/" + mongoDB + ".traces_" + versionId);

				// Collection of traces<null, trace>
				JavaPairRDD<Object, BSONObject> traces = sc.newAPIHadoopRDD(
						config, MongoInputFormat.class, Object.class,
						BSONObject.class);

				DBObject calculatedData = force ? null : db.getCollection(
						"calculated_data").findOne(
						new BasicDBObject("versionId", versionData.get("_id")));

				if (calculatedData == null) {
					calculatedData = new BasicDBObject();
					calculatedData.put("versionId", versionData.get("_id"));
				}

				Analysis analysis = new Analysis();
				JavaPairRDD<Object, BSONObject> gameplaysResults = analysis
						.analyzeGameplays(traces, versionData, calculatedData);

				gameplaysResults
						.foreach(new Update(mongoHost, mongoPort, mongoDB,
								"gameplaysresults_" + versionId, "gameplayId"));

				// Read gameplays
				config.set("mongo.input.uri", "mongodb://" + mongoHost + ":"
						+ mongoPort + "/" + mongoDB + ".gameplays_" + versionId);

				JavaPairRDD<Object, BSONObject> gameplays = sc.newAPIHadoopRDD(
						config, MongoInputFormat.class, Object.class,
						BSONObject.class);

				gameplaysResults = gameplaysResults
						.mapToPair(new ExtractFieldAsKey("gameplayId"))
						.join(gameplays.mapToPair(new ExtractFieldAsKey("_id")))
						.mapToPair(new MatchPlayer());

				// Players
				config.set("mongo.input.uri", "mongodb://" + mongoHost + ":"
						+ mongoPort + "/" + mongoDB + ".players");

				JavaPairRDD<Object, BSONObject> players = sc.newAPIHadoopRDD(
						config, MongoInputFormat.class, Object.class,
						BSONObject.class);

				gameplaysResults = players
						.mapToPair(new ExtractFieldAsKey("_id"))
						.join(gameplaysResults.mapToPair(new ExtractFieldAsKey(
								"playerId"))).mapToPair(new AddPlayerData());

				List<BSONObject> segmentResults = analysis
						.analyzeSegments(gameplaysResults);
				// Write to mongo segment results
				DBCollection collection = db.getCollection("segments_"
						+ versionId);
				collection.drop();

				for (BSONObject segmentResult : segmentResults) {
					collection.insert(new BasicDBObject(segmentResult.toMap()));
				}

				// Store calculated data
				if (calculatedData.containsField("_id")) {
					calculatedData.removeField("_id");
				}

				db.getCollection("calculated_data").update(
						new BasicDBObject("versionId", versionData.get("_id")),
						calculatedData, true, false);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				setVersionLoading(false);
			}
		}
	}

	public void setVersionLoading(boolean loading) {
		db.getCollection("versions").update(
				new BasicDBObject("_id", new ObjectId(versionId)),
				new BasicDBObject("$set", new BasicDBObject("analyzing",
						loading)));
	}
}
