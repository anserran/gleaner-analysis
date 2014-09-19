package es.eucm.gleaner.analysis.reducers;

import es.eucm.gleaner.analysis.utils.Q;
import org.bson.BSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ListReducer implements Reducer {

	private String field;

	public ListReducer(String field) {
		this.field = field;
	}

	@Override
	public void zero(BSONObject result) {
		result.put(field, Arrays.asList());
	}

	@Override
	public void one(BSONObject gameplayResult, BSONObject result) {
		Object value = Q.get(field, gameplayResult);
		ArrayList list = new ArrayList();
		list.add(value);
		result.put(field, list);
	}

	@Override
	public void aggregate(BSONObject v1, BSONObject v2, BSONObject result) {
		List<Object> list1 = (List<Object>) v1.get("list_" + field);
		List<Object> list2 = (List<Object>) v2.get("list_" + field);
		list1.addAll(list2);
		result.put(field, list1);
	}

    @Override
    public void extraOperations(BSONObject segmentResult) {

    }
}
