package es.eucm.gleaner.analysis.utils;

import es.eucm.gleaner.analysis.AnalysisTest;
import es.eucm.gleaner.analysis.utils.GameplayResultAssert.ResultAsserter;
import es.eucm.gleaner.analysis.utils.GameplayResultAssert.SegmentAsserter;
import org.bson.BSONObject;
import org.bson.BasicBSONObject;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class ZeroAnalysisTest extends AnalysisTest {

	private ZeroAsserter asserter = new ZeroAsserter();

	@Override
	protected BSONObject buildVersionData() {
		VersionData versionData = new VersionData();
		versionData.putVar("completed", "reach('Inicio')");

		BasicBSONObject counter = new BasicBSONObject();
		counter.put("id", "count_completed");
		counter.put("condition", "completed === true");

		versionData.addReport("counter", counter);

		return versionData;
	}

	@Override
	protected List<BSONObject> buildTraces() {
		return Arrays.asList();
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
		return null;
	}

	public static class ZeroAsserter implements ResultAsserter, SegmentAsserter {

		@Override
		public void assertResult(String gameplayId, BSONObject gameplayResult) {
			fail("This should never be called");
		}

		@Override
		public void assertSegment(String segmentName, BSONObject segmentResult) {
			assertEquals(Q.getLong("count_completed", segmentResult), 0);
		}
	}
}
