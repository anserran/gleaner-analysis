package es.eucm.gleaner.analysis;

import es.eucm.gleaner.analysis.utils.GameplayResultAssert;
import es.eucm.gleaner.analysis.utils.GameplayResultAssert.ResultAsserter;
import es.eucm.gleaner.analysis.utils.GameplayResultAssert.SegmentAsserter;
import es.eucm.gleaner.analysis.utils.Q;
import es.eucm.gleaner.analysis.utils.Split;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.PairFunction;
import org.bson.BSONObject;
import org.bson.BasicBSONObject;
import org.junit.Test;
import scala.Tuple2;

import java.util.List;

public abstract class AnalysisTest {

	protected abstract BSONObject buildVersionData();

	protected abstract List<BSONObject> buildTraces();

	protected abstract ResultAsserter buildResultAsserter();

	protected abstract SegmentAsserter buildSegmentAsserter();

	protected abstract PlayerSetter buildPlayerSetter();

	protected void extraTest() {

	}

	@Test
	public void test() {
		JavaSparkContext sparkContext = new JavaSparkContext(new SparkConf()
				.setMaster("local").setAppName("test"));
		JavaPairRDD<Object, BSONObject> traces = sparkContext.parallelize(
				buildTraces()).mapToPair(new Split());
		BSONObject versionData = buildVersionData();
		Analysis analysis = new Analysis();
		JavaPairRDD<Object, BSONObject> gameplaysResults = analysis
				.analyzeGameplays(traces, versionData, new BasicBSONObject())
				.mapToPair(new PlayerSetterFunction(buildPlayerSetter()));

		gameplaysResults
				.foreach(new GameplayResultAssert(buildResultAsserter()));

		List<BSONObject> segmentsResults = analysis
				.analyzeSegments(gameplaysResults);

		SegmentAsserter segmentAsserter = buildSegmentAsserter();
		if (segmentAsserter != null) {
			for (BSONObject segmentResult : segmentsResults) {
				segmentAsserter.assertSegment(
						Q.<String> get("_name", segmentResult),
						segmentResult);
			}
		}
		extraTest();
	}

	public static class PlayerSetterFunction implements
			PairFunction<Tuple2<Object, BSONObject>, Object, BSONObject> {

		private PlayerSetter playerSetter;

		public PlayerSetterFunction(PlayerSetter playerSetter) {
			this.playerSetter = playerSetter;
		}

		@Override
		public Tuple2<Object, BSONObject> call(Tuple2<Object, BSONObject> tuple2) {
			if (playerSetter == null) {
				return new Tuple2<Object, BSONObject>("player", tuple2._2);
			} else {
				return new Tuple2<Object, BSONObject>(
						playerSetter.getPlayer((String) tuple2._2
								.get("gameplayId")), tuple2._2);
			}
		}
	}

	public static interface PlayerSetter {
		String getPlayer(String gameplayId);
	}
}
