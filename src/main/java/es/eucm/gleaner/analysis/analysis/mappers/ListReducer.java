package es.eucm.gleaner.analysis.analysis.mappers;

import es.eucm.gleaner.analysis.Q;
import org.bson.BSONObject;

import java.util.ArrayList;
import java.util.List;

public class ListReducer implements MapReducer {

	private String field;

	public ListReducer(String field) {
		this.field = field;
	}

	@Override
	public void one(BSONObject gameplayResult, BSONObject result) {
		Object value = Q.get(field, gameplayResult);
		ArrayList list = new ArrayList();
		list.add(value);
		result.put("list_" + field, list);
	}

	@Override
	public void aggregate(BSONObject v1, BSONObject v2, BSONObject result) {
		List<Object> list1 = (List<Object>) v1.get("list_" + field);
		List<Object> list2 = (List<Object>) v2.get("list_" + field);
		list1.addAll(list2);
		result.put("list_" + field, list1);
	}
}
