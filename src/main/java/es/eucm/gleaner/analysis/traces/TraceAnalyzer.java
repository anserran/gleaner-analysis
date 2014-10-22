package es.eucm.gleaner.analysis.traces;

import org.bson.BSONObject;

import java.io.Serializable;

/**
 * Process traces and set variables to the gameplay
 */
public interface TraceAnalyzer extends Serializable {
	public static final String GAMEPLAY_ID = "gameplayId";

	public static final String EVENT = "event";
	public static final String VALUE = "value";
	public static final String TARGET = "target";

	public static final String ZONE = "zone";
    public static final String VAR = "var";
    public static final String CHOICE = "choice";

	/**
	 * Sets in the given result the default values of the calculated variables
	 */
	void defaultValues(BSONObject gameplayResult);

	/**
	 * @return true if this analyzer is interested in the given type of event
	 */
	boolean interestedIn(String event);

	/**
	 * Process a trace and sets pertinent variables in the result
	 */
	void analyze(BSONObject trace, BSONObject gameplayResult);

}
