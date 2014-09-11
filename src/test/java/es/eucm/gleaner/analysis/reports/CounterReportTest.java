package es.eucm.gleaner.analysis.reports;

import es.eucm.gleaner.analysis.analysis.reports.CounterReport;
import org.bson.BasicBSONObject;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class CounterReportTest {

    @Test
    public void test(){
        CounterReport counter = new CounterReport();
        counter.readReportData(new BasicBSONObject("condition", "completed + 1*3.310-_count_/$com3"));
        List<String> vars = counter.addTracesAnalyzers(null);
        assertEquals(vars.get(0), "completed");
        assertEquals(vars.get(1), "_count_");
        assertEquals(vars.get(2), "$com3");
    }
}
