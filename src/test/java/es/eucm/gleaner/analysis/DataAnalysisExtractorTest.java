package es.eucm.gleaner.analysis;

import es.eucm.gleaner.analysis.utils.Q;
import es.eucm.gleaner.analysis.utils.VersionData;
import org.bson.BSONObject;
import org.bson.BasicBSONObject;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DataAnalysisExtractorTest {

	@Test
	public void test() {
		BSONObject calculatedData = new BasicBSONObject();

		VersionData versionData = new VersionData();
		versionData.putVar("completed",
				"reach('Boss') && double('score') > 1000");
		versionData.putVar("final_score",
				"double('score') + double('extra_points') + completed");

		DataAnalysisExtractor extractor = new DataAnalysisExtractor();
		BSONObject analysisData = extractor
				.extract(versionData, calculatedData);
		assertFunctions(analysisData, "reach('Boss')", "double('score')",
				"double('extra_points')");
		assertVariables(analysisData, "completed", "final_score");



        versionData.putVar("completed",
                "reach('Boss') && double('score') > 2000");

        analysisData = extractor.extract(versionData, calculatedData);
        assertFunctions(analysisData);
        assertVariables(analysisData, "completed", "final_score");

        versionData.putSegment("segment", "reach('Boss') > 15");

        analysisData = extractor.extract(versionData, calculatedData);
        assertFunctions(analysisData);
        assertVariables(analysisData, "_segment_segment");
        assertSegments(analysisData, "segment");

        versionData.putSegment("segment", "reach('Boss') > 15", "first");

        analysisData = extractor.extract(versionData, calculatedData);
        assertFunctions(analysisData);
        assertVariables(analysisData);
        assertSegments(analysisData, "segment");

        analysisData = extractor.extract(versionData, calculatedData);
        assertFunctions(analysisData);
        assertVariables(analysisData);
        assertSegments(analysisData);
	}

    private void assertFunctions(BSONObject analysisData,
			String... assertFunctions) {
		List<String> functions = Q.get("functions", analysisData);
		assertEquals(functions.size(), assertFunctions.length);
		for (String v : assertFunctions) {
			assertTrue(functions.contains(v));
		}
	}

	private void assertVariables(BSONObject analysisData,
			String... assertVariables) {
		BSONObject variables = Q.get("variables", analysisData);
		assertEquals(variables.toMap().size(), assertVariables.length);
		for (String v : assertVariables) {
			assertTrue(variables.containsField(v));
		}
	}

    private void assertSegments(BSONObject analysisData, String...assertSegments) {
        BSONObject segments = Q.get("segments", analysisData);
        assertEquals(segments.toMap().size(), assertSegments.length);
        for (String s : assertSegments) {
            assertTrue(segments.containsField(s));
        }
    }
}
