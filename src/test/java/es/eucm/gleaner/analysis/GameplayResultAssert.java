package es.eucm.gleaner.analysis;

import org.apache.spark.api.java.function.VoidFunction;
import org.bson.BSONObject;
import scala.Tuple2;

import java.io.Serializable;

/**
 * Created by angel on 10/09/14.
 */
public class GameplayResultAssert implements
		VoidFunction<Tuple2<Object, BSONObject>> {
	private ResultAsserter asserter;

	public GameplayResultAssert(ResultAsserter asserter) {
		this.asserter = asserter;
	}

	@Override
	public void call(Tuple2<Object, BSONObject> tuple2) throws Exception {
		String gameplayId = (String) tuple2._2.get("gameplayId");
		if (asserter != null) {
			asserter.assertResult(gameplayId, tuple2._2);
		}
	}

	/**
	 * Created by angel on 10/09/14.
	 */
	public static interface ResultAsserter extends Serializable {
		void assertResult(String gameplayId, BSONObject gameplayResult);
	}

	public static interface SegmentAsserter extends Serializable {
		void assertSegment(String segmentName, BSONObject segmentResult);
	}
}
