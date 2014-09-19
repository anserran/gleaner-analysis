package es.eucm.gleaner.analysis;

import es.eucm.gleaner.analysis.utils.GameplayResultAssert.ResultAsserter;
import es.eucm.gleaner.analysis.utils.GameplayResultAssert.SegmentAsserter;
import es.eucm.gleaner.analysis.utils.Q;
import es.eucm.gleaner.analysis.utils.Trace.VarTrace;
import es.eucm.gleaner.analysis.utils.Trace.ZoneTrace;
import es.eucm.gleaner.analysis.utils.VersionData;
import org.bson.BSONObject;
import org.bson.BasicBSONObject;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class GroupByPlayerTest extends AnalysisTest {

	private GroupByAsserter asserter = new GroupByAsserter();

	@Override
	protected BSONObject buildVersionData() {
		VersionData versionData = new VersionData();
		versionData.putVar("completed", "reach('end')");
        versionData.putVar("score", "doubleValue('score')");
		versionData.putSegment("segment1", "completed === true", "first");
		versionData.putSegment("segment2", "completed === true", "last");

        BasicBSONObject counter = new BasicBSONObject();
        counter.put("id", "count_all");
        counter.put("condition", "true");
        versionData.addReport("counter", counter);

		return versionData;
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
				assertEquals(2, Q.getLong("count_all", segmentResult));
			} else if ("segment1".equals(segmentName)) {
                assertEquals(1, Q.getLong("count_all", segmentResult));
			} else if ("segment2".equals(segmentName)) {
                assertEquals(1, Q.getLong("count_all", segmentResult));
			}
		}
	}
}
