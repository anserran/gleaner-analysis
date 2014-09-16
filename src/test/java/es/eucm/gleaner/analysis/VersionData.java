package es.eucm.gleaner.analysis;

import com.mongodb.BasicDBObject;
import org.bson.BSONObject;
import org.bson.BasicBSONObject;

import java.util.ArrayList;
import java.util.List;

public class VersionData extends BasicDBObject {

	private List<BSONObject> derivedVars;

	private List<BSONObject> reports;

    private List<BSONObject> segments;

	public VersionData() {
		put("derivedVars", derivedVars = new ArrayList<BSONObject>());

		BasicDBObject panel = new BasicDBObject("reports",
				reports = new ArrayList<BSONObject>());
		ArrayList<BasicDBObject> panels = new ArrayList<BasicDBObject>();
		panels.add(panel);
		put("panels", panels);

        put("segments", segments = new ArrayList<BSONObject>());
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

    public BasicBSONObject addSegment(String name, String condition) {
        BasicBSONObject segment = new BasicBSONObject("name", name);
        segment.put("condition", condition);
        segments.add(segment);
        return segment;
    }

    public BasicBSONObject addSegment(String name, String condition, String operator, String operation) {
        BasicBSONObject segment = addSegment(name, condition);
        BasicBSONObject groupby = new BasicBSONObject("operator", operator);
        groupby.put("operator", operator);
        groupby.put("operation", operation);
        segment.put("groupbyplayer", groupby);
        return segment;
    }
}
