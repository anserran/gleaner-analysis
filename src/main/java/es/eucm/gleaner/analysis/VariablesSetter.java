package es.eucm.gleaner.analysis;

import es.eucm.gleaner.analysis.traces.DoubleValue;
import es.eucm.gleaner.analysis.traces.FunctionEvaluator;
import es.eucm.gleaner.analysis.traces.SelectedOptions;
import es.eucm.gleaner.analysis.traces.ZoneReached;
import es.eucm.gleaner.analysis.traces.ZoneTime;
import es.eucm.gleaner.analysis.utils.Q;
import org.apache.spark.api.java.function.PairFunction;
import org.bson.BSONObject;
import scala.Tuple2;
import sun.org.mozilla.javascript.Context;
import sun.org.mozilla.javascript.Function;
import sun.org.mozilla.javascript.ScriptableObject;

import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

public class VariablesSetter implements
		PairFunction<Tuple2<Object, BSONObject>, Object, BSONObject>,
		FunctionEvaluator {

	private BSONObject variables;

	private BSONObject gameplayResult;

	public VariablesSetter(BSONObject variables) {
		this.variables = variables;
	}

	@Override
	public Tuple2<Object, BSONObject> call(Tuple2<Object, BSONObject> tuple2) {
		this.gameplayResult = tuple2._2;
		try {
			Context context = Context.enter();
			ScriptableObject scope = context.initStandardObjects();
			Set<Entry> entries = gameplayResult.toMap().entrySet();
			for (Entry entry : entries) {
				setVar(entry.getKey(), entry.getValue(), context, scope);
			}

			Object wrappedOut = Context.javaToJS(this, scope);
			ScriptableObject.putProperty(scope, "setter", wrappedOut);

			for (String function : FUNCTIONS) {
				context.evaluateString(scope, "function " + function
						+ "(){ return setter." + function
						+ "(Array.prototype.slice.call(arguments)); }", null,
						1, null);
			}

			context.evaluateString(scope,
					"function $value(expression){ return eval(expression); }",
					"", 1, null);

			Function value = (Function) scope.get("$value", scope);

			for (String var : variables.keySet()) {
				Object result = value.call(context, scope, null,
						new Object[] { variables.get(var) });
				Object javaValue = Context.jsToJava(result, Object.class);
				gameplayResult.put(var, javaValue);
				setVar(var, javaValue, context, scope);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			Context.exit();
		}

		return tuple2;
	}

	public void setVar(Object varName, Object value, Context context,
			ScriptableObject scope) {
		try {
			context.evaluateString(scope, "var " + varName + " = " + value, "",
					1, null);
		} catch (Exception e) {
			context.evaluateString(scope, "var " + varName + " = '" + value
					+ "'", "", 1, null);
		}
	}

	@Override
	public long ms(List<Object> arguments) {
		return Q.getLong(ZoneTime.PREFIX + arguments.get(0), gameplayResult);
	}

	@Override
	public boolean reach(List<Object> arguments) {
		return Q.getBoolean(ZoneReached.PREFIX + arguments.get(0),
				gameplayResult);
	}

	@Override
	public double doubleValue(List<Object> arguments) {
		return Q.getDouble(DoubleValue.PREFIX + arguments.get(0),
				gameplayResult);
	}

	@Override
	public Object choices(List<Object> arguments) {
		return Q.get(SelectedOptions.PREFIX + arguments.get(0), gameplayResult);
	}
}
