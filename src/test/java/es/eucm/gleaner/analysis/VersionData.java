package es.eucm.gleaner.analysis;

import com.mongodb.BasicDBObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VersionData extends BasicDBObject {

	private List<BasicDBObject> derivedVars;

	public VersionData() {
		put("derivedVars", derivedVars = new ArrayList<BasicDBObject>());
	}

	public void putVar(String name, String expression, String... aggregations) {
		BasicDBObject var = new BasicDBObject("name", name);
		var.put("value", expression);
		var.put("aggregations", Arrays.asList(aggregations));
		derivedVars.add(var);
	}
}
