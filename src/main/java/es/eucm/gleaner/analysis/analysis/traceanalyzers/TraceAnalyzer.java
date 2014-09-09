package es.eucm.gleaner.analysis.analysis.traceanalyzers;

import org.bson.BSONObject;

import java.io.Serializable;

public interface TraceAnalyzer extends Serializable {
    public static final String GAMEPLAY_ID = "gameplayId";
    public static final String EVENT = "event";
    public static final String VALUE = "value";
    public static final String TARGET = "target";

    public static final String ZONE = "zone";

    public abstract void defaultValues(BSONObject gameplayResult);

    public abstract boolean interestedIn(String event);

    public abstract void analyze(BSONObject trace, BSONObject gameplayResult);


}
