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
package org.fireflow.engine.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.fireflow.engine.EngineException;
import org.fireflow.engine.IProcessInstance;
import org.fireflow.engine.IRuntimeContextAware;
import org.fireflow.engine.ITaskInstance;
import org.fireflow.engine.IWorkItem;
import org.fireflow.engine.IWorkflowSession;
import org.fireflow.engine.IWorkflowSessionAware;
import org.fireflow.engine.IWorkflowSessionCallback;
import org.fireflow.engine.RuntimeContext;
import org.fireflow.engine.definition.WorkflowDefinition;
import org.fireflow.engine.persistence.IPersistenceService;
import org.fireflow.engine.taskinstance.DynamicAssignmentHandler;
import org.fireflow.kernel.KernelException;
import org.fireflow.model.DataField;
import org.fireflow.model.WorkflowProcess;

/**
 * @author chennieyun
 * 
 */
public class WorkflowSession implements IWorkflowSession, IRuntimeContextAware {

	protected RuntimeContext runtimeContext = null;
	protected DynamicAssignmentHandler dynamicAssignmentHandler = null;
	protected boolean inWithdrawOrRejectOperation = false;
	protected Map<String,Object> attributes = new HashMap<String,Object>();

	public void setRuntimeContext(RuntimeContext ctx) {
		this.runtimeContext = ctx;
	}

	public WorkflowSession(RuntimeContext ctx) {
		this.runtimeContext = ctx;
	}

	public void setCurrentDynamicAssignmentHandler(
			DynamicAssignmentHandler handler) {
		this.dynamicAssignmentHandler = handler;
	}

