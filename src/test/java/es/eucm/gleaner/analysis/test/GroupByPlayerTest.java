package es.eucm.gleaner.analysis.test;

import es.eucm.gleaner.analysis.GameplayResultAssert.ResultAsserter;
import es.eucm.gleaner.analysis.GameplayResultAssert.SegmentAsserter;
import es.eucm.gleaner.analysis.Q;
import es.eucm.gleaner.analysis.Trace.VarTrace;
import es.eucm.gleaner.analysis.Trace.ZoneTrace;
import es.eucm.gleaner.analysis.VersionData;
import es.eucm.gleaner.analysis.analysis.GameplaysAnalysis;
import es.eucm.gleaner.analysis.analysis.mappers.Sum;
import org.bson.BSONObject;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class GroupByPlayerTest extends AnalysisTest {

	private GroupByAsserter asserter = new GroupByAsserter();

	@Override
	protected BSONObject buildVersionData() {
		VersionData versionData = new VersionData();
		versionData.putVar("completed", "reach('end')");
		versionData.putVar("score", "double('score')");
		versionData.addSegment("segment1", "true", "first",
				"completed === true");
		versionData
				.addSegment("segment2", "true", "last", "completed === true");
		versionData.forceCalculate("score", "completed");
		return versionData;
	}

	@Override
	protected void addExtra(GameplaysAnalysis gameplaysAnalysis) {
		gameplaysAnalysis.getMapReducers().addMapper(new Sum("score"));
	}

	@Override
	protected List<BSONObject> buildTraces() {
		return Arrays.<BSONObject> asList(new ZoneTrace("1", "start"),
				new VarTrace("1", "score", 100), new ZoneTrace("1", "end"),
				new ZoneTrace("2", "start"), new VarTrace("2", "score", 50),
				new ZoneTrace("2", "end"));
	}

	@Override
	protected ResultAsserter buildResultAsserter() {
		return asserter;
	}

	@Override
	protected SegmentAsserter buildSegmentAsserter() {
		return asserter;
	}

	@Override
	protected PlayerSetter buildPlayerSetter() {
		return asserter;
	}

	public static class GroupByAsserter implements PlayerSetter,
			ResultAsserter, SegmentAsserter {

		@Override
		public String getPlayer(String gameplayId) {
			if ("1".equals(gameplayId) || "2".equals(gameplayId)) {
				return "player1";
			} else {
				return "player2";
			}
		}

		@Override
		public void assertResult(String gameplayId, BSONObject gameplayResult) {
			if ("1".equals(gameplayId)) {
				assertEquals(100.0, Q.get("score", gameplayResult));
			} else if ("2".equals(gameplayId)) {
				assertEquals(50.0, Q.get("score", gameplayResult));
			}
		}

		@Override
		public void assertSegment(String segmentName, BSONObject segmentResult) {
			if ("all".equals(segmentName)) {
				assertEquals(150.0, Q.get("sum_score", segmentResult));
			} else if ("segment1".equals(segmentName)) {
				assertEquals(50.0, Q.get("sum_score", segmentResult));
			} else if ("segment2".equals(segmentName)) {
				assertEquals(100.0, Q.get("sum_score", segmentResult));
			}
		}
	}
}
