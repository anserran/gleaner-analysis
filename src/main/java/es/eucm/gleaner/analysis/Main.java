package es.eucm.gleaner.analysis;

import es.eucm.gleaner.analysis.mongo.MongoAnalysis;

import java.net.UnknownHostException;

public class Main {

	public static void main(final String[] args) throws UnknownHostException {
		String versionId = args[0];
		String mongoHost = System.getenv("MONGO_HOST");
		Integer mongoPort = Integer.parseInt(System.getenv("MONGO_PORT"));
		String mongoDB = System.getenv("MONGO_DB");
		MongoAnalysis analysis = new MongoAnalysis(versionId, mongoHost,
				mongoPort, mongoDB);
		analysis.execute(null);
	}
}
