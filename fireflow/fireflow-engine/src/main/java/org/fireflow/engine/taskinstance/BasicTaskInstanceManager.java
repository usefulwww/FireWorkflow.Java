/**
 * Copyright 2007-2008非也
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
package org.fireflow.engine.taskinstance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.fireflow.engine.EngineException;
import org.fireflow.engine.IProcessInstance;
import org.fireflow.engine.IRuntimeContextAware;
import org.fireflow.engine.ITaskInstance;
import org.fireflow.engine.IWorkItem;
import org.fireflow.engine.IWorkflowSession;
import org.fireflow.engine.IWorkflowSessionAware;
import org.fireflow.engine.RuntimeContext;
import org.fireflow.engine.beanfactory.IBeanFactory;
import org.fireflow.engine.calendar.ICalendarService;
import org.fireflow.engine.event.ITaskInstanceEventListener;
import org.fireflow.engine.event.TaskInstanceEvent;
import org.fireflow.engine.impl.ProcessInstanceTrace;
import org.fireflow.engine.impl.TaskInstance;
import org.fireflow.engine.impl.WorkItem;
import org.fireflow.engine.impl.WorkflowSession;
import org.fireflow.engine.persistence.IPersistenceService;
import org.fireflow.kernel.IActivityInstance;
import org.fireflow.kernel.INetInstance;
import org.fireflow.kernel.ISynchronizerInstance;
import org.fireflow.kernel.IToken;
import org.fireflow.kernel.ITransitionInstance;
import org.fireflow.kernel.KernelException;
import org.fireflow.kernel.impl.Token;
import org.fireflow.model.Duration;
import org.fireflow.model.EventListener;
import org.fireflow.model.FormTask;
import org.fireflow.model.IWFElement;
import org.fireflow.model.Task;
import org.fireflow.model.WorkflowProcess;
import org.fireflow.model.net.Activity;
import org.fireflow.model.net.EndNode;
import org.fireflow.model.net.Node;
import org.fireflow.model.net.Synchronizer;
import org.fireflow.model.net.Transition;
import org.springframework.beans.BeanUtils;

/**
 * 缺省的任务实例管理器实现。
 * 
 * @author 非也,chennieyun
 * 
 */
public class BasicTaskInstanceManager implements ITaskInstanceManager {

	ITaskInstanceCreator defaultTaskInstanceCreator = null;
	ITaskInstanceRunner defaultFormTaskInstanceRunner = null;
	ITaskInstanceRunner defaultSubflowTaskInstanceRunner = null;
	ITaskInstanceRunner defaultToolTaskInstanceRunner = null;
	ITaskInstanceCompletionEvaluator defaultFormTaskInstanceCompletionEvaluator = null;
	ITaskInstanceCompletionEvaluator defaultToolTaskInstanceCompletionEvaluator = null;
	ITaskInstanceCompletionEvaluator defaultSubflowTaskInstanceCompletionEvaluator = null;

	ITaskInstanceEventListener defaultTaskInstanceEventListener = null;

	protected RuntimeContext rtCtx = null;

	public void setRuntimeContext(RuntimeContext ctx) {
		this.rtCtx = ctx;
	}

