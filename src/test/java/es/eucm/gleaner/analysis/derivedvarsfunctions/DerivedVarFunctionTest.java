package es.eucm.gleaner.analysis.derivedvarsfunctions;

import es.eucm.gleaner.analysis.GameplayResultAssert;
import es.eucm.gleaner.analysis.GameplayResultAssert.ResultAsserter;
import es.eucm.gleaner.analysis.MockReport;
import es.eucm.gleaner.analysis.Split;
import es.eucm.gleaner.analysis.analysis.GameplaysAnalysis;
import es.eucm.gleaner.analysis.functions.ExtractFieldAsKey;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.bson.BSONObject;
import org.junit.Test;

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
		GameplaysAnalysis gameplaysAnalysis = new GameplaysAnalysis();
		gameplaysAnalysis.getReportsMap().put("mock", new MockReport());
		gameplaysAnalysis.read(versionData);
		traces.mapToPair(new ExtractFieldAsKey("gameplayId")).groupByKey()
				.mapToPair(gameplaysAnalysis.getTracesAnalyzer())
				.foreach(new GameplayResultAssert(buildResultAsserter()));

	}

}
