package es.eucm.gleaner.analysis.derivedvarsfunctions;

import es.eucm.gleaner.analysis.VersionData;
import org.bson.BSONObject;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ReachTest extends DerivedVarFunctionTest {

	@Override
	protected BSONObject buildVersionData() {
		VersionData versionData = new VersionData();
		versionData.putVar("started", "reach('start')");
		versionData.putVar("finished", "reach('start') && reach('finish')");
		return versionData;
	}

	@Override
	protected List<BSONObject> buildTraces() {
		return Arrays.<BSONObject> asList(new ZoneTrace("1", "start"),
				new ZoneTrace("1", "finish"), new ZoneTrace("2", "start"));
	}

	@Override
	protected ResultAsserter buildResultAsserter() {
		return new ReachAsserter();
	}

	public static class ReachAsserter implements ResultAsserter {

		@Override
		public void assertResult(String gameplayId, BSONObject gameplayResult) {
			if ("1".equals(gameplayId)) {
				assertTrue((Boolean) gameplayResult.get("started"));
				assertTrue((Boolean) gameplayResult.get("finished"));
			} else if ("2".equals(gameplayId)) {
				assertTrue((Boolean) gameplayResult.get("started"));
				assertFalse((Boolean) gameplayResult.get("finished"));
			}
		}
	}
}
