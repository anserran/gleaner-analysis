package es.eucm.gleaner.analysis;

import es.eucm.gleaner.analysis.mongo.MongoAnalysis;
import org.apache.spark.SparkConf;

import java.net.UnknownHostException;

public class MainTest {

	public static void main(final String[] args) throws UnknownHostException {
		String versionId = "541fe8eac7d1dcca18e4b287";
		String mongoHost = "localhost";
		Integer mongoPort = 27017;
		String mongoDB = "gleaner";
		MongoAnalysis analysis = new MongoAnalysis(versionId, mongoHost,
				mongoPort, mongoDB);
		analysis.execute(new SparkConf().setMaster("local").setAppName("MainTest"), true);
	}
}
