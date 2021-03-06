package es.eucm.gleaner.analysis.utils;

import com.mongodb.BasicDBObject;
import org.bson.BSONObject;
import org.bson.BasicBSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VersionData extends BasicDBObject {

	private List<BSONObject> derivedVars;

	private List<BSONObject> reports;

	private List<BSONObject> segments;

	private List<BSONObject> choices;

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
		BSONObject var = null;
		for (BSONObject v : derivedVars) {
			if (name.equals(Q.get("name", v))) {
				var = v;
				break;
			}
		}

		if (var == null) {
			var = new BasicDBObject("name", name);
			derivedVars.add(var);
		}
		var.put("value", expression);
	}

	public void addChoice(String choiceId, String... options) {
		if (choices == null) {
			put("choices", choices = new ArrayList<BSONObject>());
		}
		BSONObject choice = new BasicBSONObject("id", choiceId);
		choice.put("options", Arrays.asList(options));
		choices.add(choice);
	}

	public void addReport(String type, BSONObject data) {
		data.put("type", type);
		reports.add(data);
	}

	public BSONObject putSegment(String name, String condition) {
		BSONObject segment = null;
		for (BSONObject s : segments) {
			if (name.equals(Q.get("name", s))) {
				segment = s;
				break;
			}
		}

		if (segment == null) {
			segment = new BasicDBObject("name", name);
			segments.add(segment);
		}
		segment.put("condition", condition);
		return segment;
	}

	public BSONObject putSegment(String name, String condition, String having) {
		BSONObject segment = putSegment(name, condition);
		segment.put("having", having);
		return segment;
	}
}
