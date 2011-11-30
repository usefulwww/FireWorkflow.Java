package org.fireflow.engine.persistence.jpa;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.fireflow.engine.IProcessInstance;
import org.fireflow.engine.ITaskInstance;
import org.fireflow.engine.IWorkItem;
import org.fireflow.engine.RuntimeContext;
import org.fireflow.engine.definition.WorkflowDefinition;
import org.fireflow.engine.impl.ProcessInstance;
import org.fireflow.engine.impl.ProcessInstanceTrace;
import org.fireflow.engine.impl.ProcessInstanceVar;
import org.fireflow.engine.impl.TaskInstance;
import org.fireflow.engine.impl.WorkItem;
import org.fireflow.engine.persistence.IPersistenceService;
import org.fireflow.kernel.IToken;
import org.fireflow.kernel.impl.Token;

public class PersistenceServiceJpaImpl implements IPersistenceService
{
	@PersistenceContext
	private EntityManager em;

	protected RuntimeContext rtCtx = null;

	public void setRuntimeContext(RuntimeContext ctx)
	{
		this.rtCtx = ctx;
	}

	public RuntimeContext getRuntimeContext()
	{
		return this.rtCtx;

	}

	public void saveOrUpdateProcessInstance(IProcessInstance processInstance)
	{
		if (processInstance.getId() == null || processInstance.getId().equals(""))
		{
			((ProcessInstance) processInstance).setId(java.util.UUID.randomUUID().toString().replaceAll("-", ""));
		}
		this.em.merge(processInstance);

		// // 操作流程实例变量
		// Map<String, Object> map =
		// processInstance.getProcessInstanceVariables();
		//
		// if (map != null && !map.isEmpty())
		// {// map不为空，且map中有值
		//
		// // saveOrUpdateProcessInstanceVar(processInstance.getId(),map);
		// Set<Entry<String, Object>> entrySet = map.entrySet();
		// ProcessInstanceVar processInstanceVar = null;
		// ProcessInstanceVarPk varPrimaryKey = null;
		//
		// for (Entry<String, Object> entry : entrySet)
		// {
		// varPrimaryKey = new ProcessInstanceVarPk();
		// varPrimaryKey.setProcessInstanceId(processInstance.getId());
		// varPrimaryKey.setName(entry.getKey());
		//				
		// processInstanceVar = new ProcessInstanceVar();
		// processInstanceVar.setVarPrimaryKey(varPrimaryKey);
		// processInstanceVar.setValue(entry.getValue().getClass().getName() +
		// "#" + entry.getValue());
		//				
		// this.updateProcessInstanceVariable(processInstanceVar);
		// }
		// }
	}

	// public void saveOrUpdateProcessInstanceVar(String processInstanceId,
	// Map<String, Object> variables)
	// {
	// Set<Entry<String,Object>> entrySet = variables.entrySet();
	// ProcessInstanceVar processInstanceVar = null;
	// for (Entry<String, Object> entry : entrySet) {
	// processInstanceVar = new ProcessInstanceVar();
	// // processInstanceVar.setProcessInstanceId(processInstanceId);
	// // processInstanceVar.setName(entry.getKey());
	// //
	// processInstanceVar.setValue(entry.getValue().getClass().getName()+"#"+entry.getValue());
	// //
	// this.em.merge(processInstanceVar);
	// }
	// }

	public void saveOrUpdateTaskInstance(ITaskInstance taskInstance)
	{
		if (taskInstance.getId() == null || taskInstance.getId().equals(""))
		{
			((TaskInstance) taskInstance).setId(java.util.UUID.randomUUID().toString().replaceAll("-", ""));
		}
		this.em.merge(taskInstance);
	}

	public void saveOrUpdateWorkItem(IWorkItem workitem)
	{
		if (workitem.getId() == null || workitem.getId().equals(""))
		{
			((WorkItem) workitem).setId(java.util.UUID.randomUUID().toString().replaceAll("-", ""));
		}
		this.em.merge(workitem);
	}

	public void saveOrUpdateToken(IToken token)
	{
		if (token.getId() == null || token.getId().equals(""))
		{
			((Token) token).setId(java.util.UUID.randomUUID().toString().replaceAll("-", ""));
		}
		this.em.merge(token);
	}

	public Integer getAliveTokenCountForNode(final String processInstanceId, final String nodeId)
	{
		Query query = em
				.createQuery("select count(token) from Token as token where processInstanceId=?1 and nodeId=?2 and alive=?3");
		query.setParameter(1, processInstanceId);
		query.setParameter(2, nodeId);
		query.setParameter(3, java.lang.Boolean.TRUE);

		Object result = query.getSingleResult();
		if (result instanceof Integer)
			return (Integer) result;
		else
		{
			return new Integer(((Long) result).intValue());
		}
	}

	public Integer getCompletedTaskInstanceCountForTask(final String processInstanceId, final String taskId)
	{
		Query query = em
				.createQuery("select count(ti) from TaskInstance ti where taskId=?1 and processInstanceId=?2 and state=?3");
		query.setParameter(1, taskId.trim());
		query.setParameter(2, processInstanceId);
		query.setParameter(3, Integer.valueOf(ITaskInstance.COMPLETED));

		Object result = query.getSingleResult();
		if (result instanceof Integer)
			return (Integer) result;
		else
		{
			return new Integer(((Long) result).intValue());
		}

	}

