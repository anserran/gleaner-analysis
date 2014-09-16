package es.eucm.gleaner.analysis;

import org.bson.BasicBSONObject;
import org.bson.types.ObjectId;

import java.util.Date;

public class Trace extends BasicBSONObject {

	public Trace(String gameplayId) {
		put("gameplayId", gameplayId);
	}

	public Trace time(long time) {
		put("_id", new ObjectId(new Date(time)));
		return this;
	}

	public static class ZoneTrace extends Trace {

		public ZoneTrace(String gameplayId, String zone) {
			super(gameplayId);
			put("event", "zone");
			put("value", zone);
		}

	}

	public static class VarTrace extends Trace {
		public VarTrace(String gameplayId, String var, Object value) {
			super(gameplayId);
			put("event", "var");
			put("target", var);
			put("value", value);
		}
	}
}
