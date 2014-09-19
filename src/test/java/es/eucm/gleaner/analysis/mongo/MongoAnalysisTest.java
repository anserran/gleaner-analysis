package es.eucm.gleaner.analysis.mongo;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import es.eucm.gleaner.analysis.utils.Q;
import es.eucm.gleaner.analysis.utils.Trace.ZoneTrace;
import es.eucm.gleaner.analysis.utils.VersionData;
import org.apache.spark.SparkConf;
import org.bson.BSONObject;
import org.bson.BasicBSONObject;
import org.bson.types.ObjectId;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MongoAnalysisTest {

	private static final int PLAYERS = 50;

	private DB db;

	private MongoAnalysis mongoAnalysis;

	private ArrayList<DBObject> players = new ArrayList<DBObject>();

	private ArrayList<DBObject> gameplays = new ArrayList<DBObject>();

	private String versionId;

	@Before
	public void setUp() {
		db = Q.getDB("localhost", 27017, "gleaner_analysis_test");
		db.dropDatabase();
		DBObject version = createVersion();
		db.getCollection("versions").insert(version);
		versionId = version.get("_id").toString();
		addPlayers();
		addGameplays();
		addTraces();

		mongoAnalysis = new MongoAnalysis(versionId, "localhost", 27017,
				"gleaner_analysis_test");
	}

	private VersionData createVersion() {
		VersionData versionData = new VersionData();
		versionData.putVar("started", "reach('Start')");

		BasicBSONObject counter = new BasicBSONObject();
		counter.put("id", "count_started");
		counter.put("condition", "started === true");

		versionData.addReport("counter", counter);

		return versionData;
	}

	private void addPlayers() {
		for (int i = 0; i < PLAYERS; i++) {
			DBObject player = new BasicDBObject("name", i);
			players.add(player);
		}
		db.getCollection("players").insert(players);
	}

	private void addGameplays() {
		for (DBObject player : players) {
			gameplays.add(new BasicDBObject("playerId", player.get("_id")));
		}
		db.getCollection("gameplays_" + versionId).insert(gameplays);
	}

	private void addTraces() {
		ArrayList<DBObject> traces = new ArrayList<DBObject>();
		int i = 0;
		for (DBObject gameplay : gameplays) {
			traces.add(new ZoneTrace(gameplay.get("_id"), "Start"));
			if (i % 2 == 0) {
				traces.add(new ZoneTrace(gameplay.get("_id"), "End"));
			}
			i++;
		}
		db.getCollection("traces_" + versionId).insert(traces);
	}

	@Test
	public void test() {
		SparkConf conf = new SparkConf().setAppName("mongotest").setMaster(
				"local");
		mongoAnalysis.execute(conf);
		asserts();

		// Update version data
		VersionData data = createVersion();
		data.putVar("completed", "reach('End')");

		BasicBSONObject counter = new BasicBSONObject();
		counter.put("id", "count_completed");
		counter.put("condition", "completed === true");

		data.addReport("counter", counter);

		db.getCollection("versions").update(
				new BasicDBObject("_id", new ObjectId(versionId)),
				new BasicDBObject("$set", data));

		mongoAnalysis.execute(conf);
		DBObject segmentResult = asserts();
		assertEquals((long) PLAYERS / 2,
				Q.getLong("count_completed", segmentResult));

		DBObject calculatedData = db.getCollection("calculated_data").findOne();
		assertEquals(Q.<List<String>> get("functions", calculatedData).get(1),
				"reach('End')");
		BSONObject variables = Q.get("variables", calculatedData);
		assertTrue(variables.containsField("completed"));
		assertTrue(variables.containsField("count_completed"));
	}

	private DBObject asserts() {
		DBCollection gameplaysResults = db.getCollection("gameplaysresults_"
				+ versionId);
		assertEquals(PLAYERS, gameplaysResults.count());
		assertEquals(PLAYERS,
				gameplaysResults.find(new BasicDBObject("started", true))
						.count());

		DBCollection segmentsResults = db
				.getCollection("segments_" + versionId);
		assertEquals(1, segmentsResults.count());
		DBObject segmentResult = segmentsResults.findOne();

		assertEquals((long) PLAYERS, Q.getLong("count_started", segmentResult));

		DBObject calculatedData = db.getCollection("calculated_data").findOne();
		assertEquals(Q.<List<String>> get("functions", calculatedData).get(0),
				"reach('Start')");
		BSONObject variables = Q.get("variables", calculatedData);
		assertTrue(variables.containsField("started"));
		assertTrue(variables.containsField("count_started"));
		return segmentResult;
	}

	@After
	public void tearDown() {
		db.dropDatabase();
	}

}
