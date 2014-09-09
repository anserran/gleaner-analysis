package es.eucm.gleaner.analysis.mappers;

import es.eucm.gleaner.analysis.analysis.GameplayResultCalculator;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.PairFunction;
import org.bson.BSONObject;
import org.bson.BasicBSONObject;
import org.junit.Test;
import scala.Tuple2;

import java.util.List;

public abstract class MapperTest {

	protected abstract BSONObject buildVersionData();

    protected abstract List<BSONObject> buildGameplaysResults();

	protected abstract void assertResult(BSONObject result);

	@Test
	public void test() {
		JavaSparkContext sparkContext = new JavaSparkContext(new SparkConf()
				.setMaster("local").setAppName("test"));

		GameplayResultCalculator gameplayResultCalculator = new GameplayResultCalculator(
				buildVersionData());

		JavaPairRDD<Object, BSONObject> gameplaysResults = sparkContext
				.parallelize(buildGameplaysResults()).mapToPair(new AddNull());

		BSONObject result = gameplaysResults.map(
				gameplayResultCalculator.getMapReducers()).reduce(
				gameplayResultCalculator.getMapReducers());
		assertResult(result);
	}

    public static class GameplayResult extends BasicBSONObject {

        public GameplayResult(String var, Object value){
            put(var, value);
        }

    }

	public static class AddNull implements
			PairFunction<BSONObject, Object, BSONObject> {
		@Override
		public Tuple2<Object, BSONObject> call(BSONObject bsonObject)
				throws Exception {
			return new Tuple2<Object, BSONObject>(null, bsonObject);
		}
	}

}
