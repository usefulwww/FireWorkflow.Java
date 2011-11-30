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
package org.fireflow.engine;

import java.util.Date;

import org.fireflow.engine.taskinstance.DynamicAssignmentHandler;
import org.fireflow.kernel.KernelException;
import org.fireflow.model.Task;
import org.fireflow.model.WorkflowProcess;
import org.fireflow.model.net.Activity;

/**
 * 任务实例<br>
 * 对任务实例的状态字段作如下规定：小于5的状态为“活动”状态，大于等于5的状态为“非活动”状态。<br>
 * 活动状态包括：INITIALIZED,RUNNING,SUSPENDED<br>
 * 非活动状态包括：COMPLETED,CANCELED
 * 
 * @author 非也,nychen2000@163.com
 * 
 */
public interface ITaskInstance {
	/**
	 * 初始化状态
	 */
	public static final int INITIALIZED = 0;

	/**
	 * 运行状态
	 */
	public static final int RUNNING = 1;
	
	/**
	 * 已经结束
	 */
	public static final int COMPLETED = 7;

	/**
	 * 被撤销
	 */
	public static final int CANCELED = 9;

	/**
	 * 返回任务实例的Id
	 * 
	 * @return
	 */
	public String getId();

	/**
	 * 返回对应的任务Id
	 * 
	 * @return
	 */
	public String getTaskId();

	/**
	 * 返回任务Name
	 * 
	 * @return
	 */
	public String getName();

	/**
	 * 返回任务显示名
	 * 
	 * @return
	 */
	public String getDisplayName();

	public IProcessInstance getAliveProcessInstance();

	/**
	 * 返回对应的流程实例Id
	 * 
	 * @return
	 */
	public String getProcessInstanceId();

	/**
	 * 返回对应的流程的Id
	 * 
	 * @return
	 */
	public String getProcessId();

	/**
	 * 返回流程的版本
	 * 
	 * @return
	 */
	public Integer getVersion();

	/**
	 * 返回任务实例创建的时间
	 * 
	 * @return
	 */
	public Date getCreatedTime();

	/**
	 * 返回任务实例启动的时间
	 * 
	 * @return
	 */
	public Date getStartedTime();

	/**
	 * 返回任务实例结束的时间
	 * 
	 * @return
	 */
	public Date getEndTime();

	/**
	 * 返回任务实例到期日期
	 * 
	 * @return
	 */
	public Date getExpiredTime();// 过期时间

	/**
	 * 返回任务实例的状态，取值为：INITIALIZED(已初始化），STARTED(已启动),COMPLETED(已结束),CANCELD(被取消)
	 * 
	 * @return
	 */
	public Integer getState();

	/**
	 * 返回任务实例的分配策略，取值为 org.fireflow.model.Task.ALL或者org.fireflow.model.Task.ANY
	 * 
	 * @return
	 */
	public String getAssignmentStrategy();

	/**
	 * 返回任务实例所属的环节的Id
	 * 
	 * @return
	 */
	public String getActivityId();

	/**
	 * 返回任务类型，取值为org.fireflow.model.Task.FORM,org.fireflow.model.Task.TOOL,
	 * org.fireflow.model.Task.SUBFLOW或者org.fireflow.model.Task.DUMMY
	 * 
	 * @return
	 */
	public String getTaskType();

	/**
	 * 返回任务是里对应的环节
	 * 
	 * @return
	 * @throws org.fireflow.engine.EngineException
	 */
	public Activity getActivity() throws EngineException;

	/**
	 * 返回任务实例对应的流程
	 * 
	 * @return
	 * @throws org.fireflow.engine.EngineException
	 */
	public WorkflowProcess getWorkflowProcess() throws EngineException;

	/**
	 * 返回任务实例对应的Task对象
	 * 
	 * @return
	 * @throws org.fireflow.engine.EngineException
	 */
	public Task getTask() throws EngineException;

	/**
	 * 当执行JumpTo和LoopTo操作时，返回目标Activity 的Id
	 * 
	 * @return
	 */
	public String getTargetActivityId();

	/**
	 * 返回TaskInstance的"步数"。
	 * 
	 * @return
	 */
	public Integer getStepNumber();

	/**
	 * 挂起
	 * 
	 * @throws org.fireflow.engine.EngineException
	 */
	public void suspend() throws EngineException;

	public Boolean isSuspended();

	/**
	 * 从挂起状态恢复到挂起前的状态
	 * 
	 * @throws org.fireflow.engine.EngineException
	 */
	public void restore() throws EngineException;

	/**
	 * 将该TaskInstance中止，并且使得当前流程实例按照流程定义往下流转。</br>
	 * 与该TaskInstance相关的WorkItem都被置为Canceled状态。</br>
	 * 如果该TaskInstance的状态已经是Completed或者Canceled，则抛出异常。</br>
	 * @throws EngineException
	 */
	public void abort() throws EngineException,KernelException;
	
	/**
	 * 将该TaskInstance中止，并且使得当前流程实例跳转到targetActivityId指定的环节。</br>
	 * 与该TaskInstance相关的WorkItem都被置为Canceled状态。</br>
	 * 如果该TaskInstance的状态已经是Completed或者Canceled，则抛出异常。</br>
	 * @param targetActivityId
	 * @throws EngineException
	 * @throws KernelException
	 */
	public void abort(String targetActivityId) throws EngineException,KernelException;
	
	/**
	 * 将该TaskInstance中止，并且使得当前流程实例跳转到targetActivityId指定的环节。该环节任务的操作人从dynamicAssignmentHandler获取。</br>
	 * 当前环节和目标环节必须在同一条“执行线”上</br>
	 * 与该TaskInstance相关的WorkItem都被置为Canceled状态。</br>
	 * 如果该TaskInstance的状态已经是Completed或者Canceled，则抛出异常。</br>
	 * @param targetActivityId
	 * @param dynamicAssignmentHandler
	 * @param comments
	 * @throws EngineException
	 * @throws KernelException
	 */
	public void abort(String targetActivityId, DynamicAssignmentHandler dynamicAssignmentHandler) throws EngineException,KernelException;
	
	/**
	 * 将该TaskInstance中止，并且使得当前流程实例跳转到targetActivityId指定的环节。该环节任务的操作人从dynamicAssignmentHandler获取。</br>
	 * 当前环节和目标环节 可以不在 同一条“执行线”上</br>
	 * 与该TaskInstance相关的WorkItem都被置为Canceled状态。</br>
	 * 如果该TaskInstance的状态已经是Completed或者Canceled，则抛出异常。</br>
	 * @param targetActivityId
	 * @param dynamicAssignmentHandler
	 * @throws EngineException
	 * @throws KernelException
	 */
	public void abortEx(String targetActivityId,DynamicAssignmentHandler dynamicAssignmentHandler) throws EngineException,KernelException;

}
