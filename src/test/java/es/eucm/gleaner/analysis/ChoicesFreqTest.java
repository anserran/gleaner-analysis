package es.eucm.gleaner.analysis;

import es.eucm.gleaner.analysis.traces.SelectedOptions;
import es.eucm.gleaner.analysis.utils.GameplayResultAssert.ResultAsserter;
import es.eucm.gleaner.analysis.utils.GameplayResultAssert.SegmentAsserter;
import es.eucm.gleaner.analysis.utils.Q;
import es.eucm.gleaner.analysis.utils.Trace.ChoiceTrace;
import es.eucm.gleaner.analysis.utils.VersionData;
import org.bson.BSONObject;
import org.bson.BasicBSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ChoicesFreqTest extends AnalysisTest {

	private ChoicesAsserter asserter = new ChoicesAsserter();

	@Override
	protected BSONObject buildVersionData() {
		VersionData versionData = new VersionData();
		versionData.addChoice("choice1", "option1", "option2");
		versionData.addChoice("choice2", "z1", "z2");

		BasicBSONObject report = new BasicBSONObject("choiceIds",
				Arrays.asList("choice1", "choice2"));
		report.put("id", "choices");
		versionData.addReport("choices", report);
		return versionData;
	}

	@Override
	protected List<BSONObject> buildTraces() {
		return Arrays.<BSONObject> asList(new ChoiceTrace("1", "choice1",
				"option1"), new ChoiceTrace("1", "choice1", "option1"),
				new ChoiceTrace("1", "choice2", "z1"), new ChoiceTrace("2",
						"choice1", "option2"), new ChoiceTrace("2", "choice2",
						"z2"), new ChoiceTrace("3", "choice1", "option1"),
				new ChoiceTrace("3", "choice2", "z2"));
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

	public static class ChoicesAsserter implements ResultAsserter,
			SegmentAsserter {

		private ArrayList<String> segmentsExpected = new ArrayList<String>();

		public ChoicesAsserter() {
			segmentsExpected.add("all");
		}

		@Override
		public void assertResult(String gameplayId, BSONObject gameplayResult) {
			BSONObject choices = Q.get(SelectedOptions.PREFIX + "choices",
					gameplayResult);
			BSONObject choice1 = Q.get("choice1", choices);
			BSONObject choice2 = Q.get("choice2", choices);
			if ("1".equals(gameplayId)) {
				assertEquals(2, Q.getInt("option1", choice1));
				assertFalse(choice1.containsField("option2"));

				assertEquals(1, Q.getInt("z1", choice2));
				assertFalse(choice2.containsField("z2"));
			} else if ("2".equals(gameplayId)) {
				assertEquals(1, Q.getInt("option2", choice1));
				assertFalse(choice1.containsField("option1"));

				assertEquals(1, Q.getInt("z2", choice2));
				assertFalse(choice2.containsField("z1"));
			} else if ("3".equals(gameplayId)) {
				assertEquals(1, Q.getInt("option1", choice1));
				assertFalse(choice1.containsField("option2"));

				assertEquals(1, Q.getInt("z2", choice2));
				assertFalse(choice2.containsField("z1"));
			}
		}

		@Override
		public void assertSegment(String segmentName, BSONObject segmentResult) {
			if ("all".equals(segmentName)) {
				BSONObject choices = Q.get("choices", segmentResult);

				BSONObject choice1 = Q.get("choice1", choices);
				assertEquals(3, Q.getInt("option1", choice1));
				assertEquals(1, Q.getInt("option2", choice1));

				BSONObject choice2 = Q.get("choice2", choices);
				assertEquals(1, Q.getInt("z1", choice2));
				assertEquals(2, Q.getInt("z2", choice2));
			}
			assertTrue(segmentsExpected.remove(segmentName));
		}
	}

	@Override
	protected void extraTest() {
		assertTrue(asserter.segmentsExpected.isEmpty());
	}
}
