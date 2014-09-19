package es.eucm.gleaner.analysis;

import es.eucm.gleaner.analysis.traces.DoubleValue;
import es.eucm.gleaner.analysis.traces.TraceAnalyzer;
import es.eucm.gleaner.analysis.traces.ZoneReached;
import es.eucm.gleaner.analysis.traces.ZoneTime;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TracesAnalyzerTest {

	@Test
	public void test() {
		TracesAnalyzer traceAnalyzer = new TracesAnalyzer();
		List<TraceAnalyzer> list = traceAnalyzer.readFunctions(Arrays.asList(
				"ms('Inicio')", "doubleValue('a')", "reach('Tal')"));
		assertEquals(list.size(), 3);
		assertTrue(list.get(0) instanceof ZoneTime);
		assertTrue(list.get(1) instanceof DoubleValue);
		assertTrue(list.get(2) instanceof ZoneReached);
	}
}
