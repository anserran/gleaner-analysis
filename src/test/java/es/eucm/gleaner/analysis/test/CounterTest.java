package es.eucm.gleaner.analysis.test;

import es.eucm.gleaner.analysis.GameplayResultAssert.ResultAsserter;
import es.eucm.gleaner.analysis.GameplayResultAssert.SegmentAsserter;
import es.eucm.gleaner.analysis.Q;
import es.eucm.gleaner.analysis.Trace.ZoneTrace;
import es.eucm.gleaner.analysis.VersionData;
import es.eucm.gleaner.analysis.analysis.mappers.Count;
import org.bson.BSONObject;
import org.bson.BasicBSONObject;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CounterTest extends AnalysisTest {

	private CounterAsserter asserter = new CounterAsserter();

	@Override
	protected BSONObject buildVersionData() {
		VersionData versionData = new VersionData();
		versionData.putVar("completed", "reach('Result')");
        versionData.putVar("all", "true");

		BasicBSONObject counter = new BasicBSONObject();
		counter.put("variable", "completed");
		counter.put("value", true);

		versionData.addReport("counter", counter);

        BasicBSONObject counter2 = new BasicBSONObject();
        counter2.put("variable", "all");
        counter2.put("value", true);

        versionData.addReport("counter", counter);
        versionData.addReport("counter", counter2);
		return versionData;
	}

	@Override
	protected List<BSONObject> buildTraces() {
		return Arrays.<BSONObject> asList(new ZoneTrace("1", "Cover"),
				new ZoneTrace("1", "Result"), new ZoneTrace("2", "Cover"),
				new ZoneTrace("3", "Cover"), new ZoneTrace("4", "Cover"),
				new ZoneTrace("4", "Result"));
	}

	@Override
	protected ResultAsserter buildResultAsserter() {
		return asserter;
	}

	@Override
	protected SegmentAsserter buildSegmentAsserter() {
		return asserter;
	}

	public static class CounterAsserter implements ResultAsserter,
			SegmentAsserter {

		@Override
		public void assertResult(String gameplayId, BSONObject gameplayResult) {
			Boolean value = Q.get("completed", gameplayResult);
			if ("1".equals(gameplayId) || "4".equals(gameplayId)) {
				assertTrue(value);
			} else {
				assertFalse(value);
			}
            assertTrue(Q.<Boolean>get("all", gameplayResult));
		}

		@Override
		public void assertSegment(BSONObject segmentResult) {
            assertEquals(Q.get(Count.COUNT + "completedtrue", segmentResult), 2);
            assertEquals(Q.get(Count.COUNT + "alltrue", segmentResult), 4);
		}
	}

}
