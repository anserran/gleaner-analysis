package es.eucm.gleaner.analysis.analysis.traceanalyzers;

import org.bson.BSONObject;

import es.eucm.gleaner.analysis.Q;

public class ZoneReached implements TraceAnalyzer {

    private String resultVariable;

    private String zoneToReach;

    public ZoneReached(String resultVariable, String zoneToReach){
        this.resultVariable = resultVariable;
        this.zoneToReach = zoneToReach;
    }

    @Override
    public void defaultValues(BSONObject gameplayResult) {
        gameplayResult.put(resultVariable, false);
    }

    @Override
    public boolean interestedIn(String event) {
        return zoneToReach != null && ZONE.equals(event);
    }

    @Override
    public void analyze(BSONObject trace, BSONObject gameplayResult) {
        String zone = Q.get(VALUE, trace);
        if (zoneToReach.equals(zone)){
            gameplayResult.put(resultVariable, true);
        }
    }
}
