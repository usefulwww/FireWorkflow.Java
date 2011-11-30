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

/**
 * 转移条件表达式解析器
 * @author 非也，nychen2000@163.com
 *
 */
public interface IConditionResolver {
    /**
     * 解析条件表达式。条件表达是必须是一个值为boolean类型的EL表达式
     * @param vars 变量列表
     * @param elExpression 条件表达式 
     * @return 返回条件表达式的计算结果
     */
    public boolean resolveBooleanExpression(Map<String ,Object> vars, String elExpression) throws Exception;
}
