package es.eucm.gleaner.analysis.reports;

import es.eucm.gleaner.analysis.reducers.Aggregate2;
import es.eucm.gleaner.analysis.utils.Q;
import org.bson.BSONObject;
import org.bson.BasicBSONObject;
import scala.Tuple2;

import java.util.Arrays;
import java.util.List;

/**
 * Shows frequency of selected options for each of the choices
 */
public class ChoicesFrequency implements Report {

	private String id;

	private List<String> choiceIds;

	@Override
	public boolean readData(String id, BSONObject report, BSONObject versionData) {
		this.id = id;
		try {
			choiceIds = Q.get("choiceIds", report);
		} catch (ClassCastException e) {
			e.printStackTrace();
		}
		return choiceIds != null && !choiceIds.isEmpty();
	}

	@Override
	public List<Tuple2<String, String>> getVariables() {
		String idsList = "";
		for (String id : choiceIds) {
			idsList += ",'" + id + "'";
		}
		return Arrays.asList(new Tuple2<String, String>(id, "choices('" + id
				+ "'" + idsList + ")"));
	}

	@Override
	public BSONObject getReducer() {
		return new BasicBSONObject("type", Aggregate2.ID);
	}
}
