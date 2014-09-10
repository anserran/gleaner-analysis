package es.eucm.gleaner.analysis.analysis.traceanalyzers;

import org.bson.BSONObject;

import es.eucm.gleaner.analysis.Q;

public class ZoneReached implements TraceAnalyzer {

	private String zoneToReach;

	public ZoneReached(String zoneToReach) {
		this.zoneToReach = zoneToReach;
	}

	@Override
	public void defaultValues(BSONObject gameplayResult) {
		gameplayResult.put(ZONE_REACHED_PREFIX + zoneToReach, false);
	}

	@Override
	public boolean interestedIn(String event) {
		return zoneToReach != null && ZONE.equals(event);
	}

	@Override
	public void analyze(BSONObject trace, BSONObject gameplayResult) {
		String zone = Q.get(VALUE, trace);
		if (zoneToReach.equals(zone)) {
			gameplayResult.put(ZONE_REACHED_PREFIX + zoneToReach, true);
		}
	}

	@Override
	public String getVarsGenerated() {
		return ZONE_REACHED_PREFIX + zoneToReach;
	}
}