	public RuntimeContext getRuntimeContext() {
		return this.rtCtx;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.fireflow.engine.taskinstance.ITaskInstanceManager#archiveTaskInstances
	 * (org.fireflow.kenel.IActivityInstance)
	 */

	public void archiveTaskInstances(IActivityInstance activityInstance) {
		// TODO Auto-generated method stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.fireflow.engine.taskinstance.ITaskInstanceManager#createTaskInstances
	 * (org.fireflow.kenel.IActivityInstance)
	 */
	public final void createTaskInstances(IToken token,
			IActivityInstance activityInstance) throws EngineException,
			KernelException {
		
		Activity activity = activityInstance.getActivity();
		IPersistenceService persistenceService = rtCtx.getPersistenceService();
		ICalendarService calService = rtCtx.getCalendarService();

		IProcessInstance processInstance = token.getProcessInstance();
		WorkflowSession workflowSession = (WorkflowSession) ((IWorkflowSessionAware) processInstance)
				.getCurrentWorkflowSession();

		if (workflowSession == null) {
			throw new EngineException(token.getProcessInstance(),
					activityInstance.getActivity(),
					"The workflow session in process instance can NOT be null");
		}

		int createdTaskInstanceCount = 0;
		for (int i = 0; i < activity.getTasks().size(); i++) {
			Task task = activity.getTasks().get(i);
			// 1、创建Task实例，并设置工作流系统定义的属性
			ITaskInstance taskInstance = this.createTaskInstance(
					workflowSession, processInstance, task, activity);

			if (taskInstance == null) {
				continue;
			}
			createdTaskInstanceCount = createdTaskInstanceCount + 1;

			String taskType = task.getType();
			((TaskInstance) taskInstance).setTaskType(taskType);
			((TaskInstance) taskInstance).setStepNumber(token.getStepNumber());
			// ((TaskInstance) taskInstance).setTokenId(token.getId());
			((TaskInstance) taskInstance).setProcessInstanceId(processInstance
					.getId());
			((TaskInstance) taskInstance).setProcessId(processInstance
					.getProcessId());
			((TaskInstance) taskInstance).setVersion(processInstance
					.getVersion());
			((TaskInstance) taskInstance).setActivityId(activity.getId());
			if (Task.FORM.equals(taskType)) {
				((TaskInstance) taskInstance)
						.setAssignmentStrategy(((FormTask) task)
								.getAssignmentStrategy());
				((TaskInstance) taskInstance).setCanBeWithdrawn(true);
			} else {
				((TaskInstance) taskInstance).setCanBeWithdrawn(false);
			}
			((TaskInstance) taskInstance).setCreatedTime(calService
					.getSysDate());
			((TaskInstance) taskInstance).setDisplayName(task.getDisplayName());
			((TaskInstance) taskInstance).setName(task.getName());

			((TaskInstance) taskInstance).setState(TaskInstance.INITIALIZED);

			((TaskInstance) taskInstance).setTaskId(task.getId());

			((TaskInstance) taskInstance).setFromActivityId(token
					.getFromActivityId());

			((IRuntimeContextAware) taskInstance).setRuntimeContext(rtCtx);
			((IWorkflowSessionAware) taskInstance)
					.setCurrentWorkflowSession(workflowSession);
			// 计算超时
			Duration duration = task.getDuration();

			if (duration != null && calService != null) {
				((TaskInstance) taskInstance).setExpiredTime(calService
						.dateAfter(calService.getSysDate(), duration));
			}

			// 2、保存实例taskInstance
			persistenceService.saveOrUpdateTaskInstance(taskInstance);

			// 3、启动实例
			this.startTaskInstance(workflowSession, processInstance,
					taskInstance);

		}
		if (createdTaskInstanceCount == 0) { //如果是空activity，哪么直接结束
			activityInstance.complete(token, null);
		}
	}

	/**
	 * 
	 * @param currentSession
	 * @param processInstance
	 * @param task
	 * @param activity
	 * @return
	 * @throws EngineException
	 */
	public ITaskInstance createTaskInstance(IWorkflowSession currentSession,
			IProcessInstance processInstance, Task task, Activity activity)
			throws EngineException {
		// 如果loopStrategy为SKIP且Task被执行过，则直接返回null;
		IPersistenceService persistenceService = this.rtCtx
				.getPersistenceService();
		String loopStrategy = task.getLoopStrategy();
		if (loopStrategy != null && Task.SKIP.equals(loopStrategy)
				&& !currentSession.isInWithdrawOrRejectOperation()) {
			// 检查是否已经执行过的task instance
			Integer count = persistenceService
					.getCompletedTaskInstanceCountForTask(processInstance
							.getId(), task.getId());
			if (count > 0) {
				return null;
			}
		}

		String taskInstanceCreatorName = null;
		ITaskInstanceCreator taskInstanceCreator = null;

		// 首先查找Task级别的TaskInstanceCreator
		taskInstanceCreatorName = task.getTaskInstanceCreator();
		if (taskInstanceCreatorName != null
				&& !taskInstanceCreatorName.trim().equals("")) {
			IBeanFactory beanFactory = this.rtCtx.getBeanFactory();
			taskInstanceCreator = (ITaskInstanceCreator) beanFactory
					.getBean(taskInstanceCreatorName);
		}
		// 如果没有，则查询流程级别的TaskInstanceCreator
		if (taskInstanceCreator == null) {
			taskInstanceCreatorName = processInstance.getWorkflowProcess()
					.getTaskInstanceCreator();
			if (taskInstanceCreatorName != null
					&& !taskInstanceCreatorName.trim().equals("")) {
				IBeanFactory beanFactory = this.rtCtx.getBeanFactory();
				taskInstanceCreator = (ITaskInstanceCreator) beanFactory
						.getBean(taskInstanceCreatorName);
			}
		}

		// 如果流程定义中也没有指定TaskInstanceCreator,则用缺省的
		if (taskInstanceCreator == null) {
			taskInstanceCreator = defaultTaskInstanceCreator;
		}

		return taskInstanceCreator.createTaskInstance(currentSession, rtCtx,
				processInstance, task, activity);
	}

	/**
	 * 启动TaskInstance。<br>
	 * 该方法定义为final,不允许子类扩展。
	 * 
	 * @param taskInstance
	 * @throws org.fireflow.engine.EngineException
	 * @throws org.fireflow.kenel.KenelException
	 */
	public final void startTaskInstance(IWorkflowSession currentSession,
			IProcessInstance processInstance, ITaskInstance taskInstance)
			throws EngineException, KernelException {

		// 触发事件
		TaskInstanceEvent e = new TaskInstanceEvent();
		e.setSource(taskInstance);
		e.setWorkflowSession(currentSession);
		e.setProcessInstance(processInstance);
		e.setEventType(TaskInstanceEvent.BEFORE_TASK_INSTANCE_START);
		if (defaultTaskInstanceEventListener != null) {
			defaultTaskInstanceEventListener.onTaskInstanceEventFired(e);
		}
		this.fireTaskInstanceEvent(taskInstance, e);

		((TaskInstance) taskInstance).setState(ITaskInstance.RUNNING);
		((TaskInstance) taskInstance).setStartedTime(this.rtCtx
				.getCalendarService().getSysDate());
		this.rtCtx.getPersistenceService().saveOrUpdateTaskInstance(
				taskInstance);

		Task task = taskInstance.getTask();
		String taskInstanceRunnerName = null;
		ITaskInstanceRunner taskInstanceRunner = null;

		String taskType = task.getType();

		taskInstanceRunnerName = task.getTaskInstanceRunner();
		if (taskInstanceRunnerName != null
				&& !taskInstanceRunnerName.trim().equals("")) {
			IBeanFactory beanFactory = this.rtCtx.getBeanFactory();
			taskInstanceRunner = (ITaskInstanceRunner) beanFactory
					.getBean(taskInstanceRunnerName);
		}

		if (taskInstanceRunner == null) {
			if (Task.FORM.equals(taskType)) {
				taskInstanceRunnerName = processInstance.getWorkflowProcess()
						.getFormTaskInstanceRunner();
			} else if (Task.TOOL.equals(taskType)) {
				taskInstanceRunnerName = processInstance.getWorkflowProcess()
						.getToolTaskInstanceRunner();
			} else if (Task.SUBFLOW.equals(taskType)) {
				taskInstanceRunnerName = processInstance.getWorkflowProcess()
						.getSubflowTaskInstanceRunner();
			}
			if (taskInstanceRunnerName != null
					&& !taskInstanceRunnerName.trim().equals("")) {
				IBeanFactory beanFactory = this.rtCtx.getBeanFactory();
				taskInstanceRunner = (ITaskInstanceRunner) beanFactory
						.getBean(taskInstanceRunnerName);
			}
		}

		if (taskInstanceRunner == null) {
			if (Task.FORM.equals(taskType)) {
				taskInstanceRunner = defaultFormTaskInstanceRunner;
			} else if (Task.TOOL.equals(taskType)) {
				taskInstanceRunner = defaultToolTaskInstanceRunner;
			} else if (Task.SUBFLOW.equals(taskType)) {
				taskInstanceRunner = defaultSubflowTaskInstanceRunner;
			}
		}
		if (taskInstanceRunner != null) {
			taskInstanceRunner.run(currentSession, this.rtCtx, processInstance,
					taskInstance);
		} else {
			WorkflowProcess process = taskInstance.getWorkflowProcess();
			throw new EngineException(taskInstance.getProcessInstanceId(),
					process, taskInstance.getTaskId(),
					"无法获取TaskInstanceRunner,TaskId=" + task.getId()
							+ ", taskType=" + taskInstance.getTaskType());
		}
	}

	/**
	 * 判断TaskInstance是否可以结束，缺省的判断规则是：没有活动的WorkItem即可结束。<br>
	 * 业务代码可以重载该函数，对特定的Task采取特殊的判断规则。
	 * 
	 * @param taskInstance
	 * @return
	 */
	protected final boolean taskInstanceCanBeCompleted(
			IWorkflowSession currentSession, RuntimeContext runtimeContext,
			IProcessInstance processInstance, ITaskInstance taskInstance)
			throws EngineException, KernelException {
		Task task = taskInstance.getTask();
		String taskInstanceCompletionEvaluatorName = null;
		ITaskInstanceCompletionEvaluator taskInstanceCompletionEvaluator = null;

		String taskType = task.getType();

		taskInstanceCompletionEvaluatorName = task
				.getTaskInstanceCompletionEvaluator();
		if (taskInstanceCompletionEvaluatorName != null
				&& !taskInstanceCompletionEvaluatorName.trim().equals("")) {
			IBeanFactory beanFactory = runtimeContext.getBeanFactory();
			taskInstanceCompletionEvaluator = (ITaskInstanceCompletionEvaluator) beanFactory
					.getBean(taskInstanceCompletionEvaluatorName);
		}

		if (taskInstanceCompletionEvaluator == null) {
			if (Task.FORM.equals(taskType)) {
				taskInstanceCompletionEvaluatorName = processInstance
						.getWorkflowProcess()
						.getFormTaskInstanceCompletionEvaluator();
			} else if (Task.TOOL.equals(taskType)) {
				taskInstanceCompletionEvaluatorName = processInstance
						.getWorkflowProcess()
						.getToolTaskInstanceCompletionEvaluator();
			} else if (Task.SUBFLOW.equals(taskType)) {
				taskInstanceCompletionEvaluatorName = processInstance
						.getWorkflowProcess()
						.getSubflowTaskInstanceCompletionEvaluator();
			}
			if (taskInstanceCompletionEvaluatorName != null
					&& !taskInstanceCompletionEvaluatorName.trim().equals("")) {
				IBeanFactory beanFactory = runtimeContext.getBeanFactory();
				taskInstanceCompletionEvaluator = (ITaskInstanceCompletionEvaluator) beanFactory
						.getBean(taskInstanceCompletionEvaluatorName);
			}
		}

		if (taskInstanceCompletionEvaluator == null) {
			if (Task.FORM.equals(taskType)) {
				taskInstanceCompletionEvaluator = this.defaultFormTaskInstanceCompletionEvaluator;
			} else if (Task.TOOL.equals(taskType)) {
				taskInstanceCompletionEvaluator = this.defaultToolTaskInstanceCompletionEvaluator;
			} else if (Task.SUBFLOW.equals(taskType)) {
				taskInstanceCompletionEvaluator = this.defaultSubflowTaskInstanceCompletionEvaluator;
			}
		}
		if (taskInstanceCompletionEvaluator != null) {
			return taskInstanceCompletionEvaluator.taskInstanceCanBeCompleted(
					currentSession, runtimeContext, processInstance,
					taskInstance);
		} else {
			WorkflowProcess process = taskInstance.getWorkflowProcess();
			throw new EngineException(taskInstance.getProcessInstanceId(),
					process, taskInstance.getTaskId(),
					"无法获取TaskInstanceCompletionEvaluator,TaskId="
							+ task.getId() + ", taskType="
							+ taskInstance.getTaskType());
		}
	}

	/**
	 * 
	 * @param taskInstance
	 * @return
	 * @throws EngineException
	 */
	protected boolean activityInstanceCanBeCompleted(ITaskInstance taskInstance)
			throws EngineException {
		IPersistenceService persistenceService = this.rtCtx
				.getPersistenceService();
		Activity thisActivity = (Activity) taskInstance.getActivity();
		// 检查是否有尚未创建的TaskInstance
		if (thisActivity.getTasks().size() > 1) {
			List<ITaskInstance> taskInstanceList = persistenceService
					.findTaskInstancesForProcessInstanceByStepNumber(
							taskInstance.getProcessInstanceId(), taskInstance
									.getStepNumber());
			if (taskInstanceList == null
					|| taskInstanceList.size() < thisActivity.getTasks().size()) {
				return false;
			}
		}
		if (thisActivity.getCompletionStrategy().equals(Activity.ALL)) {
			Integer aliveTaskInstanceCount4ThisActivity = persistenceService
					.getAliveTaskInstanceCountForActivity(taskInstance
							.getProcessInstanceId(), taskInstance
							.getActivityId());

			if (aliveTaskInstanceCount4ThisActivity.intValue() > 0) {
				return false;// 尚有未结束的TaskInstance
			} else {
				return true;
			}
		} else {
			return true;// 此处应该不需要判断，因为对于已经结束的Activity已经没有对应的token。所以继续往下执行不会导致逻辑错误。
		}
	}

	/**
	 * 终止当前TaskInstance，检查是否可以中止当前ActivityInstance，如果可以，
	 * 则结束当前ActivityInstance，并触发targetActivityInstance或后继ActivityInstance
	 * 
	 * @param taskInstance
	 * @param targetActivityInstance
	 * @throws org.fireflow.engine.EngineException
	 * @throws org.fireflow.kernel.KernelException
	 */
	public final void completeTaskInstance(IWorkflowSession currentSession,
			IProcessInstance processInstance, ITaskInstance taskInstance,
			IActivityInstance targetActivityInstance) throws EngineException,
			KernelException {
		// 如果TaskInstance处于结束状态，则直接返回
		if (taskInstance.getState() == ITaskInstance.COMPLETED
				|| taskInstance.getState() == ITaskInstance.CANCELED) {
			return;
		}
		if (taskInstance.getState() == ITaskInstance.INITIALIZED) {
			WorkflowProcess process = taskInstance.getWorkflowProcess();
			throw new EngineException(taskInstance.getProcessInstanceId(),
					process, taskInstance.getTaskId(),
					"Complete task insatance failed.The state of the task insatnce[id="
							+ taskInstance.getId() + "] is "
							+ taskInstance.getState());
		}
		if (taskInstance.isSuspended()) {
			WorkflowProcess process = taskInstance.getWorkflowProcess();
			throw new EngineException(taskInstance.getProcessInstanceId(),
					process, taskInstance.getTaskId(),
					"Complete task insatance failed. The task instance [id="
							+ taskInstance.getId() + "] is suspended");
		}

		if (targetActivityInstance != null) {
			((TaskInstance) taskInstance)
					.setTargetActivityId(targetActivityInstance.getActivity()
							.getId());
		}

		IPersistenceService persistenceService = this.rtCtx
				.getPersistenceService();

		// 第一步，首先结束当前taskInstance
		if (!this.taskInstanceCanBeCompleted(currentSession, this.rtCtx,
				processInstance, taskInstance)) {
			return;
		}
		((TaskInstance) taskInstance).setState(ITaskInstance.COMPLETED);
		((TaskInstance) taskInstance).setCanBeWithdrawn(Boolean.FALSE);
		((TaskInstance) taskInstance).setEndTime(rtCtx.getCalendarService()
				.getSysDate());
		persistenceService.saveOrUpdateTaskInstance(taskInstance);
		// 触发相应的事件
		TaskInstanceEvent e = new TaskInstanceEvent();
		e.setSource(taskInstance);
		e.setWorkflowSession(currentSession);
		e.setProcessInstance(processInstance);
		e.setEventType(TaskInstanceEvent.AFTER_TASK_INSTANCE_COMPLETE);
		if (this.defaultTaskInstanceEventListener != null) {
			this.defaultTaskInstanceEventListener.onTaskInstanceEventFired(e);
		}

		this.fireTaskInstanceEvent(taskInstance, e);

		// 第二步，检查ActivityInstance是否可以结束
		if (!activityInstanceCanBeCompleted(taskInstance)) {
			return;
		}

		// 第三步，尝试结束对应的activityInstance
		List<IToken> tokens = persistenceService.findTokensForProcessInstance(
				taskInstance.getProcessInstanceId(), taskInstance
						.getActivityId());
		// System.out.println("Inside TaskInstance.complete(targetActivityInstance):: tokens.size is "+tokens.size());
		if (tokens == null || tokens.size() == 0) {
			return;// 表明activityInstance已经结束了。
		}
		if (tokens.size() > 1) {
			WorkflowProcess process = taskInstance.getWorkflowProcess();
			throw new EngineException(taskInstance.getProcessInstanceId(),
					process, taskInstance.getTaskId(), "与activityId="
							+ taskInstance.getActivityId() + "对应的token数量(="
							+ tokens.size() + ")不正确，正确只能为1，因此无法完成complete操作");
		}
		IToken token = tokens.get(0);
		// stepNumber不相等，不允许执行结束操作。
		if (token.getStepNumber().intValue() != taskInstance.getStepNumber()
				.intValue()) {
			return;
		}
		if (token.isAlive() == false) {
			WorkflowProcess process = taskInstance.getWorkflowProcess();
			throw new EngineException(taskInstance.getProcessInstanceId(),
					process, taskInstance.getTaskId(), "与activityId="
							+ taskInstance.getActivityId()
							+ "对应的token.alive=false，因此无法完成complete操作");
		}

		INetInstance netInstance = rtCtx.getKernelManager().getNetInstance(
				taskInstance.getProcessId(), taskInstance.getVersion());
		Object obj = netInstance.getWFElementInstance(taskInstance
				.getActivityId());
		if (obj == null) {
			WorkflowProcess process = taskInstance.getWorkflowProcess();
			throw new EngineException(taskInstance.getProcessInstanceId(),
					process, taskInstance.getTaskId(), "系统没有找到与activityId="
							+ taskInstance.getActivityId()
							+ "对应activityInstance，无法执行complete操作。");
		}

		token.setProcessInstance(processInstance);

		((IActivityInstance) obj).complete(token, targetActivityInstance);

	}

	/**
	 * 中止当前的TaskInstance,并使得流程流转到指定的环节。
	 * 
	 * @param currentSession
	 * @param processInstance
	 * @param taskInstance
	 * @param targetActivityInstance
	 * @throws EngineException
	 * @throws KernelException
	 */
	public final void abortTaskInstance(IWorkflowSession currentSession,
			IProcessInstance processInstance, ITaskInstance taskInstance,
			String targetActivityId) throws EngineException, KernelException {
		// 如果TaskInstance处于结束状态，则直接返回
		if (taskInstance.getState() == ITaskInstance.COMPLETED
				|| taskInstance.getState() == ITaskInstance.CANCELED) {
			return;
		}
		// Initialized状态的TaskInstance也可以中止，20090830
		// if (taskInstance.getState() == ITaskInstance.INITIALIZED) {
		// WorkflowProcess process = taskInstance.getWorkflowProcess();
		// throw new EngineException(taskInstance.getProcessInstanceId(),
		// process,
		// taskInstance.getTaskId(),
		// "Complete task insatance failed.The state of the task insatnce[id=" +
		// taskInstance.getId() + "] is " + taskInstance.getState());
		// }
		if (taskInstance.isSuspended()) {
			WorkflowProcess process = taskInstance.getWorkflowProcess();
			throw new EngineException(taskInstance.getProcessInstanceId(),
					process, taskInstance.getTaskId(),
					"Abort task insatance failed. The task instance [id="
							+ taskInstance.getId() + "] is suspended");
		}

		// 1）检查是否在同一个“执行线”上

		WorkflowProcess workflowProcess = taskInstance.getWorkflowProcess();
		if (targetActivityId != null) {
			String thisActivityId = taskInstance.getActivityId();
			boolean isInSameLine = workflowProcess.isInSameLine(thisActivityId,
					targetActivityId);
			if (!isInSameLine) {
				throw new EngineException(
						taskInstance.getProcessInstanceId(),
						taskInstance.getWorkflowProcess(),
						taskInstance.getTaskId(),
						"Jumpto refused because of the current activitgy and the target activity are NOT in the same 'Execution Thread'.");
			}
		}

		INetInstance netInstance = rtCtx.getKernelManager().getNetInstance(
				workflowProcess.getId(), taskInstance.getVersion());
		IActivityInstance targetActivityInstance = (IActivityInstance) netInstance
				.getWFElementInstance(targetActivityId);

		IActivityInstance thisActivityInstance = (IActivityInstance) netInstance
				.getWFElementInstance(taskInstance.getActivityId());
		if (thisActivityInstance == null) {
			WorkflowProcess process = taskInstance.getWorkflowProcess();
			throw new EngineException(taskInstance.getProcessInstanceId(),
					process, taskInstance.getTaskId(), "系统没有找到与activityId="
							+ taskInstance.getActivityId() 
							+ "对应activityInstance，无法执行abort操作。");
		}

		if (targetActivityInstance != null) {
			((TaskInstance) taskInstance)
					.setTargetActivityId(targetActivityInstance.getActivity()
							.getId());
		}

		IPersistenceService persistenceService = this.rtCtx
				.getPersistenceService();

		// 第一步，首先Abort当前taskInstance
		persistenceService.abortTaskInstance((TaskInstance) taskInstance);

		// 触发相应的事件
		TaskInstanceEvent e = new TaskInstanceEvent();
		e.setSource(taskInstance);
		e.setWorkflowSession(currentSession);
		e.setProcessInstance(processInstance);
		e.setEventType(TaskInstanceEvent.AFTER_TASK_INSTANCE_COMPLETE);
		if (this.defaultTaskInstanceEventListener != null) {
			this.defaultTaskInstanceEventListener.onTaskInstanceEventFired(e);
		}

		this.fireTaskInstanceEvent(taskInstance, e);

		// 第二步，检查ActivityInstance是否可以结束
		if (!activityInstanceCanBeCompleted(taskInstance)) {
			return;
		}

		// 第三步，尝试结束对应的activityInstance
		List<IToken> tokens = persistenceService.findTokensForProcessInstance(
				taskInstance.getProcessInstanceId(), taskInstance
						.getActivityId());
		// System.out.println("Inside TaskInstance.complete(targetActivityInstance):: tokens.size is "+tokens.size());
		if (tokens == null || tokens.size() == 0) {
			return;// 表明activityInstance已经结束了。
		}
		if (tokens.size() > 1) {
			WorkflowProcess process = taskInstance.getWorkflowProcess();
			throw new EngineException(taskInstance.getProcessInstanceId(),
					process, taskInstance.getTaskId(), "与activityId="
							+ taskInstance.getActivityId() + "对应的token数量(="
							+ tokens.size() + ")不正确，正确只能为1，因此无法完成complete操作");
		}
		IToken token = tokens.get(0);
		// stepNumber不相等，不允许执行结束操作。
		if (token.getStepNumber().intValue() != taskInstance.getStepNumber()
				.intValue()) {
			return;
		}
		if (token.isAlive() == false) {
			WorkflowProcess process = taskInstance.getWorkflowProcess();
			throw new EngineException(taskInstance.getProcessInstanceId(),
					process, taskInstance.getTaskId(), "与activityId="
							+ taskInstance.getActivityId()
							+ "对应的token.alive=false，因此无法完成complete操作");
		}

		token.setProcessInstance(processInstance);

		thisActivityInstance.complete(token, targetActivityInstance);

	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.taskinstance.ITaskInstanceManager#abortTaskInstanceEx(org.fireflow.engine.IWorkflowSession, org.fireflow.engine.IProcessInstance, org.fireflow.engine.ITaskInstance, java.lang.String)
	 */
	public final void abortTaskInstanceEx(IWorkflowSession currentSession,
			IProcessInstance processInstance, ITaskInstance thisTaskInst,
			String targetActivityId) throws EngineException, KernelException {
		// 如果TaskInstance处于结束状态，则直接返回
		if (thisTaskInst.getState() == ITaskInstance.COMPLETED
				|| thisTaskInst.getState() == ITaskInstance.CANCELED) {
			return;
		}
		
		// Initialized状态的TaskInstance也可以中止，20090830
		// if (taskInstance.getState() == ITaskInstance.INITIALIZED) {
		// WorkflowProcess process = taskInstance.getWorkflowProcess();
		// throw new EngineException(taskInstance.getProcessInstanceId(),
		// process,
		// taskInstance.getTaskId(),
		// "Complete task insatance failed.The state of the task insatnce[id=" +
		// taskInstance.getId() + "] is " + taskInstance.getState());
		// }
		if (thisTaskInst.isSuspended()) {
			WorkflowProcess process = thisTaskInst.getWorkflowProcess();
			throw new EngineException(thisTaskInst.getProcessInstanceId(),
					process, thisTaskInst.getTaskId(),
					"Abort task insatance failed. The task instance [id="
							+ thisTaskInst.getId() + "] is suspended");
		}

		// 
		IPersistenceService persistenceService = this.rtCtx.getPersistenceService();
		WorkflowProcess workflowProcess = thisTaskInst.getWorkflowProcess();
		List<IToken> allTokens = null;
		List<String> aliveActivityIdsAfterJump = new ArrayList<String>();
		if (targetActivityId != null) {			
			String thisActivityId = thisTaskInst.getActivityId();
			boolean isInSameLine = workflowProcess.isInSameLine(thisActivityId,
					targetActivityId);
			
			if (isInSameLine){
				this.abortTaskInstance(currentSession, processInstance, thisTaskInst, targetActivityId);
			}
			
			//合法性检查
			allTokens = persistenceService.findTokensForProcessInstance(thisTaskInst.getProcessInstanceId(), null);

			aliveActivityIdsAfterJump.add(targetActivityId);
			
			for (int i=0;allTokens!=null && i<allTokens.size();i++){
				IToken tokenTmp = allTokens.get(i);
				IWFElement workflowElement = workflowProcess.findWFElementById(tokenTmp.getNodeId());
				if ((workflowElement instanceof Activity) && !workflowElement.getId().equals(thisActivityId)){
					
					aliveActivityIdsAfterJump.add(workflowElement.getId());
					
					if (workflowProcess.isReachable(targetActivityId, workflowElement.getId()) 
						|| workflowProcess.isReachable(workflowElement.getId(), targetActivityId)){
						throw new EngineException(
								thisTaskInst.getProcessInstanceId(),
								thisTaskInst.getWorkflowProcess(),
								thisTaskInst.getTaskId(),
								"Abort refused because of the business-logic conflict!");

					}
				}
			}			
			
			//1）检查是否在同一个“执行线”上(不做该检查，20091008)	
//			if (!isInSameLine) {
//				throw new EngineException(
//						taskInstance.getProcessInstanceId(),
//						taskInstance.getWorkflowProcess(),
//						taskInstance.getTaskId(),
//						"Jumpto refused because of the current activitgy and the target activity are NOT in the same 'Execution Thread'.");
//			}
		}

		INetInstance netInstance = rtCtx.getKernelManager().getNetInstance(
				workflowProcess.getId(), thisTaskInst.getVersion());
		IActivityInstance targetActivityInstance = null;
		if (targetActivityId!=null){
			targetActivityInstance = (IActivityInstance) netInstance
				.getWFElementInstance(targetActivityId);
		}

		IActivityInstance thisActivityInstance = (IActivityInstance) netInstance
				.getWFElementInstance(thisTaskInst.getActivityId());
		if (thisActivityInstance == null) {
			WorkflowProcess process = thisTaskInst.getWorkflowProcess();
			throw new EngineException(thisTaskInst.getProcessInstanceId(),
					process, thisTaskInst.getTaskId(), "系统没有找到与activityId="
							+ thisTaskInst.getActivityId() 
							+ "对应activityInstance，无法执行abort操作。");
		}

		if (targetActivityInstance != null) {
			((TaskInstance) thisTaskInst)
					.setTargetActivityId(targetActivityInstance.getActivity()
							.getId());
		}


		// 第一步，首先Abort当前taskInstance
		persistenceService.abortTaskInstance((TaskInstance) thisTaskInst);

		// 触发相应的事件
		TaskInstanceEvent e = new TaskInstanceEvent();
		e.setSource(thisTaskInst);
		e.setWorkflowSession(currentSession);
		e.setProcessInstance(processInstance);
		e.setEventType(TaskInstanceEvent.AFTER_TASK_INSTANCE_COMPLETE);
		if (this.defaultTaskInstanceEventListener != null) {
			this.defaultTaskInstanceEventListener.onTaskInstanceEventFired(e);
		}

		this.fireTaskInstanceEvent(thisTaskInst, e);

		// 第二步，检查ActivityInstance是否可以结束
		if (!activityInstanceCanBeCompleted(thisTaskInst)) {
			return;
		}

		// 第三步，尝试结束对应的activityInstance
		List<IToken> tokens = persistenceService.findTokensForProcessInstance(
				thisTaskInst.getProcessInstanceId(), thisTaskInst
						.getActivityId());
		// System.out.println("Inside TaskInstance.complete(targetActivityInstance):: tokens.size is "+tokens.size());
		if (tokens == null || tokens.size() == 0) {
			return;// 表明activityInstance已经结束了。
		}
		if (tokens.size() > 1) {
			WorkflowProcess process = thisTaskInst.getWorkflowProcess();
			throw new EngineException(thisTaskInst.getProcessInstanceId(),
					process, thisTaskInst.getTaskId(), "与activityId="
							+ thisTaskInst.getActivityId() + "对应的token数量(="
							+ tokens.size() + ")不正确，正确只能为1，因此无法完成complete操作");
		}
		IToken token = tokens.get(0);
		// stepNumber不相等，不允许执行结束操作。
		if (token.getStepNumber().intValue() != thisTaskInst.getStepNumber()
				.intValue()) {
			return;
		}
		if (token.isAlive() == false) {
			WorkflowProcess process = thisTaskInst.getWorkflowProcess();
			throw new EngineException(thisTaskInst.getProcessInstanceId(),
					process, thisTaskInst.getTaskId(), "与activityId="
							+ thisTaskInst.getActivityId()
							+ "对应的token.alive=false，因此无法完成complete操作");
		}

		token.setProcessInstance(processInstance);

		//调整token布局
		if (targetActivityId != null) {
			List<Synchronizer> allSynchronizersAndEnds = new ArrayList<Synchronizer>(); 
			allSynchronizersAndEnds.addAll(workflowProcess.getSynchronizers());
			allSynchronizersAndEnds.addAll(workflowProcess.getEndNodes());
			for (int i=0;i<allSynchronizersAndEnds.size();i++){
				Synchronizer synchronizer = allSynchronizersAndEnds.get(i);
				if (synchronizer.getName().equals("Synchronizer4")){
					System.out.println(synchronizer.getName());
				}
				int volumn = 0;
				if (synchronizer instanceof EndNode){
					volumn = synchronizer.getEnteringTransitions().size();
				}else{
					volumn = synchronizer.getEnteringTransitions().size()*synchronizer.getLeavingTransitions().size();
				}			
				IToken tokenTmp =  new Token();
				tokenTmp.setNodeId(synchronizer.getId());
				tokenTmp.setAlive(false);
				tokenTmp.setProcessInstanceId(thisTaskInst.getProcessInstanceId());
				tokenTmp.setStepNumber(-1);

				List<String> incomingTransitionIds = new ArrayList<String>();
				boolean reachable = false;
				List<Transition> enteringTrans = synchronizer.getEnteringTransitions();			
				for (int m=0;m<aliveActivityIdsAfterJump.size();m++){
					String aliveActivityId = aliveActivityIdsAfterJump.get(m);
					if (workflowProcess.isReachable(aliveActivityId, synchronizer.getId())){					
						Transition trans = null;
						reachable = true;
						for (int j=0;j<enteringTrans.size();j++){
							trans = enteringTrans.get(j);
							Node fromNode = (Node)trans.getFromNode();
							if (workflowProcess.isReachable(aliveActivityId, fromNode.getId())){
								if (!incomingTransitionIds.contains(trans.getId())){
									incomingTransitionIds.add(trans.getId());
								}
							}
						}			
					}
				}
				if (reachable){
					tokenTmp.setValue(volumn-(incomingTransitionIds.size()*volumn/enteringTrans.size()));	
					
					IToken virtualToken = getJoinInfo(allTokens,synchronizer.getId());
					
					if (virtualToken!=null){
						persistenceService.deleteTokensForNode(thisTaskInst.getProcessInstanceId(), synchronizer.getId());
					}
					
					if (tokenTmp.getValue()!=0){
						tokenTmp.setProcessInstance(processInstance);
						persistenceService.saveOrUpdateToken(tokenTmp);
					}
				}
			}
		}
		thisActivityInstance.complete(token, targetActivityInstance);

	}
	
	/**
	 * 触发task instance相关的事件
	 * 
	 * @param e
	 * @throws org.fireflow.engine.EngineException
	 */
	protected final void fireTaskInstanceEvent(ITaskInstance taskInstance,
			TaskInstanceEvent e) throws EngineException {
		Task task = taskInstance.getTask();
		if (task == null) {
			return;
		}

		List<EventListener> listeners = task.getEventListeners();
		for (int i = 0; i < listeners.size(); i++) {
			EventListener listener = listeners.get(i);
			Object obj = rtCtx.getBeanByName(listener.getClassName());
			if (obj != null && (obj instanceof ITaskInstanceEventListener)) {
				((ITaskInstanceEventListener) obj).onTaskInstanceEventFired(e);
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.taskinstance.ITaskInstanceManager#createWorkItem(org.fireflow.engine.IWorkflowSession, org.fireflow.engine.IProcessInstance, org.fireflow.engine.ITaskInstance, java.lang.String)
	 */
	public final WorkItem createWorkItem(IWorkflowSession currentSession,
			IProcessInstance processInstance, ITaskInstance taskInstance,
			String actorId) throws EngineException {
		IPersistenceService persistenceService = rtCtx.getPersistenceService();

		WorkItem wi = new WorkItem();
		wi.setTaskInstance(taskInstance);
		wi.setActorId(actorId);
		wi.setState(IWorkItem.INITIALIZED);
		wi.setCreatedTime(rtCtx.getCalendarService().getSysDate());
		wi.setRuntimeContext(rtCtx);
		wi.setCurrentWorkflowSession(currentSession);
		// 保存到数据库
		persistenceService.saveOrUpdateWorkItem(wi);

		// 触发事件
		// 触发相应的事件
		TaskInstanceEvent e = new TaskInstanceEvent();
		e.setSource(taskInstance);
		e.setWorkItem(wi);
		e.setWorkflowSession(currentSession);
		e.setProcessInstance(processInstance);

		e.setEventType(TaskInstanceEvent.AFTER_WORKITEM_CREATED);
		if (this.defaultTaskInstanceEventListener != null) {
			this.defaultTaskInstanceEventListener.onTaskInstanceEventFired(e);
		}
		this.fireTaskInstanceEvent(taskInstance, e);

		return wi;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.taskinstance.ITaskInstanceManager#claimWorkItem(java.lang.String, java.lang.String)
	 */
	public final IWorkItem claimWorkItem(String workItemId,
			String taskInstanceId) throws EngineException, KernelException {
		IPersistenceService persistenceService = rtCtx.getPersistenceService();

		persistenceService.lockTaskInstance(taskInstanceId);

		IWorkItem workItem = persistenceService.findWorkItemById(workItemId);

		if (workItem == null)
			return null;

		if (workItem.getState().intValue() != IWorkItem.INITIALIZED) {
			TaskInstance thisTaskInst = (TaskInstance) workItem
					.getTaskInstance();
			throw new EngineException(thisTaskInst.getProcessInstanceId(),
					thisTaskInst.getWorkflowProcess(),
					thisTaskInst.getTaskId(),
					"Claim work item failed. The state of the work item is "
							+ workItem.getState());
		}
		if (workItem.getTaskInstance().getState().intValue() != ITaskInstance.INITIALIZED
				&& workItem.getTaskInstance().getState().intValue() != ITaskInstance.RUNNING) {
			TaskInstance thisTaskInst = (TaskInstance) workItem
					.getTaskInstance();
			throw new EngineException(thisTaskInst.getProcessInstanceId(),
					thisTaskInst.getWorkflowProcess(),
					thisTaskInst.getTaskId(),
					"Claim work item failed .The state of the correspond task instance is "
							+ workItem.getTaskInstance().getState());
		}

		if (workItem.getTaskInstance().isSuspended()) {
			TaskInstance thisTaskInst = (TaskInstance) workItem
					.getTaskInstance();
			throw new EngineException(thisTaskInst.getProcessInstanceId(),
					thisTaskInst.getWorkflowProcess(),
					thisTaskInst.getTaskId(),
					"Claim work item failed .The  correspond task instance is suspended");
		}

		// 0、首先修改workitem的状态
		((WorkItem) workItem).setState(IWorkItem.RUNNING);
		((WorkItem) workItem).setClaimedTime(rtCtx.getCalendarService()
				.getSysDate());
		persistenceService.saveOrUpdateWorkItem(workItem);

		// 1、如果不是会签，则删除其他的workitem
		if (FormTask.ANY.equals(workItem.getTaskInstance()
				.getAssignmentStrategy())) {
			persistenceService.deleteWorkItemsInInitializedState(workItem
					.getTaskInstance().getId());
		}

		// 2、将TaskInstance的canBeWithdrawn字段改称false。即不允许被撤销
		TaskInstance taskInstance = (TaskInstance) workItem.getTaskInstance();
		taskInstance.setCanBeWithdrawn(false);
		persistenceService.saveOrUpdateTaskInstance(taskInstance);

		return workItem;

	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.taskinstance.ITaskInstanceManager#completeWorkItem(org.fireflow.engine.IWorkItem, org.fireflow.kernel.IActivityInstance, java.lang.String)
	 */
	public void completeWorkItem(IWorkItem workItem,
			IActivityInstance targetActivityInstance, String comments)
			throws EngineException, KernelException {
		if (workItem.getState().intValue() != IWorkItem.RUNNING) {
			TaskInstance thisTaskInst = (TaskInstance) workItem
					.getTaskInstance();
			// System.out.println("WorkItem的当前状态为"+this.getState()+"，不可以执行complete操作。");
			throw new EngineException(thisTaskInst.getProcessInstanceId(),
					thisTaskInst.getWorkflowProcess(),
					thisTaskInst.getTaskId(),
					"Complete work item failed . The state of the work item [id="
							+ workItem.getId() + "] is " + workItem.getState());
		}

		if (workItem.getTaskInstance().isSuspended()) {
			TaskInstance thisTaskInst = (TaskInstance) workItem
					.getTaskInstance();
			WorkflowProcess process = thisTaskInst.getWorkflowProcess();
			throw new EngineException(thisTaskInst.getProcessInstanceId(),
					process, thisTaskInst.getTaskId(),
					"Complete work item failed. The correspond task instance [id="
							+ thisTaskInst.getId() + "] is suspended");
		}

		IPersistenceService persistenceService = rtCtx.getPersistenceService();

		((WorkItem) workItem).setComments(comments);
		((WorkItem) workItem).setState(IWorkItem.COMPLETED);
		((WorkItem) workItem).setEndTime(rtCtx.getCalendarService()
				.getSysDate());
		persistenceService.saveOrUpdateWorkItem(workItem);

		// 触发AFTER_WORKITEM_COMPLETE事件
		TaskInstanceEvent e = new TaskInstanceEvent();
		e.setSource(workItem.getTaskInstance());
		e.setWorkflowSession(((IWorkflowSessionAware) workItem)
				.getCurrentWorkflowSession());
		e.setProcessInstance(((TaskInstance) workItem.getTaskInstance())
				.getAliveProcessInstance());
		e.setEventType(TaskInstanceEvent.AFTER_WORKITEM_COMPLETE);
		if (this.defaultTaskInstanceEventListener != null) {
			this.defaultTaskInstanceEventListener.onTaskInstanceEventFired(e);
		}

		this.fireTaskInstanceEvent(workItem.getTaskInstance(), e);

		((TaskInstance) workItem.getTaskInstance())
				.complete(targetActivityInstance);
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.taskinstance.ITaskInstanceManager#completeWorkItemAndJumpTo(org.fireflow.engine.IWorkItem, java.lang.String, java.lang.String)
	 */
	public void completeWorkItemAndJumpTo(IWorkItem workItem,
			String targetActivityId, String comments) throws EngineException,
			KernelException {
		// 首先检查是否可以正确跳转
		// 1）检查是否在同一个“执行线”上
		WorkflowProcess workflowProcess = workItem.getTaskInstance()
				.getWorkflowProcess();
		String thisActivityId = workItem.getTaskInstance().getActivityId();
		boolean isInSameLine = workflowProcess.isInSameLine(thisActivityId,
				targetActivityId);
		TaskInstance thisTaskInst = (TaskInstance) workItem.getTaskInstance();
		if (!isInSameLine) {
			throw new EngineException(
					thisTaskInst.getProcessInstanceId(),
					thisTaskInst.getWorkflowProcess(),
					thisTaskInst.getTaskId(),
					"Jumpto refused because of the current activitgy and the target activity are NOT in the same 'Execution Thread'.");
		}

		// 2）检查目标Activity Form Task的数量(暂时关闭该检查项目)
		// Activity targetActivity =
		// (Activity)workflowProcess.findWFElementById(activityId);
		// int count = getFormTaskCount(targetActivity);
		// if (count!=1){
		// if (!isInSameLine) throw new
		// EngineException("Jumpto refused because of the  FORM-type-task count of the target activitgy  is NOT 1; the count is "+count);
		// }

		// 3)检查当前的 taskinstance是否可以结束
		IPersistenceService persistenceService = rtCtx.getPersistenceService();

		Integer aliveWorkItemCount = persistenceService
				.getAliveWorkItemCountForTaskInstance(thisTaskInst.getId());
		if (aliveWorkItemCount != null && aliveWorkItemCount > 1) {
			throw new EngineException(
					thisTaskInst.getProcessInstanceId(),
					thisTaskInst.getWorkflowProcess(),
					thisTaskInst.getTaskId(),
					"Jumpto refused because of current taskinstance can NOT be completed. some workitem of this taskinstance is in runing state or initialized state");

		}

		// 4)检查当前的activity instance是否可以结束
		if (Activity.ALL.equals(workItem.getTaskInstance().getActivity()
				.getCompletionStrategy())) {

			Integer aliveTaskInstanceCount4ThisActivity = persistenceService
					.getAliveTaskInstanceCountForActivity(workItem
							.getTaskInstance().getProcessInstanceId(), workItem
							.getTaskInstance().getActivityId());
			if (aliveTaskInstanceCount4ThisActivity.intValue() > 1) {// 大于2表明当前Activity不可以complete
				throw new EngineException(
						thisTaskInst.getProcessInstanceId(),
						thisTaskInst.getWorkflowProcess(),
						thisTaskInst.getTaskId(),
						"Jumpto refused because of current activity instance can NOT be completed. some task instance of this activity instance is in runing state or initialized state");
			}
		}

		INetInstance netInstance = rtCtx.getKernelManager().getNetInstance(
				workflowProcess.getId(),
				workItem.getTaskInstance().getVersion());
		if (netInstance == null) {
			throw new EngineException(thisTaskInst.getProcessInstanceId(),
					thisTaskInst.getWorkflowProcess(),
					thisTaskInst.getTaskId(),
					"Not find the net instance for workflow process [id="
							+ workflowProcess.getId() + ", version="
							+ workItem.getTaskInstance().getVersion() + "]");
		}
		Object obj = netInstance.getWFElementInstance(targetActivityId);
		IActivityInstance targetActivityInstance = (IActivityInstance) obj;
		if (targetActivityInstance == null) {
			throw new EngineException(thisTaskInst.getProcessInstanceId(),
					thisTaskInst.getWorkflowProcess(),
					thisTaskInst.getTaskId(),
					"Not find the activity instance  for activity[process id="
							+ workflowProcess.getId() + ", version="
							+ workItem.getTaskInstance().getVersion()
							+ ",activity id=" + targetActivityId + "]");
		}

		if (rtCtx.isEnableTrace()) {

			ProcessInstanceTrace trace = new ProcessInstanceTrace();
			trace.setProcessInstanceId(workItem.getTaskInstance()
					.getProcessInstanceId());
			trace.setStepNumber(workItem.getTaskInstance().getStepNumber() + 1);
			trace.setType(ProcessInstanceTrace.JUMPTO_TYPE);
			trace.setFromNodeId(workItem.getTaskInstance().getActivityId());
			trace.setToNodeId(targetActivityId);
			trace.setEdgeId("");
			rtCtx.getPersistenceService().saveOrUpdateProcessInstanceTrace(
					trace);
		}

		this.completeWorkItem(workItem, targetActivityInstance, comments);
	}

	/**
	 * 自由流方法扩展，去除“同一执行线”的限制
	 */
	public void completeWorkItemAndJumpToEx(IWorkItem workItem,
			String targetActivityId, String comments) throws EngineException,
			KernelException {
		
		// 首先检查是否可以正确跳转	

		WorkflowProcess workflowProcess = workItem.getTaskInstance().getWorkflowProcess();
		String thisActivityId = workItem.getTaskInstance().getActivityId();
		TaskInstance thisTaskInst = (TaskInstance) workItem.getTaskInstance();
		//如果是在同一条执行线上，那么可以直接跳过去，只是重复判断了是否在同一条执行线上
		boolean isInSameLine = workflowProcess.isInSameLine(thisActivityId,targetActivityId);	
		if (isInSameLine){
			this.completeWorkItemAndJumpTo(workItem, targetActivityId, comments);
			return;
		}
		
		// 1）检查是否在同一个“执行线”上(关闭该检查，20091002)		
//		if (!isInSameLine) {
//			throw new EngineException(
//					thisTaskInst.getProcessInstanceId(),
//					thisTaskInst.getWorkflowProcess(),
//					thisTaskInst.getTaskId(),
//					"Jumpto refused because of the current activitgy and the target activity are NOT in the same 'Execution Thread'.");
//		}

		// 2）检查目标Activity Form Task的数量(暂时关闭该检查项目)
		// Activity targetActivity =
		// (Activity)workflowProcess.findWFElementById(activityId);
		// int count = getFormTaskCount(targetActivity);
		// if (count!=1){
		// if (!isInSameLine) throw new
		// EngineException("Jumpto refused because of the  FORM-type-task count of the target activitgy  is NOT 1; the count is "+count);
		// }

		// 3)检查当前的 taskinstance是否可以结束
		IPersistenceService persistenceService = rtCtx.getPersistenceService();

		Integer aliveWorkItemCount = persistenceService
				.getAliveWorkItemCountForTaskInstance(thisTaskInst.getId());
		if (aliveWorkItemCount != null && aliveWorkItemCount > 1) {
			throw new EngineException(
					thisTaskInst.getProcessInstanceId(),
					thisTaskInst.getWorkflowProcess(),
					thisTaskInst.getTaskId(),
					"Jumpto refused because of current taskinstance can NOT be completed. some workitem of this taskinstance is in runing state or initialized state");

		}

		// 4)检查当前的activity instance是否可以结束
		if (Activity.ALL.equals(workItem.getTaskInstance().getActivity().getCompletionStrategy())) {

			Integer aliveTaskInstanceCount4ThisActivity = persistenceService
					.getAliveTaskInstanceCountForActivity(workItem
							.getTaskInstance().getProcessInstanceId(), workItem
							.getTaskInstance().getActivityId());
			if (aliveTaskInstanceCount4ThisActivity.intValue() > 1) {// 大于1表明当前Activity不可以complete
				throw new EngineException(
						thisTaskInst.getProcessInstanceId(),
						thisTaskInst.getWorkflowProcess(),
						thisTaskInst.getTaskId(),
						"Jumpto refused because of current activity instance can NOT be completed. some task instance of this activity instance is in runing state or initialized state");
			}
		}
		
		//4)首先检查目标状态M是否存在冲突,如果存在冲突则不允许跳转；如果不存在冲突，则需要调整token
		List<IToken> allTokens = persistenceService.findTokensForProcessInstance(thisTaskInst.getProcessInstanceId(), null);
		WorkflowProcess thisProcess = thisTaskInst.getWorkflowProcess();//找到当前的工作里模型
		List<String> aliveActivityIdsAfterJump = new ArrayList<String>();//计算跳转后，哪些activity节点复活
		aliveActivityIdsAfterJump.add(targetActivityId);
		
		for (int i=0;allTokens!=null && i<allTokens.size();i++){
			IToken tokenTmp = allTokens.get(i);
			IWFElement workflowElement = thisProcess.findWFElementById(tokenTmp.getNodeId()); //找到拥有此token的工作流元素
			if ((workflowElement instanceof Activity) && !workflowElement.getId().equals(thisActivityId)){
				//注意：不能自己跳转到自己，同时此工作流元素是activity类型
				aliveActivityIdsAfterJump.add(workflowElement.getId());
				
				if (thisProcess.isReachable(targetActivityId, workflowElement.getId()) 
					|| thisProcess.isReachable(workflowElement.getId(), targetActivityId)){
					throw new EngineException(
							thisTaskInst.getProcessInstanceId(),
							thisTaskInst.getWorkflowProcess(),
							thisTaskInst.getTaskId(),
							"Jumpto refused because of the business-logic conflict!");

				}
			}
		}

		//所有检查结束，开始执行跳转操作
		
		INetInstance netInstance = rtCtx.getKernelManager().getNetInstance(
				workflowProcess.getId(),
				workItem.getTaskInstance().getVersion());
		if (netInstance == null) {
			throw new EngineException(thisTaskInst.getProcessInstanceId(),
					thisTaskInst.getWorkflowProcess(),
					thisTaskInst.getTaskId(),
					"Not find the net instance for workflow process [id="
							+ workflowProcess.getId() + ", version="
							+ workItem.getTaskInstance().getVersion() + "]");
		}
		Object obj = netInstance.getWFElementInstance(targetActivityId);
		IActivityInstance targetActivityInstance = (IActivityInstance) obj;
		if (targetActivityInstance == null) {
			throw new EngineException(thisTaskInst.getProcessInstanceId(),
					thisTaskInst.getWorkflowProcess(),
					thisTaskInst.getTaskId(),
					"Not find the activity instance  for activity[process id="
							+ workflowProcess.getId() + ", version="
							+ workItem.getTaskInstance().getVersion()
							+ ",activity id=" + targetActivityId + "]");
		}

		if (rtCtx.isEnableTrace()) {

			ProcessInstanceTrace trace = new ProcessInstanceTrace();
			trace.setProcessInstanceId(workItem.getTaskInstance()
					.getProcessInstanceId());
			trace.setStepNumber(workItem.getTaskInstance().getStepNumber() + 1);
			trace.setType(ProcessInstanceTrace.JUMPTO_TYPE);
			trace.setFromNodeId(workItem.getTaskInstance().getActivityId());
			trace.setToNodeId(targetActivityId);
			trace.setEdgeId("");
			rtCtx.getPersistenceService().saveOrUpdateProcessInstanceTrace(
					trace);
		}

		//调整token布局 
		List<Synchronizer> allSynchronizersAndEnds = new ArrayList<Synchronizer>(); 
		allSynchronizersAndEnds.addAll(thisProcess.getSynchronizers());
		allSynchronizersAndEnds.addAll(thisProcess.getEndNodes());
		for (int i=0;i<allSynchronizersAndEnds.size();i++){
			Synchronizer synchronizer = allSynchronizersAndEnds.get(i);

			int volumn = 0;
			if (synchronizer instanceof EndNode){
				volumn = synchronizer.getEnteringTransitions().size();
			}else{
				volumn = synchronizer.getEnteringTransitions().size()*synchronizer.getLeavingTransitions().size();
			}			
			IToken tokenTmp =  new Token();
			tokenTmp.setNodeId(synchronizer.getId());
			tokenTmp.setAlive(false);
			tokenTmp.setProcessInstanceId(thisTaskInst.getProcessInstanceId());
			tokenTmp.setStepNumber(-1);

			List<String> incomingTransitionIds = new ArrayList<String>();
			boolean reachable = false;
			List<Transition> enteringTrans = synchronizer.getEnteringTransitions();		
			for (int m=0;m<aliveActivityIdsAfterJump.size();m++){
				String aliveActivityId = aliveActivityIdsAfterJump.get(m);
				if (thisProcess.isReachable(aliveActivityId, synchronizer.getId())){					
					Transition trans = null;
					reachable = true;
					for (int j=0;j<enteringTrans.size();j++){
						trans = enteringTrans.get(j);
						Node fromNode = trans.getFromNode();
						if (thisProcess.isReachable(aliveActivityId, fromNode.getId())){
							if (!incomingTransitionIds.contains(trans.getId())){
								incomingTransitionIds.add(trans.getId());
							}
						}
					}			
				}
			}
			if (reachable){
				tokenTmp.setValue(volumn-(incomingTransitionIds.size()*volumn/enteringTrans.size()));	
				
				IToken virtualToken = getJoinInfo(allTokens,synchronizer.getId()); //获取一个虚拟的综合性token
				
				if (virtualToken!=null){
					persistenceService.deleteTokensForNode(thisTaskInst.getProcessInstanceId(), synchronizer.getId());
				}
				
				if (tokenTmp.getValue()!=0){
					tokenTmp.setProcessInstance(thisTaskInst.getAliveProcessInstance());
					persistenceService.saveOrUpdateToken(tokenTmp);
				}
			}
		}
		
		this.completeWorkItem(workItem, targetActivityInstance, comments);
	}
	
	/**
	 * 获取特定同步器的汇聚信息，如果该同步器已经存在token，则返回一个综合性的虚拟的token，否则返回null。
	 * @param allTokens
	 * @param synchronizerId
	 * @return
	 */
	private IToken getJoinInfo(List<IToken> allTokens,String synchronizerId){
		boolean findTokens = false;
		Map<String,IToken> tokensMap = new HashMap<String,IToken>();
        for (int i = 0; i < allTokens.size(); i++) {
            IToken tmpToken = (IToken) allTokens.get(i);
            if (!tmpToken.getNodeId().equals(synchronizerId)){
            	continue;
            }
            findTokens = true;
            String tmpFromActivityId = tmpToken.getFromActivityId();
            if (!tokensMap.containsKey(tmpFromActivityId)) {
                tokensMap.put(tmpFromActivityId, tmpToken);
            } else {
                IToken tmpToken2 = (IToken) tokensMap.get(tmpFromActivityId);
                if (tmpToken2.getStepNumber() > tmpToken.getStepNumber()) {
                    tokensMap.put(tmpFromActivityId, tmpToken2);
                }
            }
        }

        if (!findTokens) return null;
        IToken virtualToken = new Token();
        int stepNumber = 0;
        List<IToken> tokensList = new ArrayList<IToken>(tokensMap.values());

        for (int i = 0; i < tokensList.size(); i++) {
            IToken _token = (IToken) tokensList.get(i);
            //Fixed by wmj2003  http://www.fireflow.org/viewthread.php?tid=1040&extra=page%3D1
            virtualToken.setValue(virtualToken.getValue()==null?0:virtualToken.getValue()+_token.getValue());
            if (_token.isAlive()) {
            	virtualToken.setAlive(true);
                String oldFromActivityId = virtualToken.getFromActivityId();
                if (oldFromActivityId == null || oldFromActivityId.trim().equals("")) {
                	virtualToken.setFromActivityId(_token.getFromActivityId());
                } else {
                	virtualToken.setFromActivityId(oldFromActivityId + IToken.FROM_ACTIVITY_ID_SEPARATOR + _token.getFromActivityId());
                }
            }
            if (_token.getStepNumber() > stepNumber) {
                stepNumber = _token.getStepNumber();
            }
        }

        virtualToken.setStepNumber(stepNumber + 1);
        
        return virtualToken;
	} 
	
	/* (non-Javadoc)
	 * @see org.fireflow.engine.taskinstance.ITaskInstanceManager#rejectWorkItem(org.fireflow.engine.IWorkItem, java.lang.String)
	 */
	public void rejectWorkItem(IWorkItem workItem, String comments)
			throws EngineException, KernelException {
		Activity thisActivity = workItem.getTaskInstance().getActivity();
		TaskInstance thisTaskInstance = (TaskInstance) workItem
				.getTaskInstance();
		if (workItem.getState() > 5 || workItem.getTaskInstance().isSuspended()) {// 处于非活动状态,或者被挂起,则不允许reject
			throw new EngineException(
					thisTaskInstance.getProcessInstanceId(),
					thisTaskInstance.getWorkflowProcess(),
					thisTaskInstance.getTaskId(),
					"Reject operation refused!Current work item is completed or the correspond task instance is suspended!!");
		}
		// 当前Activity只允许一个Form类型的Task
		if (thisActivity.getTasks().size() > 1) {
			throw new EngineException(thisTaskInstance.getProcessInstanceId(),
					thisTaskInstance.getWorkflowProcess(), thisTaskInstance
							.getTaskId(),
					"Reject operation refused!The correspond activity has more than 1 tasks");
		}
		// 汇签Task不允许Reject
		if (FormTask.ALL.equals(thisTaskInstance.getAssignmentStrategy())) {
			throw new EngineException(thisTaskInstance.getProcessInstanceId(),
					thisTaskInstance.getWorkflowProcess(), thisTaskInstance
							.getTaskId(),
					"Reject operation refused!The assignment strategy is 'ALL'");
		}
        //----added by wmj2003 20090915 ---start---
        //处理拒收的边界问题
        if(thisTaskInstance.getFromActivityId().equals(IToken.FROM_START_NODE)){
                throw new EngineException(
                                thisTaskInstance.getProcessInstanceId(),
                                thisTaskInstance.getWorkflowProcess(),
                                thisTaskInstance.getTaskId(),
                                "Reject operation refused!Because the from activityId equals "+IToken.FROM_START_NODE );
        }
        //----added by wmj2003 20090915 ---end---
		IPersistenceService persistenceService = this.rtCtx
				.getPersistenceService();
		List<ITaskInstance> siblingTaskInstancesList = null;

		siblingTaskInstancesList = persistenceService
				.findTaskInstancesForProcessInstanceByStepNumber(workItem
						.getTaskInstance().getProcessInstanceId(),
						thisTaskInstance.getStepNumber());

		// 如果执行了split操作，则不允许reject
		if (siblingTaskInstancesList.size() > 1) {
			throw new EngineException(
					thisTaskInstance.getProcessInstanceId(),
					thisTaskInstance.getWorkflowProcess(),
					thisTaskInstance.getTaskId(),
					"Reject operation refused!Because the process instance has taken a split operation.");
		}

		// 检查From Activity中是否有ToolTask和SubflowTask
		List<String> fromActivityIdList = new ArrayList<String>();
		StringTokenizer tokenizer = new StringTokenizer(thisTaskInstance
				.getFromActivityId(), IToken.FROM_ACTIVITY_ID_SEPARATOR);
		while (tokenizer.hasMoreTokens()) {
			fromActivityIdList.add(tokenizer.nextToken());
		}
		WorkflowProcess workflowProcess = workItem.getTaskInstance()
				.getWorkflowProcess();
		for (int i = 0; i < fromActivityIdList.size(); i++) {
			String fromActivityId = (String) fromActivityIdList.get(i);
			Activity fromActivity = (Activity) workflowProcess
					.findWFElementById(fromActivityId);
			List<Task> fromTaskList = fromActivity.getTasks();
			for (int j = 0; j < fromTaskList.size(); j++) {
				Task task =  fromTaskList.get(j);
				if (Task.TOOL.equals(task.getType())
						|| Task.SUBFLOW.equals(task.getType())) {
					throw new EngineException(
							thisTaskInstance.getProcessInstanceId(),
							thisTaskInstance.getWorkflowProcess(),
							thisTaskInstance.getTaskId(),
							"Reject operation refused!The previous activity contains tool-task or subflow-task");

				}
			}
		}
		// 恢复所有的FromTaskInstance
		INetInstance netInstance = rtCtx.getKernelManager().getNetInstance(
				workflowProcess.getId(),
				workItem.getTaskInstance().getVersion());
		if (netInstance == null) {
			throw new EngineException(thisTaskInstance.getProcessInstanceId(),
					thisTaskInstance.getWorkflowProcess(), thisTaskInstance
							.getTaskId(),
					"Not find the net instance for workflow process [id="
							+ workflowProcess.getId() + ", version="
							+ workItem.getTaskInstance().getVersion() + "]");
		}

		// 执行reject操作。

		IWorkflowSession session = ((IWorkflowSessionAware) workItem)
				.getCurrentWorkflowSession();
		session.setWithdrawOrRejectOperationFlag(true);
		int newStepNumber = thisTaskInstance.getStepNumber() + 1;
		try {
			// 首先将本WorkItem和TaskInstance cancel掉。
			workItem.setComments(comments);
			((WorkItem) workItem).setState(IWorkItem.CANCELED);
			((WorkItem) workItem).setEndTime(rtCtx.getCalendarService()
					.getSysDate());
			rtCtx.getPersistenceService().saveOrUpdateWorkItem(workItem);

			persistenceService.abortTaskInstance(thisTaskInstance);

			// 删除本环节的token
			persistenceService.deleteTokensForNode(thisTaskInstance
					.getProcessInstanceId(), thisTaskInstance.getActivityId());

			IActivityInstance fromActivityInstance = null;
			for (int i = 0; i < fromActivityIdList.size(); i++) {
				String fromActivityId = (String) fromActivityIdList.get(i);
				Object obj = netInstance.getWFElementInstance(fromActivityId);
				fromActivityInstance = (IActivityInstance) obj;
				Token newToken = new Token();
				((Token) newToken).setAlive(true);
				((Token) newToken).setNodeId(fromActivityId);
				newToken.setProcessInstanceId(thisTaskInstance
						.getProcessInstanceId());
				newToken.setProcessInstance(((TaskInstance) thisTaskInstance)
						.getAliveProcessInstance());
				newToken.setFromActivityId(thisTaskInstance.getActivityId());
				newToken.setStepNumber(newStepNumber);
				newToken.setValue(0);
				persistenceService.saveOrUpdateToken(newToken);

				this.createTaskInstances(newToken, fromActivityInstance);

				if (rtCtx.isEnableTrace()) {
					ProcessInstanceTrace trace = new ProcessInstanceTrace();
					trace.setProcessInstanceId(thisTaskInstance
							.getProcessInstanceId());
					trace.setStepNumber(newStepNumber);
					trace.setType(ProcessInstanceTrace.REJECT_TYPE);
					trace.setFromNodeId(thisActivity.getId());
					trace.setToNodeId(fromActivityId);
					trace.setEdgeId("");
					rtCtx.getPersistenceService()
							.saveOrUpdateProcessInstanceTrace(trace);
				}
			}

			ITransitionInstance theLeavingTransitionInstance = (ITransitionInstance) fromActivityInstance
					.getLeavingTransitionInstances().get(0);
			ISynchronizerInstance synchronizerInstance = (ISynchronizerInstance) theLeavingTransitionInstance
					.getLeavingNodeInstance();
			if (synchronizerInstance.getEnteringTransitionInstances().size() > fromActivityIdList
					.size()) {
				Token supplementToken = new Token();
				((Token) supplementToken).setAlive(false);
				((Token) supplementToken).setNodeId(synchronizerInstance
						.getSynchronizer().getId());
				supplementToken.setProcessInstanceId(thisTaskInstance
						.getProcessInstanceId());
				supplementToken
						.setProcessInstance(((TaskInstance) thisTaskInstance)
								.getAliveProcessInstance());
				supplementToken.setFromActivityId("EMPTY(created by reject)");
				supplementToken
						.setStepNumber(thisTaskInstance.getStepNumber() + 1);
				supplementToken.setValue(synchronizerInstance.getVolume()
						- theLeavingTransitionInstance.getWeight()
						* fromActivityIdList.size());
				persistenceService.saveOrUpdateToken(supplementToken);
			}
		} finally {
			session.setWithdrawOrRejectOperationFlag(false);
		}
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.taskinstance.ITaskInstanceManager#withdrawWorkItem(org.fireflow.engine.IWorkItem)
	 */
	public IWorkItem withdrawWorkItem(IWorkItem workItem)
			throws EngineException, KernelException {
		Activity thisActivity = workItem.getTaskInstance().getActivity();
		TaskInstance thisTaskInstance = (TaskInstance) workItem
				.getTaskInstance();
		if (workItem.getState() < 5) {// 小于5的状态为活动状态
			throw new EngineException(thisTaskInstance.getProcessInstanceId(),
					thisTaskInstance.getWorkflowProcess(), thisTaskInstance
							.getTaskId(),
					"Withdraw operation is refused! Current workitem is in running state!!");
		}
		// 当前Activity只允许一个Form类型的Task
		if (thisActivity.getTasks().size() > 1) {
			throw new EngineException(thisTaskInstance.getProcessInstanceId(),
					thisTaskInstance.getWorkflowProcess(), thisTaskInstance
							.getTaskId(),
					"Withdraw operation is refused! The activity[id="
							+ thisActivity.getId() + "] has more than 1 tasks");
		}

		// 汇签Task不允许撤销
		if (FormTask.ALL.equals(thisTaskInstance.getAssignmentStrategy())) {
			throw new EngineException(thisTaskInstance.getProcessInstanceId(),
					thisTaskInstance.getWorkflowProcess(), thisTaskInstance
							.getTaskId(),
					"Withdraw operation is refused! The assignment strategy for activity[id="
							+ thisActivity.getId() + "] is 'ALL'");
		}

		IPersistenceService persistenceService = this.rtCtx
				.getPersistenceService();
		List<ITaskInstance> targetTaskInstancesList = null;
		targetTaskInstancesList = persistenceService
				.findTaskInstancesForProcessInstanceByStepNumber(
						thisTaskInstance.getProcessInstanceId(),
						thisTaskInstance.getStepNumber() + 1);

		// 如果targetTaskInstancesList为空或size 等于0,则表示流程实例执行了汇聚操作。
		if (targetTaskInstancesList == null
				|| targetTaskInstancesList.size() == 0) {
			throw new EngineException(
					thisTaskInstance.getProcessInstanceId(),
					thisTaskInstance.getWorkflowProcess(),
					thisTaskInstance.getTaskId(),
					"Withdraw operation is refused!Because the process instance has taken a join operation after this activity[id="
							+ thisActivity.getId() + "].");
		} else {
			TaskInstance taskInstance = (TaskInstance) targetTaskInstancesList
					.get(0);
			if (!taskInstance.getFromActivityId().equals(
					thisTaskInstance.getActivityId())) {
				throw new EngineException(
						thisTaskInstance.getProcessInstanceId(),
						thisTaskInstance.getWorkflowProcess(),
						thisTaskInstance.getTaskId(),
						"Withdraw operation is refused!Because the process instance has taken a join operation after this activity[id="
								+ thisActivity.getId() + "].");
			}
		}

		for (int i = 0; targetTaskInstancesList != null
				&& i < targetTaskInstancesList.size(); i++) {
			TaskInstance targetTaskInstanceTmp = (TaskInstance) targetTaskInstancesList
					.get(i);
			if (!targetTaskInstanceTmp.getCanBeWithdrawn()) {
				// 说明已经有某些WorkItem处于已签收状态，或者已经处于完毕状态，此时不允许退回
				throw new EngineException(thisTaskInstance
						.getProcessInstanceId(), thisTaskInstance
						.getWorkflowProcess(), thisTaskInstance.getTaskId(),
						"Withdraw operation is refused! Some task instances of the  next activity[id="
								+ targetTaskInstanceTmp.getActivityId()
								+ "] are not in 'Initialized' state");
			}
		}

		INetInstance netInstance = rtCtx.getKernelManager().getNetInstance(
				thisTaskInstance.getProcessId(),
				workItem.getTaskInstance().getVersion());
		if (netInstance == null) {
			throw new EngineException(thisTaskInstance.getProcessInstanceId(),
					thisTaskInstance.getWorkflowProcess(), thisTaskInstance
							.getTaskId(),
					"Withdraw operation failed.Not find the net instance for workflow process [id="
							+ thisTaskInstance.getProcessId() + ", version="
							+ workItem.getTaskInstance().getVersion() + "]");
		}
		Object obj = netInstance.getWFElementInstance(thisTaskInstance
				.getActivityId());
		IActivityInstance thisActivityInstance = (IActivityInstance) obj;

		// 一切检查通过之后进行“收回”处理

		IWorkflowSession session = ((IWorkflowSessionAware) workItem)
				.getCurrentWorkflowSession();
		session.setWithdrawOrRejectOperationFlag(true);
		int newStepNumber = thisTaskInstance.getStepNumber() + 2;
		try {
			DynamicAssignmentHandler dynamicAssignmentHandler = new DynamicAssignmentHandler();
			List<String> actorIds = new ArrayList<String>();
			actorIds.add(workItem.getActorId());
			dynamicAssignmentHandler.setActorIdsList(actorIds);
			((WorkflowSession) session)
					.setCurrentDynamicAssignmentHandler(dynamicAssignmentHandler);

			// 1、首先将后续环节的TaskInstance极其workItem变成Canceled状态
			List<String> targetActivityIdList = new ArrayList<String>();
			StringBuffer theFromActivityIds = new StringBuffer("");
			for (int i = 0; i < targetTaskInstancesList.size(); i++) {
				TaskInstance taskInstTemp = (TaskInstance) targetTaskInstancesList
						.get(i);

				persistenceService.abortTaskInstance(taskInstTemp);

				if (!targetActivityIdList
						.contains(taskInstTemp.getActivityId())) {
					targetActivityIdList.add(taskInstTemp.getActivityId());
					if (theFromActivityIds.length() == 0) {
						theFromActivityIds.append(taskInstTemp.getActivityId());
					} else {
						theFromActivityIds.append(
								IToken.FROM_ACTIVITY_ID_SEPARATOR).append(
								taskInstTemp.getActivityId());
					}
				}
			}

			persistenceService.deleteTokensForNodes(thisTaskInstance
					.getProcessInstanceId(), targetActivityIdList);

			if (rtCtx.isEnableTrace()) {
				for (int i = 0; targetActivityIdList != null
						&& i < targetActivityIdList.size(); i++) {
					String tmpActId = (String) targetActivityIdList.get(i);
					ProcessInstanceTrace trace = new ProcessInstanceTrace();
					trace.setProcessInstanceId(thisTaskInstance
							.getProcessInstanceId());
					trace.setStepNumber(newStepNumber);
					trace.setType(ProcessInstanceTrace.WITHDRAW_TYPE);
					trace.setFromNodeId(tmpActId);
					trace.setToNodeId(thisActivity.getId());
					trace.setEdgeId("");
					rtCtx.getPersistenceService()
							.saveOrUpdateProcessInstanceTrace(trace);
				}
			}

			ITransitionInstance thisLeavingTransitionInstance = (ITransitionInstance) thisActivityInstance
					.getLeavingTransitionInstances().get(0);
			ISynchronizerInstance synchronizerInstance = (ISynchronizerInstance) thisLeavingTransitionInstance
					.getLeavingNodeInstance();
			if (synchronizerInstance.getEnteringTransitionInstances().size() > 1) {
				Token supplementToken = new Token();
				((Token) supplementToken).setAlive(false);
				((Token) supplementToken).setNodeId(synchronizerInstance
						.getSynchronizer().getId());
				supplementToken.setProcessInstanceId(thisTaskInstance
						.getProcessInstanceId());
				supplementToken
						.setProcessInstance(((TaskInstance) thisTaskInstance)
								.getAliveProcessInstance());
				supplementToken.setFromActivityId("Empty(created by withdraw)");
				supplementToken.setStepNumber(newStepNumber);
				supplementToken.setValue(synchronizerInstance.getVolume()
						- thisLeavingTransitionInstance.getWeight());
				persistenceService.saveOrUpdateToken(supplementToken);
			}

			Token newToken = new Token();
			((Token) newToken).setAlive(true);
			((Token) newToken).setNodeId(workItem.getTaskInstance()
					.getActivityId());
			newToken.setProcessInstanceId(thisTaskInstance
					.getProcessInstanceId());
			newToken.setProcessInstance(((TaskInstance) thisTaskInstance)
					.getAliveProcessInstance());
			newToken.setFromActivityId(theFromActivityIds.toString());
			newToken.setStepNumber(newStepNumber);
			newToken.setValue(0);
			persistenceService.saveOrUpdateToken(newToken);

			this.createTaskInstances(newToken, thisActivityInstance);

			List<IWorkItem>  workItems = persistenceService.findTodoWorkItems(workItem
					.getActorId(), workItem.getTaskInstance().getProcessId(),
					workItem.getTaskInstance().getTaskId());
			if (workItems == null || workItems.size() == 0) {
				throw new EngineException(thisTaskInstance
						.getProcessInstanceId(), thisTaskInstance
						.getWorkflowProcess(), thisTaskInstance.getTaskId(),
						"Withdraw operation failed.No work item has been created for Task[id="
								+ thisTaskInstance.getTaskId() + "]");
			}
			if (workItems.size() > 1) {
				throw new EngineException(
						thisTaskInstance.getProcessInstanceId(),
						thisTaskInstance.getWorkflowProcess(),
						thisTaskInstance.getTaskId(),
						"Withdraw operation failed.More than one work item have been created for Task[id="
								+ thisTaskInstance.getTaskId() + "]");

			}

			return (IWorkItem) workItems.get(0);
		} finally {
			session.setWithdrawOrRejectOperationFlag(false);
		}
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.taskinstance.ITaskInstanceManager#reassignWorkItemTo(org.fireflow.engine.IWorkItem, java.lang.String, java.lang.String)
	 */
	public IWorkItem reassignWorkItemTo(IWorkItem workItem, String actorId,
			String comments) {
		WorkItem newWorkItem = new WorkItem();
		BeanUtils.copyProperties(workItem, newWorkItem);
		newWorkItem.setId(null);
		newWorkItem.setActorId(actorId);
		newWorkItem.setCreatedTime(rtCtx.getCalendarService().getSysDate());
		rtCtx.getPersistenceService().saveOrUpdateWorkItem(newWorkItem);

		((WorkItem) workItem).setState(IWorkItem.CANCELED);
		((WorkItem) workItem).setEndTime(rtCtx.getCalendarService()
				.getSysDate());
		((WorkItem) workItem).setComments(comments);
		rtCtx.getPersistenceService().saveOrUpdateWorkItem(workItem);
		return newWorkItem;
	}

	/**
	 * 
	 * @return
	 */
	public ITaskInstanceCompletionEvaluator getDefaultFormTaskInstanceCompletionEvaluator() {
		return defaultFormTaskInstanceCompletionEvaluator;
	}

	/**
	 * 
	 * @param defaultFormTaskInstanceCompletionEvaluator
	 */
	public void setDefaultFormTaskInstanceCompletionEvaluator(
			ITaskInstanceCompletionEvaluator defaultFormTaskInstanceCompletionEvaluator) {
		this.defaultFormTaskInstanceCompletionEvaluator = defaultFormTaskInstanceCompletionEvaluator;
	}

	public ITaskInstanceRunner getDefaultFormTaskInstanceRunner() {
		return defaultFormTaskInstanceRunner;
	}

	public void setDefaultFormTaskInstanceRunner(
			ITaskInstanceRunner defaultFormTaskInstanceRunner) {
		this.defaultFormTaskInstanceRunner = defaultFormTaskInstanceRunner;
	}

	public ITaskInstanceCompletionEvaluator getDefaultSubflowTaskInstanceCompletionEvaluator() {
		return defaultSubflowTaskInstanceCompletionEvaluator;
	}

	public void setDefaultSubflowTaskInstanceCompletionEvaluator(
			ITaskInstanceCompletionEvaluator defaultSubflowTaskInstanceCompletionEvaluator) {
		this.defaultSubflowTaskInstanceCompletionEvaluator = defaultSubflowTaskInstanceCompletionEvaluator;
	}

	public ITaskInstanceRunner getDefaultSubflowTaskInstanceRunner() {
		return defaultSubflowTaskInstanceRunner;
	}

	public void setDefaultSubflowTaskInstanceRunner(
			ITaskInstanceRunner defaultSubflowTaskInstanceRunner) {
		this.defaultSubflowTaskInstanceRunner = defaultSubflowTaskInstanceRunner;
	}

	public ITaskInstanceCreator getDefaultTaskInstanceCreator() {
		return defaultTaskInstanceCreator;
	}

	public void setDefaultTaskInstanceCreator(
			ITaskInstanceCreator defaultTaskInstanceCreator) {
		this.defaultTaskInstanceCreator = defaultTaskInstanceCreator;
	}

	public ITaskInstanceCompletionEvaluator getDefaultToolTaskInstanceCompletionEvaluator() {
		return defaultToolTaskInstanceCompletionEvaluator;
	}

	public void setDefaultToolTaskInstanceCompletionEvaluator(
			ITaskInstanceCompletionEvaluator defaultToolTaskInstanceCompletionEvaluator) {
		this.defaultToolTaskInstanceCompletionEvaluator = defaultToolTaskInstanceCompletionEvaluator;
	}

	public ITaskInstanceRunner getDefaultToolTaskInstanceRunner() {
		return defaultToolTaskInstanceRunner;
	}

	public void setDefaultToolTaskInstanceRunner(
			ITaskInstanceRunner defaultToolTaskInstanceRunner) {
		this.defaultToolTaskInstanceRunner = defaultToolTaskInstanceRunner;
	}

	public ITaskInstanceEventListener getDefaultTaskInstanceEventListener() {
		return defaultTaskInstanceEventListener;
	}

	public void setDefaultTaskInstanceEventListener(
			ITaskInstanceEventListener defaultTaskInstanceEventListener) {
		this.defaultTaskInstanceEventListener = defaultTaskInstanceEventListener;
	}

}
