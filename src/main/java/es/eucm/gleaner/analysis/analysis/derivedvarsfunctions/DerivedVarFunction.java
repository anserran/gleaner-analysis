package es.eucm.gleaner.analysis.analysis.derivedvarsfunctions;

import es.eucm.gleaner.analysis.analysis.traceanalyzers.TraceAnalyzer;
import org.bson.BSONObject;

import java.io.Serializable;
import java.util.List;

public interface DerivedVarFunction extends Serializable {

	/**
	 * @return name for the function. Should be unique across all functions
	 */
	String getFunctionName();

    /**
     * @return string representing the type that returns this: can be "double", "boolean"
     */
    String getResultType();

	/**
	 * @return trace analyzers necessary to calculate the value for the function
	 *         with all passed arguments
	 */
	List<TraceAnalyzer> getTraceAnalyzers(List<List<Object>> argumentsList);

	/**
	 * Calls the function with the given parameters. Can use values in current
	 * gameplay result to obtain the final value
	 */
	Object call(BSONObject gameplayResult, List<Object> arguments);
}
