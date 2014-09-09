package es.eucm.gleaner.analysis.analysis.traceanalyzers;

import org.bson.BSONObject;
import org.bson.types.ObjectId;

import es.eucm.gleaner.analysis.Q;

public class ZoneTime implements TraceAnalyzer {

	private boolean counting;

	private long lastTime;
	private long acc;

    private String varName;
	private String zoneTime;

	public ZoneTime(String varName, String zoneTime) {
        this.varName = varName;
		this.zoneTime = zoneTime;
        this.counting = false;
	}

	@Override
	public void defaultValues(BSONObject gameplayResult) {
        counting = false;
        lastTime = 0;
		gameplayResult.put(varName, acc = 0);
	}

	@Override
	public boolean interestedIn(String event) {
		return counting || ZONE.equals(event);
	}

	@Override
	public void analyze(BSONObject trace, BSONObject gameplayResult) {
		long newTime = ((ObjectId) Q.get("_id", trace)).getTime();
		if (counting) {
			acc += newTime - lastTime;
			gameplayResult.put(varName, acc);
            lastTime = newTime;
		}

		String event = Q.get("event", trace);
		if (ZONE.equals(event)) {
			counting = zoneTime.equals(Q.get("value", trace));
			lastTime = newTime;
		}
	}
}
