package es.eucm.gleaner.analysis.analysis.groupoperations;

import org.bson.BSONObject;

public interface GroupOperation {

    void operate(BSONObject groupResult);
}
