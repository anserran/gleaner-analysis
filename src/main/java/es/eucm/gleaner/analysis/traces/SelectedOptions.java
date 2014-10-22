package es.eucm.gleaner.analysis.traces;

import es.eucm.gleaner.analysis.utils.Q;
import org.bson.BSONObject;
import org.bson.BasicBSONObject;

import java.util.List;

public class SelectedOptions implements TraceAnalyzer {

    public static final String PREFIX = "_choices_";

	private String id;

	private List<String> choiceIds;

	public SelectedOptions(String id, List<String> choiceIds) {
		this.id = id;
		this.choiceIds = choiceIds;
	}

	@Override
	public void defaultValues(BSONObject gameplayResult) {
		gameplayResult.put(PREFIX + id, new BasicBSONObject());
	}

	@Override
	public boolean interestedIn(String event) {
		return CHOICE.equals(event);
	}

	@Override
	public void analyze(BSONObject trace, BSONObject gameplayResult) {
		String choiceId = Q.getTarget(trace);
		if (choiceIds.contains(choiceId)) {
			String selectedOption = Q.getValue(trace);

			BSONObject choices = Q.get(PREFIX + id, gameplayResult);
			BSONObject options = Q.getOrSet(choiceId, choices,
					BasicBSONObject.class);
			Integer count = Q.getOrSet(selectedOption, options, 0);
			options.put(selectedOption, count + 1);
		}
	}
}
