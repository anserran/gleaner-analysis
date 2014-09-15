package es.eucm.gleaner.analysis.test;

import com.mongodb.DBObject;
import es.eucm.gleaner.analysis.GameplayResultAssert;
import es.eucm.gleaner.analysis.GameplayResultAssert.ResultAsserter;
import es.eucm.gleaner.analysis.GameplayResultAssert.SegmentAsserter;
import es.eucm.gleaner.analysis.MockReport;
import es.eucm.gleaner.analysis.Q;
import es.eucm.gleaner.analysis.Split;
import es.eucm.gleaner.analysis.analysis.GameplaysAnalysis;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.bson.BSONObject;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public abstract class AnalysisTest {

	protected abstract BSONObject buildVersionData();

	protected abstract List<BSONObject> buildTraces();

	protected abstract ResultAsserter buildResultAsserter();

	protected abstract SegmentAsserter buildSegmentAsserter();

    protected void extraTest(){

    }

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
		JavaPairRDD<Object, BSONObject> gameplaysResults = gameplaysAnalysis
				.calculateGameplayResults(traces);

		gameplaysResults
				.foreach(new GameplayResultAssert(buildResultAsserter()));

		ArrayList<DBObject> segmentsResults = gameplaysAnalysis
				.calculateSegments(gameplaysResults, versionData);

		SegmentAsserter segmentAsserter = buildSegmentAsserter();
		if (segmentAsserter != null) {
			for (DBObject segmentResult : segmentsResults) {
				segmentAsserter.assertSegment(
						Q.<String> get("segmentName", segmentResult),
						segmentResult);
			}
		}
        extraTest();
	}
}
