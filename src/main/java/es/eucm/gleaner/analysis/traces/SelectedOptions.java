package es.eucm.gleaner.analysis.traces;

import es.eucm.gleaner.analysis.utils.Q;
import org.bson.BSONObject;
import org.bson.BasicBSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SelectedOptions implements TraceAnalyzer {

	public static final String PREFIX = "_choices_";

	private String id;

	private List<String> choiceIds;

	private Map<String, List<String>> choicesMap;

	public SelectedOptions(List<BSONObject> choices, String id,
			List<String> choiceIds) {
		this.id = id;
		this.choiceIds = choiceIds;
		this.choicesMap = new HashMap<String, List<String>>();
		for (BSONObject choice : choices) {
			String choiceId = Q.get("id", choice);
			if (choiceIds.contains(choiceId)) {
				choicesMap.put(choiceId,
						(List<String>) Q.get("options", choice));
			}
		}
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
			int selectedOptionIndex = choicesMap.get(choiceId).indexOf(
					selectedOption);
			Integer count = Q.getOrSet(selectedOptionIndex + "", options, 0);
			options.put(selectedOptionIndex + "", count + 1);
		}
	}
}