	/**
	 * @return
	 */
	public DynamicAssignmentHandler consumeCurrentDynamicAssignmentHandler() {
		DynamicAssignmentHandler handler = this.dynamicAssignmentHandler;
		this.dynamicAssignmentHandler = null;
		return handler;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.IWorkflowSession#createProcessInstance(java.lang.String, org.fireflow.engine.ITaskInstance)
	 */
	public IProcessInstance createProcessInstance(String workflowProcessName,
			ITaskInstance parentTaskInstance) throws EngineException,
			KernelException {
		return _createProcessInstance(workflowProcessName, parentTaskInstance
				.getId(), parentTaskInstance.getProcessInstanceId(),
				parentTaskInstance.getId());
	}

	/**
	 * 创建一个新的流程实例 (create a new process instance )
	 * @param workflowProcessId  流程定义ID
	 * @param creatorId  创建人ID
	 * @param parentProcessInstanceId  父流程实例ID
	 * @param parentTaskInstanceId     父任务实例ID
	 * @return
	 * @throws EngineException
	 * @throws KernelException
	 */
	protected IProcessInstance _createProcessInstance(String workflowProcessId,
			final String creatorId, final String parentProcessInstanceId,
			final String parentTaskInstanceId) throws EngineException,
			KernelException {
		final String wfprocessId = workflowProcessId;
		
		final WorkflowDefinition workflowDef = runtimeContext.getDefinitionService().getTheLatestVersionOfWorkflowDefinition(wfprocessId);
		final WorkflowProcess wfProcess = workflowDef.getWorkflowProcess();

		if (wfProcess == null) {
			throw new RuntimeException(
					"Workflow process NOT found,id=[" + wfprocessId
							+ "]");
		}
		IProcessInstance processInstance =  (IProcessInstance) this.execute(new IWorkflowSessionCallback() {

			public Object doInWorkflowSession(RuntimeContext ctx)
					throws EngineException, KernelException {

				ProcessInstance processInstance = new ProcessInstance();
				processInstance.setCreatorId(creatorId);
				processInstance.setProcessId(wfProcess.getId());
				processInstance.setVersion(workflowDef.getVersion());
				processInstance.setDisplayName(wfProcess.getDisplayName());
				processInstance.setName(wfProcess.getName());
				processInstance.setState(IProcessInstance.INITIALIZED);
				processInstance.setCreatedTime(ctx.getCalendarService()
						.getSysDate());
				processInstance
						.setParentProcessInstanceId(parentProcessInstanceId);
				processInstance.setParentTaskInstanceId(parentTaskInstanceId);
				
				ctx.getPersistenceService().saveOrUpdateProcessInstance(
						processInstance);
				
				return processInstance;
			}
		});
		
		// 初始化流程变量
		processInstance.setProcessInstanceVariables(new HashMap<String, Object>());
		
		List<DataField> datafields = wfProcess.getDataFields();
		for (int i = 0; datafields != null && i < datafields.size(); i++) {
			DataField df =  datafields.get(i);
			if (df.getDataType().equals(DataField.STRING)) {
				if (df.getInitialValue() != null) {
					processInstance.setProcessInstanceVariable(df
							.getName(), df.getInitialValue());
				} else {
					processInstance.setProcessInstanceVariable(df
							.getName(), "");
				}
			} else if (df.getDataType().equals(DataField.INTEGER)) {
				if (df.getInitialValue() != null) {
					try {
						Integer intValue = new Integer(df
								.getInitialValue());
						processInstance.setProcessInstanceVariable(df
								.getName(), intValue);
					} catch (Exception e) {
					}
				} else {
					processInstance.setProcessInstanceVariable(df
							.getName(), new Integer(0));
				}
			} else if (df.getDataType().equals(DataField.LONG)) {
				if (df.getInitialValue() != null) {
					try {
						Long longValue = new Long(df.getInitialValue());
						processInstance.setProcessInstanceVariable(df
								.getName(), longValue);
					} catch (Exception e) {
					}
				} else {
					processInstance.setProcessInstanceVariable(df
							.getName(), new Long(0));
				}
			} else if (df.getDataType().equals(DataField.FLOAT)) {
				if (df.getInitialValue() != null) {
					Float floatValue = new Float(df.getInitialValue());
					processInstance.setProcessInstanceVariable(df
							.getName(), floatValue);
				} else {
					processInstance.setProcessInstanceVariable(df
							.getName(), new Float(0));
				}
			} else if (df.getDataType().equals(DataField.DOUBLE)) {
				if (df.getInitialValue() != null) {
					Double doubleValue = new Double(df
							.getInitialValue());
					processInstance.setProcessInstanceVariable(df
							.getName(), doubleValue);
				} else {
					processInstance.setProcessInstanceVariable(df
							.getName(), new Double(0));
				}
			} else if (df.getDataType().equals(DataField.BOOLEAN)) {
				if (df.getInitialValue() != null) {
					Boolean booleanValue = new Boolean(df
							.getInitialValue());
					processInstance.setProcessInstanceVariable(df
							.getName(), booleanValue);
				} else {
					processInstance.setProcessInstanceVariable(df
							.getName(), Boolean.FALSE);
				}
			} else if (df.getDataType().equals(DataField.DATETIME)) {
				// TODO 需要完善一下
				if (df.getInitialValue() != null
						&& df.getDataPattern() != null) {
					try {
						SimpleDateFormat dFormat = new SimpleDateFormat(
								df.getDataPattern());
						Date dateTmp = dFormat.parse(df
								.getInitialValue());
						processInstance.setProcessInstanceVariable(df
								.getName(), dateTmp);
					} catch (Exception e) {
						processInstance.setProcessInstanceVariable(df
								.getName(), null);
						e.printStackTrace();
					}
				} else {
					processInstance.setProcessInstanceVariable(df
							.getName(), null);
				}
			}
		}
		return processInstance;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.IWorkflowSession#createProcessInstance(java.lang.String, java.lang.String)
	 */
	public IProcessInstance createProcessInstance(String workflowProcessId,
			final String creatorId) throws EngineException, KernelException {
		return _createProcessInstance(workflowProcessId, creatorId, null, null);
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.IWorkflowSession#findWorkItemById(java.lang.String)
	 */
	public IWorkItem findWorkItemById(String id) {
		final String workItemId = id;
		try {
			return (IWorkItem) this.execute(new IWorkflowSessionCallback() {

				public Object doInWorkflowSession(RuntimeContext ctx)
						throws EngineException, KernelException {
					IPersistenceService persistenceService = ctx
							.getPersistenceService();

					return persistenceService.findWorkItemById(workItemId);
				}
			});
		} catch (EngineException ex) {
			ex.printStackTrace();
			return null;
		} catch (KernelException ex) {
			ex.printStackTrace();
			return null;
		}
	}

	/**
	 * WorkflowSession采用了模板模式，以便将来有需要时可以在本方法中进行扩展。
	 * 
	 * @param callback
	 * @return
	 * @throws org.fireflow.engine.EngineException
	 * @throws org.fireflow.kenel.KenelException
	 */
	@SuppressWarnings("unchecked")
	public Object execute(IWorkflowSessionCallback callback)
			throws EngineException, KernelException {
		try {
			Object result = callback.doInWorkflowSession(runtimeContext);
			if (result != null) {
				if (result instanceof IRuntimeContextAware) {
					((IRuntimeContextAware) result)
							.setRuntimeContext(this.runtimeContext);
				}
				if (result instanceof IWorkflowSessionAware) {
					((IWorkflowSessionAware) result)
							.setCurrentWorkflowSession(this);
				}

				if (result instanceof List) {
					List l = (List) result;
					for (int i = 0; i < l.size(); i++) {
						Object item = l.get(i);
						if (item instanceof IRuntimeContextAware) {
							((IRuntimeContextAware) item)
									.setRuntimeContext(runtimeContext);
							if (item instanceof IWorkflowSessionAware) {
								((IWorkflowSessionAware) item)
										.setCurrentWorkflowSession(this);
							}
						} else {
							break;
						}
					}
				}
			}
			return result;
		} finally {

		}
	}

	public RuntimeContext getRuntimeContext() {
		return runtimeContext;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.IWorkflowSession#findTaskInstanceById(java.lang.String)
	 */
	public ITaskInstance findTaskInstanceById(String id) {
		final String taskInstanceId = id;
		try {
			return (ITaskInstance) this.execute(new IWorkflowSessionCallback() {

				public Object doInWorkflowSession(RuntimeContext ctx)
						throws EngineException, KernelException {
					IPersistenceService persistenceService = ctx
							.getPersistenceService();

					return persistenceService
							.findTaskInstanceById(taskInstanceId);
				}
			});
		} catch (EngineException ex) {
			ex.printStackTrace();
			return null;
		} catch (KernelException ex) {
			ex.printStackTrace();
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.IWorkflowSession#findMyTodoWorkItems(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public List<IWorkItem> findMyTodoWorkItems(final String actorId) {
		List<IWorkItem> result = null;
		try {
			result = (List<IWorkItem>) this.execute(new IWorkflowSessionCallback() {

				public Object doInWorkflowSession(RuntimeContext ctx)
						throws EngineException, KernelException {
					return ctx.getPersistenceService().findTodoWorkItems(
							actorId);
				}
			});
		} catch (EngineException ex) {
			Logger.getLogger(WorkflowSession.class.getName()).log(Level.SEVERE,
					null, ex);
		} catch (KernelException ex) {
			Logger.getLogger(WorkflowSession.class.getName()).log(Level.SEVERE,
					null, ex);
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.IWorkflowSession#findMyTodoWorkItems(java.lang.String, java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public List<IWorkItem> findMyTodoWorkItems(final String actorId,
			final String processInstanceId) {
		List<IWorkItem> result = null;
		try {
			result = (List<IWorkItem>) this.execute(new IWorkflowSessionCallback() {

				public Object doInWorkflowSession(RuntimeContext ctx)
						throws EngineException, KernelException {
					return ctx.getPersistenceService().findTodoWorkItems(
							actorId, processInstanceId);
				}
			});
		} catch (EngineException ex) {
			Logger.getLogger(WorkflowSession.class.getName()).log(Level.SEVERE,
					null, ex);
		} catch (KernelException ex) {
			Logger.getLogger(WorkflowSession.class.getName()).log(Level.SEVERE,
					null, ex);
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.IWorkflowSession#findMyTodoWorkItems(java.lang.String, java.lang.String, java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public List<IWorkItem> findMyTodoWorkItems(final String actorId,
			final String processId, final String taskId) {
		List<IWorkItem> result = null;
		try {
			result = (List<IWorkItem>) this.execute(new IWorkflowSessionCallback() {

				public Object doInWorkflowSession(RuntimeContext ctx)
						throws EngineException, KernelException {
					return ctx.getPersistenceService().findTodoWorkItems(
							actorId, processId, taskId);
				}
			});
		} catch (EngineException ex) {
			Logger.getLogger(WorkflowSession.class.getName()).log(Level.SEVERE,
					null, ex);
		} catch (KernelException ex) {
			Logger.getLogger(WorkflowSession.class.getName()).log(Level.SEVERE,
					null, ex);
		}
		return result;
	}

	public void setWithdrawOrRejectOperationFlag(boolean b) {
		this.inWithdrawOrRejectOperation = b;
	}

	public boolean isInWithdrawOrRejectOperation() {
		return this.inWithdrawOrRejectOperation;
	}

	public void setDynamicAssignmentHandler(
			DynamicAssignmentHandler dynamicAssignmentHandler) {
		this.dynamicAssignmentHandler = dynamicAssignmentHandler;
	}

	public IProcessInstance abortProcessInstance(String processInstanceId)
			throws EngineException {
		IProcessInstance processInstance = this
				.findProcessInstanceById(processInstanceId);
		processInstance.abort();
		return processInstance;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.IWorkflowSession#claimWorkItem(java.lang.String)
	 */
	public IWorkItem claimWorkItem(final String workItemId)
			throws EngineException, KernelException {
		IWorkItem result = null;
		IWorkItem wi = this.findWorkItemById(workItemId);
		result = wi.claim();
		return result;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.IWorkflowSession#completeWorkItem(java.lang.String)
	 */
	public void completeWorkItem(String workItemId) throws EngineException,
			KernelException {
		IWorkItem wi = this.findWorkItemById(workItemId);
		wi.complete();

	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.IWorkflowSession#completeWorkItem(java.lang.String, java.lang.String)
	 */
	public void completeWorkItem(String workItemId, String comments)
			throws EngineException, KernelException {
		IWorkItem wi = this.findWorkItemById(workItemId);
		wi.complete(comments);
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.IWorkflowSession#completeWorkItem(java.lang.String, org.fireflow.engine.taskinstance.DynamicAssignmentHandler, java.lang.String)
	 */
	public void completeWorkItem(String workItemId,
			DynamicAssignmentHandler dynamicAssignmentHandler, String comments)
			throws EngineException, KernelException {
		IWorkItem wi = this.findWorkItemById(workItemId);
		wi.complete(dynamicAssignmentHandler, comments);

	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.IWorkflowSession#completeWorkItemAndJumpTo(java.lang.String, java.lang.String)
	 */
	public void completeWorkItemAndJumpTo(String workItemId,
			String targetActivityId) throws EngineException, KernelException {
		IWorkItem wi = this.findWorkItemById(workItemId);
		wi.jumpTo(targetActivityId);

	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.IWorkflowSession#completeWorkItemAndJumpTo(java.lang.String, java.lang.String, java.lang.String)
	 */
	public void completeWorkItemAndJumpTo(String workItemId,
			String targetActivityId, String comments) throws EngineException,
			KernelException {
		IWorkItem wi = this.findWorkItemById(workItemId);
		wi.jumpTo(targetActivityId, comments);
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.IWorkflowSession#completeWorkItemAndJumpTo(java.lang.String, java.lang.String, org.fireflow.engine.taskinstance.DynamicAssignmentHandler, java.lang.String)
	 */
	public void completeWorkItemAndJumpTo(String workItemId,
			String targetActivityId,
			DynamicAssignmentHandler dynamicAssignmentHandler, String comments)
			throws EngineException, KernelException {
		IWorkItem wi = this.findWorkItemById(workItemId);
		wi.jumpTo(targetActivityId, dynamicAssignmentHandler, comments);

	}
	/* (non-Javadoc)
	 * @see org.fireflow.engine.IWorkflowSession#completeWorkItemAndJumpToEx(java.lang.String, java.lang.String, org.fireflow.engine.taskinstance.DynamicAssignmentHandler, java.lang.String)
	 */
	public void completeWorkItemAndJumpToEx(String workItemId,
			String targetActivityId,
			DynamicAssignmentHandler dynamicAssignmentHandler, String comments)
			throws EngineException, KernelException {
		IWorkItem wi = this.findWorkItemById(workItemId);
		wi.jumpToEx(targetActivityId, dynamicAssignmentHandler, comments);

	}
	/* (non-Javadoc)
	 * @see org.fireflow.engine.IWorkflowSession#findProcessInstanceById(java.lang.String)
	 */
	public IProcessInstance findProcessInstanceById(final String id) {
		try {
			return (IProcessInstance) this
					.execute(new IWorkflowSessionCallback() {

						public Object doInWorkflowSession(RuntimeContext ctx)
								throws EngineException, KernelException {
							IPersistenceService persistenceService = ctx
									.getPersistenceService();

							return persistenceService
									.findProcessInstanceById(id);
						}
					});
		} catch (EngineException ex) {
			ex.printStackTrace();
			return null;
		} catch (KernelException ex) {
			ex.printStackTrace();
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.IWorkflowSession#findProcessInstancesByProcessId(java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public List<IProcessInstance> findProcessInstancesByProcessId(
			final String processId) {
		try {
			return (List<IProcessInstance>) this
					.execute(new IWorkflowSessionCallback() {

						public Object doInWorkflowSession(RuntimeContext ctx)
								throws EngineException, KernelException {
							IPersistenceService persistenceService = ctx
									.getPersistenceService();

							return persistenceService
									.findProcessInstancesByProcessId(processId);
						}
					});
		} catch (EngineException ex) {
			ex.printStackTrace();
			return null;
		} catch (KernelException ex) {
			ex.printStackTrace();
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.IWorkflowSession#findProcessInstancesByProcessIdAndVersion(java.lang.String, java.lang.Integer)
	 */
	@SuppressWarnings("unchecked")
	public List<IProcessInstance> findProcessInstancesByProcessIdAndVersion(
			final String processId, final Integer version) {
		try {
			return (List<IProcessInstance>) this
					.execute(new IWorkflowSessionCallback() {

						public Object doInWorkflowSession(RuntimeContext ctx)
								throws EngineException, KernelException {
							IPersistenceService persistenceService = ctx
									.getPersistenceService();

							return persistenceService
									.findProcessInstancesByProcessIdAndVersion(
											processId, version);
						}
					});
		} catch (EngineException ex) {
			ex.printStackTrace();
			return null;
		} catch (KernelException ex) {
			ex.printStackTrace();
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.IWorkflowSession#findTaskInstancesForProcessInstance(java.lang.String, java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public List<ITaskInstance> findTaskInstancesForProcessInstance(
			final String processInstanceId, final String activityId) {
		try {
			return (List<ITaskInstance>) this
					.execute(new IWorkflowSessionCallback() {

						public Object doInWorkflowSession(RuntimeContext ctx)
								throws EngineException, KernelException {
							IPersistenceService persistenceService = ctx
									.getPersistenceService();

							return persistenceService
									.findTaskInstancesForProcessInstance(
											processInstanceId, activityId);
						}
					});
		} catch (EngineException ex) {
			ex.printStackTrace();
			return null;
		} catch (KernelException ex) {
			ex.printStackTrace();
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.IWorkflowSession#reassignWorkItemTo(java.lang.String, java.lang.String)
	 */
	public IWorkItem reassignWorkItemTo(String workItemId, String actorId)
			throws EngineException {
		IWorkItem workItem = this.findWorkItemById(workItemId);
		return workItem.reassignTo(actorId);

	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.IWorkflowSession#reassignWorkItemTo(java.lang.String, java.lang.String, java.lang.String)
	 */
	public IWorkItem reassignWorkItemTo(String workItemId, String actorId,
			String comments) throws EngineException {
		IWorkItem workItem = this.findWorkItemById(workItemId);
		return workItem.reassignTo(actorId, comments);
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.IWorkflowSession#rejectWorkItem(java.lang.String)
	 */
	public void rejectWorkItem(String workItemId) throws EngineException,
			KernelException {
		IWorkItem workItem = this.findWorkItemById(workItemId);
		workItem.reject();

	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.IWorkflowSession#rejectWorkItem(java.lang.String, java.lang.String)
	 */
	public void rejectWorkItem(String workItemId, String comments)
			throws EngineException, KernelException {
		IWorkItem workItem = this.findWorkItemById(workItemId);
		workItem.reject(comments);
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.IWorkflowSession#restoreProcessInstance(java.lang.String)
	 */
	public IProcessInstance restoreProcessInstance(String processInstanceId)
			throws EngineException {
		IProcessInstance processInstance = this
				.findProcessInstanceById(processInstanceId);
		processInstance.restore();
		return processInstance;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.IWorkflowSession#restoreTaskInstance(java.lang.String)
	 */
	public ITaskInstance restoreTaskInstance(String taskInstanceId)
			throws EngineException {
		ITaskInstance taskInst = this.findTaskInstanceById(taskInstanceId);
		taskInst.restore();
		return taskInst;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.IWorkflowSession#suspendProcessInstance(java.lang.String)
	 */
	public IProcessInstance suspendProcessInstance(String processInstanceId)
			throws EngineException {
		IProcessInstance processInstance = this
				.findProcessInstanceById(processInstanceId);
		processInstance.suspend();
		return processInstance;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.IWorkflowSession#suspendTaskInstance(java.lang.String)
	 */
	public ITaskInstance suspendTaskInstance(String taskInstanceId)
			throws EngineException {
		ITaskInstance taskInst = this.findTaskInstanceById(taskInstanceId);
		taskInst.suspend();
		return taskInst;
	}

	/* (non-Javadoc)
	 * @see org.fireflow.engine.IWorkflowSession#withdrawWorkItem(java.lang.String)
	 */
	public IWorkItem withdrawWorkItem(final String workItemId)
			throws EngineException, KernelException {
		IWorkItem wi = this.findWorkItemById(workItemId);
		return wi.withdraw();
	}

	public void clearAttributes() {
		this.attributes.clear();
		
	}

	public Object getAttribute(String name) {
		return this.attributes.get(name);
	}

	public void removeAttribute(String name) {
		this.attributes.remove(name);
	}

	public void setAttribute(String name, Object attr) {
		this.attributes.put(name, attr);
		
	}

}
