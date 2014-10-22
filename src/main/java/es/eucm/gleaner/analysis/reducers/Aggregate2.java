package es.eucm.gleaner.analysis.reducers;

import org.bson.BSONObject;
import org.bson.BasicBSONObject;

import es.eucm.gleaner.analysis.utils.Q;

/**
 * Aggregates objects in primary keys in objects in the form:
 *
 * <code>
 * {
 *   "primary_key1": {
 *      "a": int_value1, 
 *      "b": int_value2
 *   }, 
 *   "primary_key2": {
 *      "c": int_value3, 
 *      "d": int_value4 
 *   },
 *    ...
 * }
 * </code>
 *
 * E.g.:
 * <code>
 *     // Object 1
 *     {
 *     "a":
 *          {"u": 1, "v": 2 }
 *     "b":
 *          {"w": 5, "x": 3 }
 *     }
 *     // Object 2
 *     {
 *     "a":
 *          {"u": 2 }
 *     "b":
 *          {"w": 9, "z": 3 }
 *     "c":
 *          {"y": 5, "z": 3 }
 *     }
 *     // Result after aggregation
 *     {
 *     "a":
 *          {"u": 3, "v": 2 }
 *     "b":
 *          {"w": 14, "x": 3, "z": 3 }
 *     "c":
 *          {"y": 5, "z": 3 }
 *     }
 * </code>
 *
 */
public class Aggregate2 implements Reducer {

    public static final String ID = "aggregate2";

	private String field;

	public Aggregate2(String field) {
		this.field = field;
	}

	@Override
	public void zero(BSONObject result) {
		result.put(field, new BasicBSONObject());
	}

	@Override
	public void one(BSONObject gameplayResult, BSONObject result) {
		result.put(field, Q.get(field, gameplayResult));
	}

	@Override
	public void aggregate(BSONObject v1, BSONObject v2, BSONObject result) {
		BSONObject choices1 = Q.get(field, v1);
		BSONObject choices2 = Q.get(field, v2);

		for (String choiceId : choices2.keySet()) {
			if (choices1.containsField(choiceId)) {
				BSONObject choice1 = Q.get(choiceId, choices1);
				BSONObject choice2 = Q.get(choiceId, choices2);
				for (String selectedOption : choice2.keySet()) {
					Integer value = Q.get(selectedOption, choice2);
					if (choice1.containsField(selectedOption)) {
						value += (Integer) Q.get(selectedOption, choice1);
					}
					choice1.put(selectedOption, value);
				}
			} else {
				choices1.put(choiceId, Q.get(choiceId, choices2));
			}
		}
		result.put(field, choices1);
	}

	@Override
	public void extraOperations(BSONObject segmentResult) {

	}
}
