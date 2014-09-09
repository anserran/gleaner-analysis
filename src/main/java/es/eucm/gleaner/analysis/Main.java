package es.eucm.gleaner.analysis;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.apache.spark.api.java.JavaSparkContext;
import org.bson.BSONObject;

import java.net.UnknownHostException;

public class Main {
	public static void main(final String[] args) throws UnknownHostException {
		JavaSparkContext sc = new JavaSparkContext();
        String versionId = args[0];
        new GameplaysAnalysis(sc, versionId);

		/*JavaPairRDD<Object, BSONObject> statements = gameplaysResults
				.flatMapToPair(new PairFlatMapFunction<Tuple2<Object, BSONObject>, Object, BSONObject>() {
					@Override
					public Iterable<Tuple2<Object, BSONObject>> call(
							Tuple2<Object, BSONObject> tuple) throws Exception {
						if (Boolean.TRUE.equals(tuple._2.get("completed"))) {
							BSONObject statement = new BasicDBObject();

							BSONObject verb = new BasicDBObject();
							verb.put("id",
									"http://adlnet.gov/expapi/verbs/completed");
							verb.put("display", new BasicDBObject("en-US",
									"completed"));
							statement.put("actor", new BasicDBObject("mbox",
									tuple._2.get("gameplayId")));
							statement.put("verb", verb);

							BSONObject object = new BasicDBObject("id", args[0]);
							object.put("definition", new BasicDBObject("name",
									versionData.get("name")));
							statement.put("object", object);
							return Arrays
									.asList(new Tuple2<Object, BSONObject>(
											null, statement));
						}
						return Arrays.asList();
					}
				});

		config.set("mongo.output.uri",
				"mongodb://127.0.0.1:27017/gleaner.statements");
		statements.saveAsNewAPIHadoopFile("file:///bogus", Object.class,
				Object.class, MongoOutputFormat.class, config);*/

	}

	private static String validVarName(String var) {
		var = var.replace('.', '-');
		var = var.replace('$', '-');
		return var;
	}

	private static void addHistograms(BasicDBObject result, DBObject v) {
		for (String key : v.keySet()) {
			if (key.startsWith("v_")) {
				BasicDBObject histogram = (BasicDBObject) v.get(key);
				BasicDBObject resultHistogram;
				if (result.containsField(key)) {
					resultHistogram = (BasicDBObject) result.get(key);
				} else {
					resultHistogram = new BasicDBObject();
					result.put(key, resultHistogram);
				}

				for (String hkey : histogram.keySet()) {
					Integer count = histogram.getInt(hkey);
					Integer oldcount = resultHistogram.containsField(hkey) ? resultHistogram
							.getInt(hkey) : 0;
					resultHistogram.put(hkey, count + oldcount);
				}

			}
		}
	}

	private static void createHistograms(BSONObject v1, BasicDBObject result) {
		for (String key : v1.keySet()) {
			if (key.startsWith("v_")) {
				BasicDBObject histogram = new BasicDBObject();
				histogram.put(v1.get(key).toString(), 1);
				result.put(key, histogram);
			}
		}
	}
}
