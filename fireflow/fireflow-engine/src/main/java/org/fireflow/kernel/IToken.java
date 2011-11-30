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
package org.fireflow.kernel;

import java.util.Map;

import org.fireflow.engine.IProcessInstance;

/**
 * 
 * token的生命周期开始与一个synchronizer（包括startnode 和 endnode)，结束于另一个synchronizer
 * 
 * @author 非也
 * 
 */
public interface IToken {

	/**
	 * 前驱activityid的分隔符为"&"
	 */
	public static final String FROM_ACTIVITY_ID_SEPARATOR = "&";
	
	public static final String FROM_START_NODE = "FROM_START_NODE";

	/**
	 * @return
	 */
	public IProcessInstance getProcessInstance();

	/**
	 * @param inst
	 */
	public void setProcessInstance(IProcessInstance inst);

	/**
	 * @param id
	 */
	public void setProcessInstanceId(String id);

	/**
	 * @return
	 */
	public String getProcessInstanceId();


	/**
	 * @return
	 */
	public String getNodeId();

	/**
	 * @param nodeId
	 */
	public void setNodeId(String nodeId);


	/**
	 * @param v
	 */
	public void setValue(Integer v);

	/**
	 * @return
	 */
	public Integer getValue();

	/**
	 * 通过alive标志来判断nodeinstance是否要fire
	 * @return
	 */
	public Boolean isAlive();

	/**
	 * @param b
	 */
	public void setAlive(Boolean b);

	/**
	 * @return
	 */
	public String getId();

	/**
	 * @param id
	 */
	public void setId(String id);

	/**
	 * @return
	 */
	public Integer getStepNumber();

	/**
	 * @param i
	 */
	public void setStepNumber(Integer i);

	/**
	 * 获得前驱Activity的Id,如果有多个，则用"&"分割
	 * 
	 * @return
	 */
	public String getFromActivityId();

	/**
	 * @param s
	 */
	public void setFromActivityId(String s);
	
    /**
     * @date 20090908 
     * 返回Engine的当前上下文信息，如WorkflowSession,等。
     * 这些信息不保存到数据库    
     * @return
     */
    public Map<String ,IProcessInstance> getContextInfo();
}
