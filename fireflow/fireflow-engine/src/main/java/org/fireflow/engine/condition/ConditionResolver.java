/**
 * Copyright 2007-2008 非也
 * All rights reserved. 
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation。
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see http://www.gnu.org/licenses. *
 */
package org.fireflow.engine.condition;

import java.util.Map;

import org.apache.commons.jexl.Expression;
import org.apache.commons.jexl.ExpressionFactory;
import org.apache.commons.jexl.JexlContext;
import org.apache.commons.jexl.JexlHelper;
import org.fireflow.engine.IRuntimeContextAware;
import org.fireflow.engine.RuntimeContext;

/**
 * 缺省使用apache的JEXL实现条件表达式的解析。
 * @author 非也,nychen2000@163.com
 * 
 * 
 */
public class ConditionResolver implements IConditionResolver,
		IRuntimeContextAware {
	protected RuntimeContext rtCtx = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.fireflow.kenel.condition.IConditionResolver#resolveBooleanExpression
	 * (java.lang.String)
	 */
	public boolean resolveBooleanExpression(Map<String ,Object> vars, String elExpression)
			throws Exception {
		Expression expression = ExpressionFactory.createExpression(elExpression);
		JexlContext jexlCtx = JexlHelper.createContext();
		jexlCtx.setVars(vars);
		Object obj = expression.evaluate(jexlCtx);
		return (Boolean) obj;
	}

	public void setRuntimeContext(RuntimeContext ctx) {
		rtCtx = ctx;
	}

	public RuntimeContext getRuntimeContext() {
		return this.rtCtx;
	}
}
