package es.eucm.gleaner.analysis.derivedvarsfunctions;

import es.eucm.gleaner.analysis.VersionData;
import org.bson.BSONObject;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class MsTest extends DerivedVarFunctionTest {

	@Override
	protected BSONObject buildVersionData() {
		VersionData versionData = new VersionData();
		versionData.putVar("ms", "ms('start')");
		versionData.putVar("seconds", "ms('start') / 1000");
        versionData.putVar("ms2", "ms('start') + ms('start')");
		return versionData;
	}

	@Override
	protected List<BSONObject> buildTraces() {
		return Arrays.<BSONObject> asList(new ZoneTrace("1", "start").time(0),
				new ZoneTrace("1", "finish").time(2000), new ZoneTrace("1",
						"start").time(4000), new ZoneTrace("1", "finish")
						.time(7000));
	}

	@Override
	protected ResultAsserter buildResultAsserter() {
		return new ReachAsserter();
	}

	public static class ReachAsserter implements ResultAsserter {

		@Override
		public void assertResult(String gameplayId, BSONObject gameplayResult) {
			assertEquals(((Number) gameplayResult.get("ms")).longValue(), 5000);
            assertEquals(((Number) gameplayResult.get("ms2")).longValue(), 10000);
			assertEquals(((Number) gameplayResult.get("seconds")).longValue(), 5);
		}
	}
}
