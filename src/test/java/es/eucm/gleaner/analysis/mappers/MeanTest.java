package es.eucm.gleaner.analysis.mappers;

import es.eucm.gleaner.analysis.VersionData;
import org.bson.BSONObject;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class MeanTest extends MapperTest {
	@Override
	protected BSONObject buildVersionData() {
		VersionData versionData = new VersionData();
		versionData.putVar("var", "", "mean");
		return versionData;
	}

	@Override
	protected List<BSONObject> buildGameplaysResults() {
		return Arrays.<BSONObject> asList(new GameplayResult("var", 10),
                new GameplayResult("var", 20), new GameplayResult("var", 30));
	}

	@Override
	protected void assertResult(BSONObject result) {
		assertEquals(((Number) result.get("mean_var")).intValue(), 20);
	}
}
