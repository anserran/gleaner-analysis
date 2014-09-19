package es.eucm.gleaner.analysis;

import es.eucm.gleaner.analysis.utils.SegmentFilter;
import es.eucm.gleaner.analysis.utils.SelectFromList;
import es.eucm.gleaner.analysis.utils.ExtractFieldAsKey;
import es.eucm.gleaner.analysis.utils.Q;
import org.apache.spark.api.java.JavaPairRDD;
import org.bson.BSONObject;
import org.bson.BasicBSONObject;

import java.util.ArrayList;
import java.util.List;

public class Analysis {

	private DataAnalysisExtractor dataAnalysisExtractor = new DataAnalysisExtractor();

	private BSONObject analysisData;

	private BSONObject versionData;

	public JavaPairRDD<Object, BSONObject> analyzeGameplays(
			JavaPairRDD<Object, BSONObject> traces, BSONObject versionData,
			BSONObject calculatedData) {
		this.versionData = versionData;
		analysisData = dataAnalysisExtractor.extract(versionData,
				calculatedData);

		TracesAnalyzer tracesAnalyzer = new TracesAnalyzer();
		tracesAnalyzer.readFunctions(Q.getOrSet("functions", analysisData,
				new ArrayList<String>()));

		VariablesSetter variablesSetter = new VariablesSetter(Q.getOrSet(
				"variables", analysisData, new BasicBSONObject()));

		return traces.mapToPair(new ExtractFieldAsKey("gameplayId"))
				.groupByKey().mapToPair(tracesAnalyzer)
				.mapToPair(variablesSetter);
	}

	public List<BSONObject> analyzeSegments(
			JavaPairRDD<Object, BSONObject> gameplayResults) {
		BSONObject dataReducers = Q.getOrSet("reducers", analysisData,
				new BasicBSONObject());
		BSONObject dataSegmentReducers = Q.getOrSet("segmentReducers",
				analysisData, new BasicBSONObject());
		BSONObject dataSegments = Q.getOrSet("segments", analysisData,
				new BasicBSONObject());

		ArrayList<BSONObject> segmentsResults = new ArrayList<BSONObject>();

		if (!dataReducers.toMap().isEmpty()) {
			// All gameplays segment
			BSONObject result = reduce(gameplayResults, dataReducers,
					dataSegmentReducers, false);
			result.put("_name", "all");
			segmentsResults.add(result);
		}

		List<BSONObject> segments = Q.get("segments", versionData);
		if (segments != null) {
			for (BSONObject segment : segments) {
				boolean segmentChanged = dataSegments.containsField((String) Q
						.get("name", segment));

				if ((segmentChanged || !dataReducers.toMap().isEmpty())) {
					JavaPairRDD<Object, BSONObject> segmentedGameplays = gameplayResults
							.filter(new SegmentFilter(Q.<String> get(
									"condition", segment)));

					if (segment.containsField("having")) {
						String having = Q.get("having", segment);
						segmentedGameplays = segmentedGameplays.groupByKey()
								.flatMapToPair(
										new SelectFromList("first"
												.equals(having)));
					}

					BSONObject segmentResults = reduce(segmentedGameplays,
							dataReducers, dataSegmentReducers, segmentChanged);
					segmentResults.put("_name", Q.get("name", segment));
					segmentsResults.add(segmentResults);
				}
			}
		}
		return segmentsResults;
	}

	private BSONObject reduce(JavaPairRDD<Object, BSONObject> gameplayResults,
			BSONObject dataReducers, BSONObject dataSegmentReducers,
			boolean segmentChanged) {
		Reducers reducers = new Reducers();
		reducers.add(dataReducers);
		if (segmentChanged) {
			reducers.add(dataSegmentReducers);
		}

        BSONObject segmentResult;
		if (gameplayResults.count() > 0) {
			segmentResult = gameplayResults.map(reducers).reduce(reducers);
		} else {
			segmentResult = reducers.zero();
		}
        reducers.extraOperations(segmentResult);
        return segmentResult;
	}
}
