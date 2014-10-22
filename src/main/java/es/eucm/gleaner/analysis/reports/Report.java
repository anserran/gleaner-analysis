package es.eucm.gleaner.analysis.reports;

import org.bson.BSONObject;
import scala.Tuple2;

import java.util.List;

public interface Report {

	/**
	 * Reads data for the report.
	 *
	 * @return if the data is valid to build the report
	 */
	boolean readData(String id, BSONObject report, BSONObject versionData);

	List<Tuple2<String, String>> getVariables();

	BSONObject getReducer();
}
