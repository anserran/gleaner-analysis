package es.eucm.gleaner.analysis.traces;

import java.util.List;

public interface FunctionEvaluator {

    public static final String[] FUNCTIONS = new String[] { "ms", "reach",
            "doubleValue", "choices" };
    
    long ms(List<Object> arguments);
    boolean reach(List<Object> arguments);
    double doubleValue(List<Object> arguments);
    Object choices(List<Object> arguments);
}
