package es.eucm.gleaner.analysis;

import es.eucm.gleaner.analysis.analysis.derivedvarsfunctions.DerivedVarFunction;
import es.eucm.gleaner.analysis.analysis.DerivedVarsProcessor;
import es.eucm.gleaner.analysis.analysis.traceanalyzers.TraceAnalyzer;
import org.bson.BSONObject;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class TraceExtractorTest {

    @Test
    public void testExtractor(){
        DerivedVarsProcessor extractor = new DerivedVarsProcessor();
        extractor.addDerivedVarFunction(new TestDerivedVarFunction());
        extractor.addDerivedVarFunction(new TestDerivedVarFunction2());

        extractor.init();
        extractor.addExpression("", "test1('arg1') + test1('arg2')");
        extractor.addExpression("", "test1('arg1') || test2('arg3')");
        extractor.addExpression("", "test1('arg1') && test2('arg4')");
        extractor.addExpression("", "test1('arg1') ? test2('arg5') : test2('arg6')");
        extractor.getTraceAnalyzers();
    }

    public static class TestDerivedVarFunction implements DerivedVarFunction {

        @Override
        public String getFunctionName() {
            return "test1";
        }

        @Override
        public String getResultType() {
            return null;
        }

        @Override
        public List<TraceAnalyzer> getTraceAnalyzers(List<List<Object>> arguments) {
            assertEquals(arguments.size(), 2);
            assertEquals(arguments.get(0).get(0), "arg1");
            assertEquals(arguments.get(1).get(0), "arg2");
            return Arrays.asList();
        }

        @Override
        public Object call(BSONObject gameplayResult, List<Object> arguments) {
            return null;
        }
    }

    public static class TestDerivedVarFunction2 implements DerivedVarFunction {

        @Override
        public String getFunctionName() {
            return "test2";
        }

        @Override
        public String getResultType() {
            return null;
        }

        @Override
        public List<TraceAnalyzer> getTraceAnalyzers(List<List<Object>> arguments) {
            assertEquals(arguments.size(), 4);
            assertEquals(arguments.get(0).get(0), "arg3");
            assertEquals(arguments.get(1).get(0), "arg4");
            assertEquals(arguments.get(2).get(0), "arg5");
            assertEquals(arguments.get(3).get(0), "arg6");
            return Arrays.asList();
        }

        @Override
        public Object call(BSONObject gameplayResult, List<Object> arguments) {
            return null;
        }
    }
}
