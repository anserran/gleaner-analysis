package es.eucm.gleaner.analysis.reports;

import es.eucm.gleaner.analysis.utils.Q;
import org.bson.BSONObject;
import org.bson.BasicBSONObject;
import scala.Tuple2;

import java.util.Arrays;
import java.util.List;

public class Counter implements Report {

	private String id;

	private String condition;

	@Override
	public boolean readData(String id, BSONObject report) {
		this.id = id;
		this.condition = Q.get("condition", report);
		return condition != null;
	}

	@Override
	public List<Tuple2<String, String>> getVariables() {
		return Arrays.asList(new Tuple2<String, String>(id, condition
				+ " ? 1 : 0"));
	}

	@Override
	public BSONObject getReducer() {
		return new BasicBSONObject("type", "sum");
	}
}