	public Integer getAliveTaskInstanceCountForActivity(final String processInstanceId, final String activityId)
	{
		Query query = em.createQuery("select count(ti) from TaskInstance ti"
				+ " where processInstanceId=?1 and activityId=?2 and (state=?3 or state=?4)");

		query.setParameter(1, processInstanceId);
		query.setParameter(2, activityId.trim());
		query.setParameter(3, new Integer(ITaskInstance.INITIALIZED));
		query.setParameter(4, new Integer(ITaskInstance.RUNNING));
		Object result = query.getSingleResult();

		if (result instanceof Integer)
			return (Integer) result;
		else
		{
			return Integer.valueOf(((Long) result).intValue());
		}
	}

	/**
	 * 获得同一个Token的所有状态为Initialized的TaskInstance
	 * @param tokenId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<ITaskInstance> findInitializedTaskInstancesListForToken(final String processInstanceId,
			final String tokenId)
	{
		Query query = em.createQuery("select taskInstance from TaskInstance as taskInstance"
				+ " where processInstanceId=?1 and tokenId=?2 and state=?3");

		query.setParameter(1, processInstanceId);
		query.setParameter(2, tokenId.trim());
		query.setParameter(3, Integer.valueOf(0));
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<ITaskInstance> findTaskInstancesForProcessInstance(final java.lang.String processInstanceId,
			final String activityId)
	{
		StringBuilder queryString = new StringBuilder();
		queryString.append("select ti from TaskInstance ti where processInstanceId=?1");
		if (activityId != null && !activityId.trim().equals(""))
		{
			queryString.append(" and activityId=?2");
		}
		Query query = em.createQuery(queryString.toString());

		query.setParameter(1, processInstanceId);

		if (activityId != null && !activityId.trim().equals(""))
		{
			query.setParameter(2, activityId.trim());
		}

		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<ITaskInstance> findTaskInstancesForProcessInstanceByStepNumber(final String processInstanceId,
			final Integer stepNumber)
	{
		StringBuilder queryString = new StringBuilder();
		queryString.append("select ti from TaskInstance ti where processInstanceId=?1");
		if (stepNumber != null)
		{
			queryString.append(" and stepNumber=?2");
		}
		Query query = em.createQuery(queryString.toString());

		query.setParameter(1, processInstanceId);

		if (stepNumber != null)
		{
			query.setParameter(2, stepNumber);
		}

		return query.getResultList();
	}

	public void lockTaskInstance(String taskInstanceId)
	{
		// hibernate:LockMode.UPGRADE 相当于jpa:LockModeType.READ
		TaskInstance taskInstance = this.em.find(TaskInstance.class, taskInstanceId);
		this.em.lock(taskInstance, LockModeType.READ);
	}

	public IToken findTokenById(String id)
	{
		IToken token = this.em.find(Token.class, id);

		if (token == null)
			return null;
		return token;
	}

	public void deleteTokensForNodes(String processInstanceId, List<String> nodeIdsList)
	{
		Query query = em
				.createQuery("delete from org.fireflow.kernel.impl.Token where processInstanceId=:processInstanceId and nodeId in (:nodeId)");
		query.setParameter("processInstanceId", processInstanceId);
		query.setParameter("nodeId", nodeIdsList);
		query.executeUpdate();
	}

	public void deleteTokensForNode(final String processInstanceId, final String nodeId)
	{
		String hql = "delete from org.fireflow.kernel.impl.Token where processInstanceId=:processInstanceId and nodeId=:nodeId";
		Query query = em.createQuery(hql);
		query.setParameter("processInstanceId", processInstanceId);
		query.setParameter("nodeId", nodeId);
		query.executeUpdate();
	}

	public void deleteToken(IToken token)
	{
		this.em.remove(token);
	}

	@SuppressWarnings("unchecked")
	public List<IToken> findTokensForProcessInstance(final String processInstanceId, final String nodeId)
	{
		String hql = "select token from Token as token where processInstanceId=?1";
		if (nodeId != null && !nodeId.trim().equals(""))
		{
			hql += " and nodeId=?2";
		}
		Query query = em.createQuery(hql);
		query.setParameter(1, processInstanceId);
		if (nodeId != null && !nodeId.trim().equals(""))
		{
			query.setParameter(2, nodeId);
		}
		return query.getResultList();
	}

	public IWorkItem findWorkItemById(String id)
	{
		IWorkItem workItem = this.em.find(WorkItem.class, id);
		if (workItem == null)
			return null;
		return workItem;
	}

	@SuppressWarnings("unchecked")
	public ITaskInstance findAliveTaskInstanceById(final String id)
	{
		Query query = em.createQuery("select ti from TaskInstance ti where (state=?1 or state=?2) and id=?3");
		query.setParameter(1, new Integer(ITaskInstance.INITIALIZED));
		query.setParameter(2, new Integer(ITaskInstance.RUNNING));
		query.setParameter(3, id);
		List list = query.getResultList();
		if (list.size() == 0)
			return null;
		return (ITaskInstance) list.get(0);
	}

	public ITaskInstance findTaskInstanceById(String id)
	{
		return this.em.find(TaskInstance.class, id);
	}

	public void abortTaskInstance(final TaskInstance taskInstance)
	{
		Date now = rtCtx.getCalendarService().getSysDate();
		// 首先Cancel TaskInstance
		taskInstance.setState(ITaskInstance.CANCELED);
		taskInstance.setEndTime(now);
		taskInstance.setCanBeWithdrawn(Boolean.FALSE);
		em.merge(taskInstance);

		String hql = "Update org.fireflow.engine.impl.WorkItem m set m.state=:state ,m.endTime=:endTime Where m.taskInstance.id=:taskInstanceId And (m.state=0 Or m.state=1)";
		Query query = em.createQuery(hql);
		query.setParameter("state", IWorkItem.CANCELED);
		query.setParameter("endTime", now);
		query.setParameter("taskInstanceId", taskInstance.getId());

		query.executeUpdate();
	}

	public Integer getAliveWorkItemCountForTaskInstance(final String taskInstanceId)
	{
		String hql = "select count(*) From org.fireflow.engine.impl.WorkItem m Where m.taskInstance.id=:taskInstanceId And (m.state=0 Or m.state=1 Or m.state=3)";
		Query query = em.createQuery(hql);
		query.setParameter("taskInstanceId", taskInstanceId);

		Object result = query.getSingleResult();

		if (result instanceof Integer)
			return (Integer) result;
		else
		{
			return Integer.valueOf(((Long) result).intValue());
		}
	}

	@SuppressWarnings("unchecked")
	public List<IWorkItem> findCompletedWorkItemsForTaskInstance(final String taskInstanceId)
	{
		String hql = "select m From org.fireflow.engine.impl.WorkItem m Where m.taskInstance.id=:taskInstanceId And m.state=:state";
		Query query = em.createQuery(hql);
		query.setParameter("taskInstanceId", taskInstanceId);
		query.setParameter("state", IWorkItem.COMPLETED);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<IWorkItem> findWorkItemsForTaskInstance(final String taskInstanceId)
	{
		Query query = em.createQuery("select workItem from WorkItem workItem where workItem.taskInstance.id=?1");
		query.setParameter(1, taskInstanceId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<IWorkItem> findWorkItemsForTask(final String taskid)
	{
		Query query = em.createQuery("select workItem from WorkItem workItem where workItem.taskInstance.taskId=?1");
		query.setParameter(1, taskid);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<IProcessInstance> findProcessInstancesByProcessId(final String processId)
	{
		Query query = em.createQuery("select p from ProcessInstance p where processId=?1 order by createdTime asc");
		query.setParameter(1, processId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<IProcessInstance> findProcessInstancesByProcessIdAndVersion(final String processId,
			final Integer version)
	{
		Query query = em
				.createQuery("select p from ProcessInstance p where processId=?1 and version=?2 order by createdTime asc");
		query.setParameter(1, processId);
		query.setParameter(2, version);
		return query.getResultList();
	}

	public IProcessInstance findProcessInstanceById(String id)
	{
		IProcessInstance processInstance = this.em.find(ProcessInstance.class, id);
		
		return processInstance;
	}

	@SuppressWarnings("unchecked")
	public IProcessInstance findAliveProcessInstanceById(final String id)
	{
		Query query = em.createQuery("select p from ProcessInstance p where (state=?1 or state=?2) and id=?3");
		query.setParameter(1, new Integer(IProcessInstance.INITIALIZED));
		query.setParameter(2, new Integer(IProcessInstance.RUNNING));
		query.setParameter(3, id);
		List list = query.getResultList();
		if (list.size() == 0)
			return null;
		else
		{
			IProcessInstance processInstance = (IProcessInstance) list.get(0);
			return processInstance;
		}
	}

	public void saveOrUpdateWorkflowDefinition(WorkflowDefinition workflowDef)
	{
		if (workflowDef.getId() == null || workflowDef.getId().equals(""))
		{
			workflowDef.setId(java.util.UUID.randomUUID().toString().replaceAll("-", ""));
			Integer latestVersion = findTheLatestVersionNumberIgnoreState(workflowDef.getProcessId());
			if (latestVersion != null)
			{
				workflowDef.setVersion(new Integer(latestVersion.intValue() + 1));
			}
			else
			{
				workflowDef.setVersion(new Integer(1));
			}
		}

		this.em.merge(workflowDef);
	}

	public Integer findTheLatestVersionNumber(final String processId)
	{
		// 取得当前最大的发布状态为有效的version值
		Query q = em
				.createQuery("select max(m.version) from WorkflowDefinition m where m.processId=:processId and m.state=:state");
		q.setParameter("processId", processId);
		q.setParameter("state", Boolean.TRUE);
		Object obj = q.getSingleResult();
		if (obj != null)
		{
			Integer latestVersion = (Integer) obj;
			return latestVersion;
		}
		else
		{
			return null;
		}
	}

	public Integer findTheLatestVersionNumberIgnoreState(final String processId)
	{
		Query q = em.createQuery("select max(m.version) from WorkflowDefinition m where m.processId=:processId ");
		q.setParameter("processId", processId);
		Object obj = q.getSingleResult();
		if (obj != null)
		{
			Integer latestVersion = (Integer) obj;
			return latestVersion;
		}
		else
		{
			return null;
		}
	}

	public WorkflowDefinition findWorkflowDefinitionById(String id)
	{
		return this.em.find(WorkflowDefinition.class, id);
	}

	public WorkflowDefinition findWorkflowDefinitionByProcessIdAndVersionNumber(final String processId,
			final int version)
	{
		Query query = em.createQuery("select wfd from WorkflowDefinition wfd where processId=?1 and version=?2");
		query.setParameter(1, processId);
		query.setParameter(2, version);
		return (WorkflowDefinition) query.getSingleResult();
	}

	public WorkflowDefinition findTheLatestVersionOfWorkflowDefinitionByProcessId(String processId)
	{
		Integer latestVersion = this.findTheLatestVersionNumber(processId);
		return this.findWorkflowDefinitionByProcessIdAndVersionNumber(processId, latestVersion);
	}

	@SuppressWarnings("unchecked")
	public List<WorkflowDefinition> findWorkflowDefinitionsByProcessId(final String processId)
	{
		Query query = em.createQuery("select w from WorkflowDefinition w where processId=?1");
		query.setParameter(1, processId);
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<WorkflowDefinition> findAllTheLatestVersionsOfWorkflowDefinition()
	{
		String hql = "select distinct model.processId from WorkflowDefinition model ";
		Query query = em.createQuery(hql);
		List processIdList = query.getResultList();
		List _result = new Vector<WorkflowDefinition>();
		for (int i = 0; i < processIdList.size(); i++)
		{
			WorkflowDefinition wfDef = findTheLatestVersionOfWorkflowDefinitionByProcessId((String) processIdList
					.get(i));
			_result.add(wfDef);
		}
		return _result;
	}

	public List<IWorkItem> findTodoWorkItems(final String actorId)
	{
		return findTodoWorkItems(actorId, null);
	}

	@SuppressWarnings("unchecked")
	public List<IWorkItem> findTodoWorkItems(final String actorId, final String processInstanceId)
	{
		String hql = "select wi from WorkItem wi where (state=?1 or state=?2)";
		if (actorId != null && !actorId.trim().equals(""))
		{
			hql += " and actorId=:actorId";
		}
		if (processInstanceId != null && !processInstanceId.trim().equals(""))
		{
			hql += " and wi.taskInstance.processInstanceId=:processInstanceId";
		}
		Query query = em.createQuery(hql);

		query.setParameter(1, new Integer(IWorkItem.INITIALIZED));
		query.setParameter(2, new Integer(IWorkItem.RUNNING));

		if (actorId != null && !actorId.trim().equals(""))
		{
			query.setParameter("actorId", actorId);
		}
		if (processInstanceId != null && !processInstanceId.trim().equals(""))
		{
			query.setParameter("processInstanceId", processInstanceId);
		}
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<IWorkItem> findTodoWorkItems(final String actorId, final String processId, final String taskId)
	{
		String hql = "select wi from WorkItem wi where (state=?1 or state=?2)";
		if (actorId != null && !actorId.trim().equals(""))
		{
			hql += " and actorId=:actorId";
		}
		if (processId != null && !processId.trim().equals(""))
		{
			hql += " and wi.taskInstance.processId=:processId";
		}
		if (taskId != null && !taskId.trim().equals(""))
		{
			hql += " and wi.taskInstance.taskId=:taskId";
		}

		Query query = em.createQuery(hql);

		query.setParameter(1, new Integer(IWorkItem.INITIALIZED));
		query.setParameter(2, new Integer(IWorkItem.RUNNING));

		if (actorId != null && !actorId.trim().equals(""))
		{
			query.setParameter("actorId", actorId);
		}
		if (processId != null && !processId.trim().equals(""))
		{
			query.setParameter("processId", processId);
		}
		if (taskId != null && !taskId.trim().equals(""))
		{
			query.setParameter("taskId", taskId);
		}
		return query.getResultList();
	}

	public List<IWorkItem> findHaveDoneWorkItems(final String actorId)
	{
		return findHaveDoneWorkItems(actorId, null);
	}

	@SuppressWarnings("unchecked")
	public List<IWorkItem> findHaveDoneWorkItems(final String actorId, final String processInstanceId)
	{
		String hql = "select wi from WorkItem wi where (state=?1 or state=?2)";
		if (actorId != null && !actorId.trim().equals(""))
		{
			hql += " and actorId=:actorId";
		}
		if (processInstanceId != null && !processInstanceId.trim().equals(""))
		{
			hql += " and wi.taskInstance.processInstanceId=:processInstanceId";
		}
		hql += " order by wi.endTime asc";
		
		Query query = em.createQuery(hql);

		query.setParameter(1, new Integer(IWorkItem.COMPLETED));
		query.setParameter(2, new Integer(IWorkItem.CANCELED));

		if (actorId != null && !actorId.trim().equals(""))
		{
			query.setParameter("actorId", actorId);
		}
		if (processInstanceId != null && !processInstanceId.trim().equals(""))
		{
			query.setParameter("processInstanceId", processInstanceId);
		}
		return query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<IWorkItem> findHaveDoneWorkItems(final String actorId, final String processId, final String taskId)
	{
		String hql = "select wi from WorkItem wi where (state=?1 or state=?2)";
		if (actorId != null && !actorId.trim().equals(""))
		{
			hql += " and actorId=:actorId";
		}
		if (processId != null && !processId.trim().equals(""))
		{
			hql += " and wi.taskInstance.processId=:processId";
		}
		if (taskId != null && !taskId.trim().equals(""))
		{
			hql += " and wi.taskInstance.taskId=:taskId";
		}

		Query query = em.createQuery(hql);

		query.setParameter(1, new Integer(IWorkItem.COMPLETED));
		query.setParameter(2, new Integer(IWorkItem.CANCELED));

		if (actorId != null && !actorId.trim().equals(""))
		{
			query.setParameter("actorId", actorId);
		}
		if (processId != null && !processId.trim().equals(""))
		{
			query.setParameter("processId", processId);
		}
		if (taskId != null && !taskId.trim().equals(""))
		{
			query.setParameter("taskId", taskId);
		}
		return query.getResultList();
	}

	public void deleteWorkItemsInInitializedState(final String taskInstanceId)
	{
		String hql = "delete from org.fireflow.engine.impl.WorkItem where taskInstance.id=?1 and state=0";
		Query query = em.createQuery(hql);
		query.setParameter(1, taskInstanceId);
		query.executeUpdate();
	}

	public Integer getAliveProcessInstanceCountForParentTaskInstance(final String taskInstanceId)
	{
		Query query = em
				.createQuery("select count(pi) from ProcessInstance pi where parentTaskInstanceId=?1 and (state=?2 or state=?3)");
		query.setParameter(1, taskInstanceId);
		query.setParameter(2, new Integer(IProcessInstance.INITIALIZED));
		query.setParameter(3, new Integer(IProcessInstance.RUNNING));

		Object result = query.getSingleResult();
		if (result instanceof Integer)
			return (Integer) result;
		else
		{
			return new Integer(((Long) result).intValue());
		}
	}

	public void suspendProcessInstance(final ProcessInstance processInstance)
	{
		processInstance.setSuspended(Boolean.TRUE);
		em.merge(processInstance);

		String hql1 = "Update org.fireflow.engine.impl.TaskInstance m Set m.suspended=:suspended Where m.processInstanceId=:processInstanceId And (m.state=0 Or m.state=1)";
		Query query1 = em.createQuery(hql1);
		query1.setParameter("suspended", Boolean.TRUE);
		query1.setParameter("processInstanceId", processInstance.getId());
		query1.executeUpdate();
	}

	public void restoreProcessInstance(final ProcessInstance processInstance)
	{
		processInstance.setSuspended(Boolean.FALSE);
		em.merge(processInstance);

		String hql1 = "Update org.fireflow.engine.impl.TaskInstance m Set m.suspended=:suspended Where m.processInstanceId=:processInstanceId And (m.state=0 Or m.state=1)";
		Query query1 = em.createQuery(hql1);
		query1.setParameter("suspended", Boolean.FALSE);
		query1.setParameter("processInstanceId", processInstance.getId());
		query1.executeUpdate();
	}

	public void abortProcessInstance(final ProcessInstance processInstance)
	{
		Date now = rtCtx.getCalendarService().getSysDate();
		processInstance.setState(IProcessInstance.CANCELED);
		processInstance.setEndTime(now);
		em.merge(processInstance);

		String hql1 = "Update org.fireflow.engine.impl.TaskInstance as m set m.state=:state,m.endTime=:endTime,m.canBeWithdrawn=:canBewithdrawn Where m.processInstanceId=:processInstanceId And (m.state=0 Or m.state=1)";
		Query query1 = em.createQuery(hql1);
		query1.setParameter("state", ITaskInstance.CANCELED);
		query1.setParameter("endTime", now);
		query1.setParameter("canBewithdrawn", Boolean.FALSE);
		query1.setParameter("processInstanceId", processInstance.getId());
		query1.executeUpdate();

		String hql2 = "Update org.fireflow.engine.impl.WorkItem as m set m.state=:state,m.endTime=:endTime Where m.taskInstance in (From org.fireflow.engine.impl.TaskInstance n  Where n.processInstanceId=:processInstanceId)   And (m.state=0 Or m.state=1)";
		Query query2 = em.createQuery(hql2);
		query2.setParameter("state", IWorkItem.CANCELED);
		query2.setParameter("endTime", now);
		query2.setParameter("processInstanceId", processInstance.getId());
		query2.executeUpdate();

		String hql3 = "Delete org.fireflow.kernel.impl.Token where processInstanceId=:processInstanceId";
		Query query3 = em.createQuery(hql3);
		query3.setParameter("processInstanceId", processInstance.getId());
		query3.executeUpdate();
	}

	public void saveOrUpdateProcessInstanceTrace(ProcessInstanceTrace processInstanceTrace)
	{
		if (processInstanceTrace.getId() == null || processInstanceTrace.getId().equals(""))
		{
			processInstanceTrace.setId(java.util.UUID.randomUUID().toString().replaceAll("-", ""));
		}
		this.em.merge(processInstanceTrace);
	}

	@SuppressWarnings("unchecked")
	public List findProcessInstanceTraces(final String processInstanceId)
	{
		String hql = "From org.fireflow.engine.impl.ProcessInstanceTrace as m Where m.processInstanceId=:processInstanceId Order by m.stepNumber,m.minorNumber";
		Query q = em.createQuery(hql);
		q.setParameter("processInstanceId", processInstanceId);

		return q.getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<ProcessInstanceVar> findProcessInstanceVariable(String processInstanceId)
	{
		Query query = em
				.createQuery("select pv from ProcessInstanceVar pv where pv.varPrimaryKey.processInstanceId=?1");
		query.setParameter(1, processInstanceId);
		return (List) query.getResultList();
	}

	@SuppressWarnings("unchecked")
	public ProcessInstanceVar findProcessInstanceVariable(String processInstanceId, String name)
	{
		Query query = em
				.createQuery("select pv from ProcessInstanceVar pv where pv.varPrimaryKey.processInstanceId=?1 and pv.varPrimaryKey.name=?2");
		query.setParameter(1, processInstanceId);
		query.setParameter(2, name);
		List<ProcessInstanceVar> list = query.getResultList();
		if (list.size() < 1)
		{
			return null;
		}
		return list.get(0);
	}

	public void saveProcessInstanceVariable(ProcessInstanceVar var)
	{
		// 保存，如果主键冲突，则抛出异常
		//this.em.persist(var);
		this.em.merge(var);
	}

	public void updateProcessInstanceVariable(ProcessInstanceVar var)
	{
		this.em.merge(var);
	}

	@SuppressWarnings("unchecked")
	public List<IWorkItem> findHaveDoneWorkItems(String actorId,String publishUser, int pageSize, int pageNumber) throws RuntimeException
	{
		if (actorId == null || pageNumber < 1 || pageSize < 1)
		{
			throw new RuntimeException("actorId is null or pageNumber < 1 or pageSize < 1");
		}

		String hsql = "select wi from WorkItem wi where (state=?1 or state=?2) and actorId=:actorId";
		
		if(publishUser != null)
		{
			List<WorkflowDefinition> workflowDefinitions = this.findWorkflowDefinitionListByPublishUser(publishUser);
			
			if(workflowDefinitions.size() > 0)
			{
				hsql += " and (";
			}
			else
			{
				return new ArrayList<IWorkItem>();
			}
			WorkflowDefinition workflowDefinition = null;
			for(int i=0;i<workflowDefinitions.size();i++)
			{
				workflowDefinition = workflowDefinitions.get(i);
				if(i == 0)
				{
					hsql += "(wi.taskInstance.processId='"+workflowDefinition.getProcessId()+"' and wi.taskInstance.version="+workflowDefinition.getVersion()+")";
				}
				else
				{
					hsql += " or (wi.taskInstance.processId='"+workflowDefinition.getProcessId()+"' and wi.taskInstance.version="+workflowDefinition.getVersion()+")";
				}
			}
			hsql += ")";
		}
		hsql += " order by wi.endTime desc";
		Query query = em.createQuery(hsql);

		query.setParameter(1, new Integer(IWorkItem.COMPLETED));
		query.setParameter(2, new Integer(IWorkItem.COMPLETED));
		query.setParameter("actorId", actorId);

		int index = (pageNumber - 1)*pageSize;

		return query.setFirstResult(index).setMaxResults(pageSize).getResultList();
	}

	@SuppressWarnings("unchecked")
	public List<IWorkItem> findTodoWorkItems(String actorId,String publishUser, int pageSize, int pageNumber) throws RuntimeException
	{
		if (actorId == null || pageNumber < 1 || pageSize < 1)
		{
			throw new RuntimeException("actorId is null or pageNumber < 1 or pageSize < 1");
		}

		String hsql = "select wi from WorkItem wi where (state=?1 or state=?2) and actorId=:actorId";
		
		if(publishUser != null)
		{
			List<WorkflowDefinition> workflowDefinitions = this.findWorkflowDefinitionListByPublishUser(publishUser);
			
			if(workflowDefinitions.size() > 0)
			{
				hsql += " and (";
			}
			else
			{
				return new ArrayList<IWorkItem>();
			}
			WorkflowDefinition workflowDefinition = null;
			for(int i=0;i<workflowDefinitions.size();i++)
			{
				workflowDefinition = workflowDefinitions.get(i);
				if(i == 0)
				{
					hsql += "(wi.taskInstance.processId='"+workflowDefinition.getProcessId()+"' and wi.taskInstance.version="+workflowDefinition.getVersion()+")";
				}
				else
				{
					hsql += " or (wi.taskInstance.processId='"+workflowDefinition.getProcessId()+"' and wi.taskInstance.version="+workflowDefinition.getVersion()+")";
				}
			}
			hsql += ")";
		}
		hsql += " order by wi.createdTime desc";
		Query query = em.createQuery(hsql);

		query.setParameter(1, new Integer(IWorkItem.INITIALIZED));
		query.setParameter(2, new Integer(IWorkItem.RUNNING));
		query.setParameter("actorId", actorId);
		int index = (pageNumber - 1)*pageSize;

		return query.setFirstResult(index).setMaxResults(pageSize).getResultList();
	}

	public Integer getHaveDoneWorkItemsCount(String actorId,String publishUser) throws RuntimeException
	{
		if (actorId == null)
		{
			throw new RuntimeException("actorId is null");
		}

		String hsql = "select count(wi) from WorkItem wi where (state=?1 or state=?2) and actorId=:actorId";
		
		if(publishUser != null)
		{
			List<WorkflowDefinition> workflowDefinitions = this.findWorkflowDefinitionListByPublishUser(publishUser);
			
			if(workflowDefinitions.size() > 0)
			{
				hsql += " and (";
			}
			else
			{
				return 0;
			}
			WorkflowDefinition workflowDefinition = null;
			for(int i=0;i<workflowDefinitions.size();i++)
			{
				workflowDefinition = workflowDefinitions.get(i);
				if(i == 0)
				{
					hsql += "(wi.taskInstance.processId='"+workflowDefinition.getProcessId()+"' and wi.taskInstance.version="+workflowDefinition.getVersion()+")";
				}
				else
				{
					hsql += " or (wi.taskInstance.processId='"+workflowDefinition.getProcessId()+"' and wi.taskInstance.version="+workflowDefinition.getVersion()+")";
				}
			}
			hsql += ")";
		}
		
		Query query = em.createQuery(hsql);

		query.setParameter(1, new Integer(IWorkItem.COMPLETED));
		query.setParameter(2, new Integer(IWorkItem.COMPLETED));
		query.setParameter("actorId", actorId);

		Object result = query.getSingleResult();
		if (result instanceof Integer)
			return (Integer) result;
		else
		{
			return new Integer(((Long) result).intValue());
		}
	}

	public Integer getTodoWorkItemsCount(String actorId,String publishUser) throws RuntimeException
	{
		if (actorId == null)
		{
			throw new RuntimeException("actorId is null");
		}

		String hsql = "select count(wi) from WorkItem wi where (state=?1 or state=?2) and actorId=:actorId";
		
		if(publishUser != null)
		{
			List<WorkflowDefinition> workflowDefinitions = this.findWorkflowDefinitionListByPublishUser(publishUser);
			
			if(workflowDefinitions.size() > 0)
			{
				hsql += " and (";
			}
			else
			{
				return 0;
			}
			WorkflowDefinition workflowDefinition = null;
			for(int i=0;i<workflowDefinitions.size();i++)
			{
				workflowDefinition = workflowDefinitions.get(i);
				if(i == 0)
				{
					hsql += "(wi.taskInstance.processId='"+workflowDefinition.getProcessId()+"' and wi.taskInstance.version="+workflowDefinition.getVersion()+")";
				}
				else
				{
					hsql += " or (wi.taskInstance.processId='"+workflowDefinition.getProcessId()+"' and wi.taskInstance.version="+workflowDefinition.getVersion()+")";
				}
			}
			hsql += ")";
		}

		Query query = em.createQuery(hsql);

		query.setParameter(1, new Integer(IWorkItem.INITIALIZED));
		query.setParameter(2, new Integer(IWorkItem.RUNNING));
		query.setParameter("actorId", actorId);

		Object result = query.getSingleResult();
		if (result instanceof Integer)
			return (Integer) result;
		else
		{
			return new Integer(((Long) result).intValue());
		}
	}

	@SuppressWarnings("unchecked")
	public List<IProcessInstance> findProcessInstanceListByCreatorId(String creatorId,String publishUser, int pageSize, int pageNumber)
			throws RuntimeException
	{
		if (creatorId == null || pageNumber < 1 || pageSize < 1)
		{
			throw new RuntimeException("creatorId is null or pageNumber < 1 or pageSize < 1");
		}
		
		String hsql = "select pi from ProcessInstance pi where pi.creatorId=?1";
		
		if(publishUser != null)
		{
			List<WorkflowDefinition> workflowDefinitions = this.findWorkflowDefinitionListByPublishUser(publishUser);
			
			if(workflowDefinitions.size() > 0)
			{
				hsql += " and (";
			}
			else
			{
				return new ArrayList<IProcessInstance>();
			}
			WorkflowDefinition workflowDefinition = null;
			for(int i=0;i<workflowDefinitions.size();i++)
			{
				workflowDefinition = workflowDefinitions.get(i);
				if(i == 0)
				{
					hsql += "(pi.processId='"+workflowDefinition.getProcessId()+"' and pi.version="+workflowDefinition.getVersion()+")";
				}
				else
				{
					hsql += " or (pi.processId='"+workflowDefinition.getProcessId()+"' and pi.version="+workflowDefinition.getVersion()+")";
				}
			}
			hsql += ")";
		}
		hsql += " order by pi.createdTime desc";
		Query query = em.createQuery(hsql);
		query.setParameter(1, creatorId);
		
		int index = (pageNumber - 1)*pageSize;
		
		return query.setMaxResults(pageSize).setFirstResult(index).getResultList();
	}

	public Integer getProcessInstanceCountByCreatorId(String creatorId,String publishUser) throws RuntimeException
	{
		if (creatorId == null)
		{
			throw new RuntimeException("creatorId is null");
		}
		
		String hsql = "select count(pi) from ProcessInstance pi where pi.creatorId=?1";
		
		if(publishUser != null)
		{
			List<WorkflowDefinition> workflowDefinitions = this.findWorkflowDefinitionListByPublishUser(publishUser);
			
			if(workflowDefinitions.size() > 0)
			{
				hsql += " and (";
			}
			else
			{
				return 0;
			}
			WorkflowDefinition workflowDefinition = null;
			for(int i=0;i<workflowDefinitions.size();i++)
			{
				workflowDefinition = workflowDefinitions.get(i);
				if(i == 0)
				{
					hsql += "(pi.processId='"+workflowDefinition.getProcessId()+"' and pi.version="+workflowDefinition.getVersion()+")";
				}
				else
				{
					hsql += " or (pi.processId='"+workflowDefinition.getProcessId()+"' and pi.version="+workflowDefinition.getVersion()+")";
				}
			}
			hsql += ")";
		}
		
		Query query = em.createQuery(hsql);
		
		query.setParameter(1, creatorId);

		Object result = query.getSingleResult();
		if (result instanceof Integer)
		{
			return (Integer) result;
		}
		else
		{
			return new Integer(((Long) result).intValue());
		}
	}

	@SuppressWarnings("unchecked")
	public List<IProcessInstance> findProcessInstanceListByPublishUser(String publishUser, int pageSize, int pageNumber)
			throws RuntimeException
	{
		if (publishUser == null || pageNumber < 1 || pageSize < 1)
		{
			throw new RuntimeException("publishUser is null or pageNumber < 1 or pageSize < 1");
		}
		
		List<WorkflowDefinition> workflowDefinitions = this.findWorkflowDefinitionListByPublishUser(publishUser);

		String hsql = "select pi from ProcessInstance pi";
		if(workflowDefinitions.size() > 0)
		{
			hsql +=" where ";
		}
		else
		{
			return new ArrayList<IProcessInstance>();
		}
		WorkflowDefinition workflowDefinition = null;
		for(int i=0;i<workflowDefinitions.size();i++)
		{
			workflowDefinition = workflowDefinitions.get(i);
			if(i == 0)
			{
				hsql += "(pi.processId='"+workflowDefinition.getProcessId()+"' and pi.version="+workflowDefinition.getVersion()+")";
			}
			else
			{
				hsql += " or (pi.processId='"+workflowDefinition.getProcessId()+"' and pi.version="+workflowDefinition.getVersion()+")";
			}
		}
		hsql += " order by pi.createdTime desc";
		
		Query query = em.createQuery(hsql);
		int index = (pageNumber - 1)*pageSize;
		return query.setMaxResults(pageSize).setFirstResult(index).getResultList();
	}

	public Integer getProcessInstanceCountByPublishUser(String publishUser) throws RuntimeException
	{
		if (publishUser == null)
		{
			throw new RuntimeException("publishUser is null");
		}
		List<WorkflowDefinition> workflowDefinitions = this.findWorkflowDefinitionListByPublishUser(publishUser);

		String hsql = "select count(pi) from ProcessInstance pi";
		if(workflowDefinitions.size() > 0)
		{
			hsql +=" where ";
		}
		else
		{
			return 0;
		}
		WorkflowDefinition workflowDefinition = null;
		for(int i=0;i<workflowDefinitions.size();i++)
		{
			workflowDefinition = workflowDefinitions.get(i);
			if(i == 0)
			{
				hsql += "(pi.processId='"+workflowDefinition.getProcessId()+"' and pi.version="+workflowDefinition.getVersion()+")";
			}
			else
			{
				hsql += " or (pi.processId='"+workflowDefinition.getProcessId()+"' and pi.version="+workflowDefinition.getVersion()+")";
			}
		}
		
		Query query = em.createQuery(hsql);
		Object result = query.getSingleResult();
		if (result instanceof Integer)
		{
			return (Integer) result;
		}
		else
		{
			return new Integer(((Long) result).intValue());
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<WorkflowDefinition> findWorkflowDefinitionListByPublishUser(String publishUser)
	{
		Query query = em.createQuery("select wi from WorkflowDefinition wi where wi.publishUser=?1");
		query.setParameter(1, publishUser);
		return query.getResultList();
	}
}
