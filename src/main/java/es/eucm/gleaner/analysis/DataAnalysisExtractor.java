package es.eucm.gleaner.analysis;

import es.eucm.gleaner.analysis.reports.ChoicesFrequency;
import es.eucm.gleaner.analysis.reports.Counter;
import es.eucm.gleaner.analysis.reports.Report;
import es.eucm.gleaner.analysis.utils.Q;
import org.bson.BSONObject;
import org.bson.BasicBSONObject;
import scala.Tuple2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataAnalysisExtractor {

	private static final Pattern FUNCTIONS_PATTERN = Pattern
			.compile("\\b[a-zA-Z]+\\(.*?\\)");

	private static final Pattern VARIABLES_PATTERN = Pattern
			.compile("([a-zA-Z$_][a-zA-Z$_0-9]*)([\\s+\\-*/%]|\\Z)");

	private Map<String, Report> reportsMap;

	private List<String> functions;

	private List<String> calculatedFunctions;

	private BSONObject calculatedVariables;

	private BSONObject variables;

	private BSONObject reducers;

	private BSONObject segmentReducers;

	private BSONObject calculatedSegments;

	private BSONObject segments;

	public DataAnalysisExtractor() {
		reportsMap = new HashMap<String, Report>();

		reportsMap.put("counter", new Counter());
		reportsMap.put("choices", new ChoicesFrequency());
	}

	public BSONObject extract(BSONObject versionData, BSONObject calculatedData) {
		calculatedFunctions = Q.getOrSet("functions", calculatedData,
				new ArrayList<String>());
		calculatedVariables = Q.getOrSet("variables", calculatedData,
				new BasicBSONObject());
		calculatedSegments = Q.getOrSet("segments", calculatedData,
				new BasicBSONObject());

		BasicBSONObject analysisData = new BasicBSONObject();
		analysisData.put("functions", functions = new ArrayList<String>());
		analysisData.put("variables", variables = new BasicBSONObject());
		analysisData.put("reducers", reducers = new BasicBSONObject());
		analysisData.put("segmentReducers",
				segmentReducers = new BasicBSONObject());
		analysisData.put("segments", segments = new BasicBSONObject());

		processDerivedVars(Q.<List<BSONObject>> get("derivedVars", versionData));
		processPanels(Q.<List<BSONObject>> get("panels", versionData),
				versionData);
		processSegments(Q.<List<BSONObject>> get("segments", versionData));
		return analysisData;
	}

	private void processDerivedVars(List<BSONObject> derivedVars) {
		for (BSONObject derivedVar : derivedVars) {
			String variableName = Q.get("name", derivedVar);
			String expression = Q.get("value", derivedVar);
			addVariable(variableName, expression);
		}
	}

	private void processPanels(List<BSONObject> panels, BSONObject versionData) {
		for (BSONObject panel : panels) {
			for (BSONObject report : Q.<List<BSONObject>> get("reports", panel)) {
				String id = Q.get("id", report);
				String type = Q.get("type", report);

				Report reportBuilder = reportsMap.get(type);
				if (reportBuilder != null
						&& reportBuilder.readData(id, report, versionData)) {
					BSONObject reducer = reportBuilder.getReducer();
					List<Tuple2<String, String>> variables = reportBuilder
							.getVariables();
					for (Tuple2<String, String> variable : variables) {
						if (addVariable(variable._1, variable._2)) {
							reducers.put(variable._1, reducer);
						} else {
							segmentReducers.put(variable._1, reducer);
						}
					}
				}
			}
		}
	}

	private void processSegments(List<BSONObject> dataSegments) {
		if (dataSegments != null) {
			for (BSONObject segment : dataSegments) {
				String name = Q.get("name", segment);
				String expression = Q.get("condition", segment);
				String having = Q.get("having", segment);
				if (addVariable("_segment_" + name, expression)
						|| segmentOperationChanged(name, having)) {
					segments.put(name, having);
					calculatedSegments.put(name, having);
				}
			}
		}
	}

	private boolean addVariable(String variableName, String expression) {
		if (!isCalculated(variableName, expression)
				|| needsRecalculation(expression)) {
			variables.put(variableName, expression);
			calculatedVariables.put(variableName, expression);

			Matcher matcher = FUNCTIONS_PATTERN.matcher(expression);
			while (matcher.find()) {
				String function = matcher.group();
				if (!calculatedFunctions.contains(function)) {
					calculatedFunctions.add(function);
					functions.add(function);
				}
			}
			return true;
		}
		return false;
	}

	private boolean isCalculated(String variableName, String expression) {
		return calculatedVariables.containsField(variableName)
				&& expression.equals(calculatedVariables.get(variableName));
	}

	/**
	 * @return true if the expression contain variables that are going to be
	 *         recalculated
	 */
	private boolean needsRecalculation(String expression) {
		Matcher matcher = VARIABLES_PATTERN.matcher(expression);
		while (matcher.find()) {
			String variable = matcher.group(0);
			if (variables.containsField(variable)) {
				return true;
			}
		}
		return false;
	}

	private boolean segmentOperationChanged(String name, String having) {
		return !calculatedSegments.containsField(name)
				|| !Objects.equals(having, calculatedSegments.get(name));
	}

}
