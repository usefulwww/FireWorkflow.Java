/**
 * Copyright 2004-2008 陈乜云（非也,Chen Nieyun）
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

import org.fireflow.engine.IProcessInstance;
import org.fireflow.model.WorkflowProcess;

/**
 * ProcessInstance负责和外部运行环境（RuntimeContext)沟通
 * 
 * @author chennieyun 
 */
public interface INetInstance {
	/**
	 * @return
	 */
	public String getId();

	/**
	 * @return
	 */
	public Integer getVersion();

	/**
	 * 启动工作流的实例 
	 * @param processInstance
	 * @throws KernelException
	 */
	public void run(IProcessInstance processInstance) throws KernelException;

	/**
	 * 结束流程实例，如果流程状态没有达到终态，则直接返回。
	 * 
	 * @throws RuntimeException
	 */
	public void complete() throws KernelException;

	/**
	 * 
	 * @return
	 */
	public WorkflowProcess getWorkflowProcess();

	/**
	 * 
	 * @param wfElementId
	 * @return
	 */
	public Object getWFElementInstance(String wfElementId);
}
