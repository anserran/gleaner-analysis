package es.eucm.gleaner.analysis.derivedvarsfunctions;

import es.eucm.gleaner.analysis.analysis.GameplayResultCalculator;
import es.eucm.gleaner.analysis.functions.ExtractFieldAsKey;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.api.java.function.VoidFunction;
import org.bson.BSONObject;
import org.bson.BasicBSONObject;
import org.bson.types.ObjectId;
import org.junit.Test;
import scala.Tuple2;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public abstract class DerivedVarFunctionTest {

	protected abstract BSONObject buildVersionData();

	protected abstract List<BSONObject> buildTraces();

	protected abstract ResultAsserter buildResultAsserter();

	@Test
	public void test() {
		JavaSparkContext sparkContext = new JavaSparkContext(new SparkConf()
				.setMaster("local").setAppName("test"));
		JavaPairRDD<Object, BSONObject> traces = sparkContext.parallelize(
				buildTraces()).mapToPair(new Split());
		BSONObject versionData = buildVersionData();
		GameplayResultCalculator gameplayResultCalculator = new GameplayResultCalculator(
				versionData);
		traces.mapToPair(new ExtractFieldAsKey("gameplayId")).groupByKey()
				.mapToPair(gameplayResultCalculator.getTracesAnalyzer())
				.foreach(new Assert(buildResultAsserter()));

	}

	public static class Trace extends BasicBSONObject {

		public Trace time(long time) {
			put("_id", new ObjectId(new Date(time)));
			return this;
		}
	}

	public static class ZoneTrace extends Trace {

		public ZoneTrace(String gameplayId, String zone) {
			put("gameplayId", gameplayId);
			put("event", "zone");
			put("value", zone);
		}

	}

	public static class Split implements
			PairFunction<BSONObject, Object, BSONObject> {
		@Override
		public Tuple2<Object, BSONObject> call(BSONObject bsonObject)
				throws Exception {
			return new Tuple2<Object, BSONObject>(null, bsonObject);
		}
	}

	public static class Assert implements
			VoidFunction<Tuple2<Object, BSONObject>> {
		private ResultAsserter asserter;

		public Assert(ResultAsserter asserter) {
			this.asserter = asserter;
		}

		@Override
		public void call(Tuple2<Object, BSONObject> tuple2) throws Exception {
			String gameplayId = (String) tuple2._2.get("gameplayId");
			asserter.assertResult(gameplayId, tuple2._2);
		}
	}

	public interface ResultAsserter extends Serializable {
		void assertResult(String gameplayId, BSONObject gameplayResult);
	}
}
