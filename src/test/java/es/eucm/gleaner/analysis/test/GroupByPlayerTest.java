package es.eucm.gleaner.analysis.test;

import es.eucm.gleaner.analysis.GameplayResultAssert.ResultAsserter;
import es.eucm.gleaner.analysis.GameplayResultAssert.SegmentAsserter;
import es.eucm.gleaner.analysis.VersionData;
import org.bson.BSONObject;

import java.util.List;

public class GroupByPlayerTest extends AnalysisTest {
    @Override
    protected BSONObject buildVersionData() {
        VersionData versionData = new VersionData();
        versionData.putVar("completed", "reach('end')");
        versionData.putVar("score", "double('score')");
        versionData.addSegment("segment1", "true", "first", "completed === true");
        return versionData;
    }

    @Override
    protected List<BSONObject> buildTraces() {
        return null;
    }

    @Override
    protected ResultAsserter buildResultAsserter() {
        return null;
    }

    @Override
    protected SegmentAsserter buildSegmentAsserter() {
        return null;
    }
}
