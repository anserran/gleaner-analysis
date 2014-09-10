package es.eucm.gleaner.analysis.analysis;

import es.eucm.gleaner.analysis.analysis.derivedvarsfunctions.DerivedVarFunction;
import es.eucm.gleaner.analysis.analysis.traceanalyzers.TraceAnalyzer;
import org.bson.BSONObject;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.ScriptableObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DerivedVarsProcessor implements Serializable {

	private HashMap<String, DerivedVarFunction> derivedVarFunctionMap = new HashMap<String, DerivedVarFunction>();
	private ArrayList<DerivedVarFunction> derivedVarFunctions = new ArrayList<DerivedVarFunction>();
	private ArrayList<DerivedVar> derivedVars = new ArrayList<DerivedVar>();

	private Context context;
	private ScriptableObject scope;

	private BSONObject currentGameplayResult;

	public void addDerivedVarFunction(DerivedVarFunction function) {
		derivedVarFunctions.add(function);
		derivedVarFunctionMap.put(function.getFunctionName(), function);
	}

	public void init() {
		context = Context.enter();
		scope = context.initStandardObjects();
		for (DerivedVarFunction function : derivedVarFunctions) {
			context.evaluateString(scope,
					getProgram(function.getFunctionName()),
					function.getFunctionName() + ".js", 1, null);
		}
	}

	private String getProgram(String functionName) {
		String program = "var " + functionName + "List = [];";
		program += "function "
				+ functionName
				+ "(){ "
				+ functionName
				+ "List.push(Array.prototype.slice.call(arguments)); return 1; }";
		return program;
	}

	/**
	 * Adds a variable and expression to be evaluated
	 */
	public void addExpression(String varName, String varExpression) {
		context.evaluateString(scope, varExpression.replace("||", "&&")
				.replace("?", "&&").replace(":", "&&"), "<cmd>", 1, null);
		DerivedVar derivedVar = new DerivedVar(varName, varExpression);
		if (!derivedVars.contains(derivedVar)) {
			derivedVars.add(derivedVar);
		}
	}

	/**
	 * @return the list of all necessary trace analyzers to calculated the
	 *         current contained expressions
	 */
	public List<TraceAnalyzer> getTraceAnalyzers() {
		ArrayList<TraceAnalyzer> traceAnalyzers = new ArrayList<TraceAnalyzer>();
		for (DerivedVarFunction derivedVarFunction : derivedVarFunctions) {
			Object value = scope.get(derivedVarFunction.getFunctionName()
					+ "List", scope);
			if (value instanceof NativeArray) {
				ArrayList<List<Object>> argumentsList = new ArrayList<List<Object>>();
				for (Object o : (NativeArray) value) {
					List list = (List) o;
					if (!contains(argumentsList, list)) {
						argumentsList.add(list);
					}
				}
				traceAnalyzers.addAll(derivedVarFunction
						.getTraceAnalyzers(argumentsList));
			}
		}
		Context.exit();
		context = null;
		scope = null;
		return traceAnalyzers;
	}

	/**
	 * Evaluates all current expression and sets their results in the given
	 * gameplay result
	 */
	public void evaluateAllDerivedVars(BSONObject gameplayResult) {
		this.currentGameplayResult = gameplayResult;
		context = Context.enter();
		scope = context.initStandardObjects();

		Object wrappedOut = Context.javaToJS(this, scope);
		ScriptableObject.putProperty(scope, "analyzer", wrappedOut);

		// Create all functions
		for (DerivedVarFunction function : derivedVarFunctions) {
			context.evaluateString(
					scope,
					getEvaluateProgram(function.getFunctionName(),
							function.getResultType()), "", 1, null);
		}

		context.evaluateString(scope,
				"function $value(expression){ return eval(expression); }", "",
				1, null);

		Function value = (Function) scope.get("$value", scope);

		for (DerivedVar var : derivedVars) {
			Object result = value.call(context, scope, null,
					new Object[] { var.expression });
			gameplayResult
					.put(var.name, Context.jsToJava(result, Object.class));
		}
		Context.exit();
	}

	private String getEvaluateProgram(String functionName, String type) {
		return "function " + functionName + "(){ return analyzer." + type
				+ "Value('" + functionName
				+ "', Array.prototype.slice.call(arguments)); }";
	}

	public double doubleValue(String function, List<Object> arguments) {
		if (derivedVarFunctionMap.containsKey(function)) {
			Object value = derivedVarFunctionMap.get(function).call(
					currentGameplayResult, arguments);
			return value instanceof Number ? ((Number) value).doubleValue() : 0;
		}
		return 0;
	}

	public boolean booleanValue(String function, List<Object> arguments) {
		if (derivedVarFunctionMap.containsKey(function)) {
			Object value = derivedVarFunctionMap.get(function).call(
					currentGameplayResult, arguments);
			return value instanceof Boolean && ((Boolean) value);
		}
		return false;
	}

	private boolean contains(ArrayList<List<Object>> argumentsList,
			List<Object> arguments) {
		for (List<Object> list : argumentsList) {
			if (list.size() == arguments.size()) {
				boolean equals = true;
				int i = 0;
				for (Object o : list) {
					if (!o.equals(arguments.get(i++))) {
						equals = false;
						break;
					}
				}
				if (equals) {
					return true;
				}
			}
		}
		return false;
	}

	private static class DerivedVar implements Serializable {
		private String name;
		private String expression;

		private DerivedVar(String name, String expression) {
			this.name = name;
			this.expression = expression;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o)
				return true;
			if (o == null || getClass() != o.getClass())
				return false;

			DerivedVar that = (DerivedVar) o;

			return name.equals(that.name);
		}

		@Override
		public int hashCode() {
			return name.hashCode();
		}
	}

}
