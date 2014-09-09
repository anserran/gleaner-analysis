package es.eucm.gleaner.analysis.analysis.groupoperations;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.bson.BSONObject;

import java.util.List;

public class BoxPlotOperation implements GroupOperation {

	private String field;

	public BoxPlotOperation(String field) {
		this.field = field;
	}

	@Override
	public void operate(BSONObject groupResult) {
		DescriptiveStatistics statistics = new DescriptiveStatistics();
		List<Object> values = (List<Object>) groupResult.get("list_" + field);
		for (Object o : values) {
			if (o instanceof Number) {
				statistics.addValue(((Number) o).doubleValue());
			}
		}

		groupResult.put("min_" + field, statistics.getMin());
        groupResult.put("max_" + field, statistics.getMax());
        groupResult.put("q1_" + field, statistics.getPercentile(25));
        groupResult.put("q2_" + field, statistics.getPercentile(50));
        groupResult.put("q3_" + field, statistics.getPercentile(75));
	}
}
