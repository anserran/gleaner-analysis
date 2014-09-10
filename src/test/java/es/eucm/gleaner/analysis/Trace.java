package es.eucm.gleaner.analysis;

import org.bson.BasicBSONObject;
import org.bson.types.ObjectId;

import java.util.Date;

/**
* Created by angel on 10/09/14.
*/
public class Trace extends BasicBSONObject {

    public Trace time(long time) {
        put("_id", new ObjectId(new Date(time)));
        return this;
    }

    public static class ZoneTrace extends Trace {

		public ZoneTrace(String gameplayId, String zone) {
			put("gameplayId", gameplayId);
			put("event", "zone");
			put("value", zone);
		}

	}
}
