package es.eucm.gleaner.analysis;

import com.mongodb.BasicDBObject;
import org.bson.BSONObject;

import java.util.ArrayList;
import java.util.List;

public class VersionData extends BasicDBObject {

	private List<BSONObject> derivedVars;

	private List<BSONObject> reports;

	public VersionData() {
		put("derivedVars", derivedVars = new ArrayList<BSONObject>());

		BasicDBObject panel = new BasicDBObject("reports",
				reports = new ArrayList<BSONObject>());
		ArrayList<BasicDBObject> panels = new ArrayList<BasicDBObject>();
		panels.add(panel);
		put("panels", panels);
	}

	public void putVar(String name, String expression) {
		BasicDBObject var = new BasicDBObject("name", name);
		var.put("value", expression);
		derivedVars.add(var);
	}

	public void forceCalculate(String... vars) {
		BasicDBObject mockReport = new BasicDBObject("type", "mock");
		mockReport.put("vars", vars);
		reports.add(mockReport);
	}

    public void addReport(String type, BSONObject data){
        data.put("type", type);
        reports.add(data);
    }
}
