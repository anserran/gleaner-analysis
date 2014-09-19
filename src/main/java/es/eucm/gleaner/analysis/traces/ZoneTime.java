package es.eucm.gleaner.analysis.traces;

import es.eucm.gleaner.analysis.utils.Q;
import org.bson.BSONObject;
import org.bson.types.ObjectId;

public class ZoneTime implements TraceAnalyzer {

	public static final String PREFIX = "_ms_";

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
		gameplayResult.put(PREFIX + zoneId, acc = 0);
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
			gameplayResult.put(PREFIX + zoneId, acc);
			lastTime = newTime;
		}

		String event = Q.get("event", trace);
		if (ZONE.equals(event)) {
			counting = zoneId.equals(Q.get("value", trace));
			lastTime = newTime;
		}
	}
}
