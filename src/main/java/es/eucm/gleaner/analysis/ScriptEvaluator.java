package es.eucm.gleaner.analysis;

import sun.org.mozilla.javascript.Context;
import sun.org.mozilla.javascript.Function;
import sun.org.mozilla.javascript.ScriptableObject;

import java.util.Map;
import java.util.Map.Entry;

public class ScriptEvaluator {

	public static Object evaluateExpression(Map<String, Object> varValues,
			String expression) {
		try {
			Context context = Context.enter();
			ScriptableObject scope = context.initStandardObjects();
			for (Entry<String, Object> entry : varValues.entrySet()) {
				try {
					context.evaluateString(scope, "var " + entry.getKey()
							+ " = " + entry.getValue(), "", 1, null);
				} catch (Exception e) {
                    context.evaluateString(scope, "var " + entry.getKey()
                            + " = '" + entry.getValue() + "'", "", 1, null);
				}
			}
			context.evaluateString(scope, "function $value(){ return "
					+ expression + ";}", "", 1, null);
			Function value = (Function) scope.get("$value", scope);
			Object result = value.call(context, scope, null, null);
			return Context.jsToJava(result, Object.class);
		} catch (Exception e) {
			return null;
		} finally {
			Context.exit();
		}
	}
}
