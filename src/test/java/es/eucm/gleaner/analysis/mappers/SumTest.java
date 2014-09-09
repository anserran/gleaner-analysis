package es.eucm.gleaner.analysis.mappers;

import es.eucm.gleaner.analysis.VersionData;
import org.bson.BSONObject;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class SumTest extends MapperTest {
	@Override
	protected BSONObject buildVersionData() {
		VersionData versionData = new VersionData();
		versionData.putVar("var", "", "sum");
		return versionData;
	}

	@Override
	protected List<BSONObject> buildGameplaysResults() {
		return Arrays.<BSONObject> asList(new GameplayResult("var", 15),
				new GameplayResult("var", 25), new GameplayResult("var", 100));
	}

	@Override
	protected void assertResult(BSONObject result) {
		assertEquals(((Number) result.get("sum_var")).intValue(), 140);
	}
}
