package es.eucm.gleaner.analysis.traces;

import es.eucm.gleaner.analysis.utils.Q;
import org.bson.BSONObject;

public class ZoneReached implements TraceAnalyzer {

	public static final String PREFIX = "_reach_";

	private String zoneToReach;

	public ZoneReached(String zoneToReach) {
		this.zoneToReach = zoneToReach;
	}

	@Override
	public void defaultValues(BSONObject gameplayResult) {
		gameplayResult.put(PREFIX + zoneToReach, false);
	}

	@Override
	public boolean interestedIn(String event) {
		return zoneToReach != null && ZONE.equals(event);
	}

	@Override
	public void analyze(BSONObject trace, BSONObject gameplayResult) {
		String zone = Q.get(VALUE, trace);
		if (zoneToReach.equals(zone)) {
			gameplayResult.put(PREFIX + zoneToReach, true);
		}
	}
}
