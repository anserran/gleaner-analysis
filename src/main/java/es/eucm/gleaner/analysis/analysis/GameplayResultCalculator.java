package es.eucm.gleaner.analysis.analysis;

import com.mongodb.DBObject;
import es.eucm.gleaner.analysis.Q;
import es.eucm.gleaner.analysis.analysis.derivedvarsfunctions.DoubleFunction;
import es.eucm.gleaner.analysis.analysis.derivedvarsfunctions.MsFunction;
import es.eucm.gleaner.analysis.analysis.derivedvarsfunctions.ReachFunction;
import es.eucm.gleaner.analysis.analysis.groupoperations.BoxPlotOperation;
import es.eucm.gleaner.analysis.analysis.groupoperations.GroupOperation;
import es.eucm.gleaner.analysis.analysis.mappers.ListReducer;
import es.eucm.gleaner.analysis.analysis.mappers.Mean;
import es.eucm.gleaner.analysis.analysis.mappers.Sum;
import org.bson.BSONObject;

import java.util.ArrayList;
import java.util.List;

public class GameplayResultCalculator {

	private DerivedVarsProcessor extractor;

	private TracesAnalyzer tracesAnalyzer;

	private MapReducers mapReducers;

    private ArrayList<GroupOperation> groupOperations;

	public GameplayResultCalculator(BSONObject versionData) {
		mapReducers = new MapReducers();
        groupOperations = new ArrayList<GroupOperation>();
		extractor = new DerivedVarsProcessor();
        addDerivedVarFunctions(extractor);
		extractor.init();
		List<DBObject> derivedVars = Q.get("derivedVars", versionData);
		for (DBObject derivedVar : derivedVars) {
			String name = Q.get("name", derivedVar);
			String value = Q.get("value", derivedVar);
			extractor.addExpression(name, value);
			List<String> aggregations = Q.get("aggregations", derivedVar);
			if (aggregations != null) {
				for (String aggregation : aggregations) {
					if ("sum".equals(aggregation)) {
						mapReducers.addMapper(new Sum(name, "sum_" + name));
					} else if ("mean".equals(aggregation)) {
						mapReducers.addMapper(new Mean(name, "mean_" + name));
                    } else if ("list".equals(aggregation)) {
                        mapReducers.addMapper(new ListReducer(name));
					} else if ("boxplot".equals(aggregation)){
                        mapReducers.addMapper(new ListReducer(name));
                        groupOperations.add(new BoxPlotOperation(name));
                    }
				}
			}
		}
		tracesAnalyzer = new TracesAnalyzer(extractor);
	}

    private void addDerivedVarFunctions(DerivedVarsProcessor extractor) {
        extractor.addDerivedVarFunction(new ReachFunction());
        extractor.addDerivedVarFunction(new MsFunction());
        extractor.addDerivedVarFunction(new DoubleFunction());
    }

    public TracesAnalyzer getTracesAnalyzer() {
		return tracesAnalyzer;
	}

	public MapReducers getMapReducers() {
		return mapReducers;
	}

    public void groupOperations(BSONObject groupResult){
        for (GroupOperation operation: groupOperations){
            operation.operate(groupResult);
        }
    }
}
