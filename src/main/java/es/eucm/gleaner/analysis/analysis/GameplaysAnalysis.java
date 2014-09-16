package es.eucm.gleaner.analysis.analysis;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import es.eucm.gleaner.analysis.Q;
import es.eucm.gleaner.analysis.analysis.derivedvarsfunctions.DoubleFunction;
import es.eucm.gleaner.analysis.analysis.derivedvarsfunctions.MsFunction;
import es.eucm.gleaner.analysis.analysis.derivedvarsfunctions.ReachFunction;
import es.eucm.gleaner.analysis.analysis.groupoperations.GroupOperation;
import es.eucm.gleaner.analysis.analysis.reports.CounterReport;
import es.eucm.gleaner.analysis.analysis.reports.Report;
import es.eucm.gleaner.analysis.functions.ExtractFieldAsKey;
import es.eucm.gleaner.analysis.functions.MetCondition;
import es.eucm.gleaner.analysis.functions.SegmentFilter;
import org.apache.spark.api.java.JavaPairRDD;
import org.bson.BSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GameplaysAnalysis {

	private DerivedVarsProcessor derivedVarsProcessor;

	private TracesAnalyzer tracesAnalyzer;

	private MapReducers mapReducers;

	private ArrayList<GroupOperation> groupOperations;

	private HashMap<String, Report> reportsMap = new HashMap<String, Report>();

	public GameplaysAnalysis() {
		reportsMap.put("counter", new CounterReport());
		derivedVarsProcessor = new DerivedVarsProcessor();
		addDerivedVarFunctions(derivedVarsProcessor);
		mapReducers = new MapReducers();
		groupOperations = new ArrayList<GroupOperation>();
		derivedVarsProcessor.init();
		tracesAnalyzer = new TracesAnalyzer(derivedVarsProcessor);
	}

	public void read(BSONObject versionData) {
		List<BSONObject> panels = Q.get("panels", versionData);
		if (panels != null) {
			for (BSONObject panel : panels) {
				List<BSONObject> reports = Q.get("reports", panel);
				if (reports != null) {
					for (BSONObject reportData : reports) {
						Report report = reportsMap.get(Q
								.get("type", reportData));
						if (report != null) {
							report.readReportData(reportData);
							List<String> vars = report
									.addTracesAnalyzers(tracesAnalyzer);
							for (String var : vars) {
								String expression = getDerivedVarExpression(
										versionData, var);
								if (expression != null) {
									derivedVarsProcessor.addExpression(var,
											expression);
								}
							}
							report.addMapReducers(mapReducers);
						}
					}
				}
			}
		}
		tracesAnalyzer.addAll(derivedVarsProcessor.getTraceAnalyzers());
	}

	private void addDerivedVarFunctions(
			DerivedVarsProcessor derivedVarsProcessor) {
		derivedVarsProcessor.addDerivedVarFunction(new ReachFunction());
		derivedVarsProcessor.addDerivedVarFunction(new MsFunction());
		derivedVarsProcessor.addDerivedVarFunction(new DoubleFunction());
	}

	public TracesAnalyzer getTracesAnalyzer() {
		return tracesAnalyzer;
	}

	public MapReducers getMapReducers() {
		return mapReducers;
	}

	public void groupOperations(BSONObject groupResult) {
		for (GroupOperation operation : groupOperations) {
			operation.operate(groupResult);
		}
	}

	private String getDerivedVarExpression(BSONObject versionData,
			String variableName) {
		List<BSONObject> derivedVars = Q.get("derivedVars", versionData);
		if (derivedVars != null) {
			for (BSONObject derivedVar : derivedVars) {
				if (variableName.equals(Q.get("name", derivedVar))) {
					return Q.getValue(derivedVar);
				}
			}
		}
		return null;
	}

	public HashMap<String, Report> getReportsMap() {
		return reportsMap;
	}

	public JavaPairRDD<Object, BSONObject> calculateGameplayResults(
			JavaPairRDD<Object, BSONObject> traces) {
		// Traces grouped by gameplay<gameplayId, trace>
		JavaPairRDD<String, Iterable<BSONObject>> gameplays = traces
				.mapToPair(new ExtractFieldAsKey("gameplayId")).groupByKey()
				.cache();

		// Gameplays results, after passing all the analysis
		return gameplays.mapToPair(getTracesAnalyzer());
	}

	/**
	 * Calculate segment results
	 * 
	 * @param gameplayResults
	 *            as key, the player id, as value, a gameplay result
	 * @param versionData
	 *            version data containing segments data
	 * @return a list with the segments results
	 */
	public ArrayList<DBObject> calculateSegments(
			JavaPairRDD<Object, BSONObject> gameplayResults,
			BSONObject versionData) {

		ArrayList<DBObject> segmentsResults = new ArrayList<DBObject>();

		DBObject allResults = new BasicDBObject(gameplayResults
				.map(getMapReducers()).reduce(getMapReducers()).toMap());
		groupOperations(allResults);
		allResults.put("segmentName", "all");
		segmentsResults.add(allResults);

		List<BSONObject> segments = Q.get("segments", versionData);
		if (segments != null) {
			for (BSONObject segment : segments) {
				JavaPairRDD<Object, BSONObject> segmentedGameplays = gameplayResults
						.filter(new SegmentFilter(Q.<String> get("condition",
								segment)));

				if (segment.containsField("groupbyplayer")) {
					BSONObject groupby = Q.get("groupbyplayer", segment);
					String operator = Q.get("operator", groupby);
					String operation = Q.get("operation", groupby);
					segmentedGameplays = segmentedGameplays.groupByKey()
							.flatMapToPair(
									new MetCondition("first".equals(operator),
											operation));
				}

				if (segmentedGameplays.count() > 0) {
					DBObject segmentResults = new BasicDBObject(
							segmentedGameplays.map(getMapReducers())
									.reduce(getMapReducers()).toMap());
					groupOperations(segmentResults);
					segmentResults.put("segmentName", Q.get("name", segment));
					segmentsResults.add(segmentResults);
				}
			}
		}
		return segmentsResults;
	}
}
