package es.eucm.gleaner.analysis.analysis.traceanalyzers;

import org.bson.BSONObject;
import org.bson.types.ObjectId;

import es.eucm.gleaner.analysis.Q;

public class AllZonesTime implements TraceAnalyzer {

	private long lastTime;

    private String currentScene;

	@Override
	public void defaultValues(BSONObject gameplayResult) {
        lastTime = -1;
        currentScene = null;
	}

	@Override
	public boolean interestedIn(String event) {
		return ZONE.equals(event);
	}

	@Override
	public void analyze(BSONObject trace, BSONObject gameplayResult) {
		long newTime = ((ObjectId) Q.get("_id", trace)).getTime();
        if (lastTime != -1 && currentScene != null){
            long diff = newTime - lastTime;
            long acc = (Long) gameplayResult.get(ZONE_TIME_PREFIX + currentScene);
            gameplayResult.put(ZONE_TIME_PREFIX + currentScene, acc + diff);
        }

        currentScene = Q.getValue(trace);
        lastTime = newTime;
	}

    @Override
    public String getVarsGenerated() {
        return ZONE_TIME_PREFIX + ".*";
    }
}
