package org.fireflow.engine.condition;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.wltea.expression.ExpressionEvaluator;
import org.wltea.expression.datameta.Variable;

public class IKConditionResolver implements IConditionResolver {

	/* (non-Javadoc)
	 * @see org.fireflow.engine.condition.IConditionResolver#resolveBooleanExpression(java.util.Map, java.lang.String)
	 */
	public boolean resolveBooleanExpression(Map<String ,Object> vars, String elExpression)
			throws Exception {
//		System.out.println("-----====------IK Expression---Expression is "+elExpression);
		List<Variable> variables = new ArrayList<Variable>();
		String[] keys = vars.keySet().toArray(new String[vars.size()]);
		for (int i=0;keys!=null && i<keys.length;i++){
			String key =keys[i];
			variables.add(Variable.createVariable(key, vars.get(key)));
		}

		Object result = ExpressionEvaluator.evaluate(elExpression,variables);
		
		Boolean b = (Boolean)result;
		return b.booleanValue();
	}

}
