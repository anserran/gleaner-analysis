package es.eucm.gleaner.analysis.analysis.traceanalyzers;

import org.bson.BSONObject;
import org.bson.types.ObjectId;

import es.eucm.gleaner.analysis.Q;

public class ZoneTime implements TraceAnalyzer {

	private boolean counting;

	private long lastTime;
	private long acc;

	private String zoneId;

	public ZoneTime(String zoneId) {
		this.zoneId = zoneId;
		this.counting = false;
	}

	@Override
	public void defaultValues(BSONObject gameplayResult) {
		counting = false;
		lastTime = 0;
		gameplayResult.put(ZONE_TIME_PREFIX + zoneId, acc = 0);
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
			gameplayResult.put(ZONE_TIME_PREFIX + zoneId, acc);
			lastTime = newTime;
		}

		String event = Q.get("event", trace);
		if (ZONE.equals(event)) {
			counting = zoneId.equals(Q.get("value", trace));
			lastTime = newTime;
		}
	}

	@Override
	public String getVarsGenerated() {
		return ZONE_TIME_PREFIX + zoneId;
	}
}
