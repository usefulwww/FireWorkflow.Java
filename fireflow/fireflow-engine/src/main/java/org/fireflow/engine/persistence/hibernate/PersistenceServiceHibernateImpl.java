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
package org.fireflow.engine.persistence.hibernate;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.fireflow.engine.IProcessInstance;
import org.fireflow.engine.ITaskInstance;
import org.fireflow.engine.IWorkItem;
import org.fireflow.engine.RuntimeContext;
import org.fireflow.engine.definition.WorkflowDefinition;
import org.fireflow.engine.impl.ProcessInstance;
import org.fireflow.engine.impl.ProcessInstanceTrace;
import org.fireflow.engine.impl.ProcessInstanceVar;
import org.fireflow.engine.impl.ProcessInstanceVarPk;
import org.fireflow.engine.impl.TaskInstance;
import org.fireflow.engine.impl.WorkItem;
import org.fireflow.engine.persistence.IPersistenceService;
import org.fireflow.kernel.IToken;
import org.fireflow.kernel.impl.Token;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

/**
 * The hibernate implementation of persistence service
 * 
 * @author 非也,nychen2000@163.com
 * 
 */
public class PersistenceServiceHibernateImpl extends HibernateDaoSupport implements IPersistenceService {

    protected RuntimeContext rtCtx = null;

    public void setRuntimeContext(RuntimeContext ctx) {
        this.rtCtx = ctx;
    }

    public RuntimeContext getRuntimeContext() {
        return this.rtCtx;
    }

    /**
     * 流程实例
     * Save processInstance
     * @param processInstance
     */
    public void saveOrUpdateProcessInstance(IProcessInstance processInstance) {
    	
        this.getHibernateTemplate().saveOrUpdate(processInstance);
    }
    

    /* (non-Javadoc)
     * @see org.fireflow.engine.persistence.IPersistenceService#saveTaskInstance(org.fireflow.engine.ITaskInstance)
     */
    public void saveOrUpdateTaskInstance(ITaskInstance taskInstance) {
        this.getHibernateTemplate().saveOrUpdate(taskInstance);
    }


    /* (non-Javadoc)
     * @see org.fireflow.engine.persistence.IPersistenceService#saveOrUpdateWorkItem(org.fireflow.engine.IWorkItem)
     */
    public void saveOrUpdateWorkItem(IWorkItem workitem) {
        this.getHibernateTemplate().saveOrUpdate(workitem);
    }

    /* (non-Javadoc)
     * @see org.fireflow.engine.persistence.IPersistenceService#saveOrUpdateToken(org.fireflow.kernel.IToken)
     */
    public void saveOrUpdateToken(IToken token) {
        this.getHibernateTemplate().saveOrUpdate(token);
    }

    /* (non-Javadoc)
     * @see org.fireflow.engine.persistence.IPersistenceService#getAliveTokenCountForNode(java.lang.String, java.lang.String)
     */
    public Integer getAliveTokenCountForNode(final String processInstanceId, final String nodeId) {
        Integer result = (Integer) this.getHibernateTemplate().execute(new HibernateCallback() {

            public Object doInHibernate(Session arg0) throws HibernateException, SQLException {

                Criteria criteria = arg0.createCriteria(Token.class);

                criteria.add(Expression.eq("processInstanceId", processInstanceId));

                criteria.add(Expression.eq("nodeId", nodeId));

                criteria.add(Expression.eq("alive", java.lang.Boolean.TRUE));

                ProjectionList prolist = Projections.projectionList();
                prolist.add(Projections.rowCount());
                criteria.setProjection(prolist);

                return criteria.uniqueResult();
            }
        });
        return result;
    }

    /* (non-Javadoc)
     * @see org.fireflow.engine.persistence.IPersistenceService#getCompletedTaskInstanceCountForTask(java.lang.String, java.lang.String)
     */
    public Integer getCompletedTaskInstanceCountForTask(final String processInstanceId, final String taskId) {
        Integer result = (Integer) this.getHibernateTemplate().execute(new HibernateCallback() {

            public Object doInHibernate(Session arg0) throws HibernateException, SQLException {

                Criteria criteria = arg0.createCriteria(TaskInstance.class);
                criteria.add(Expression.eq("taskId", taskId.trim()));
                criteria.add(Expression.eq("processInstanceId", processInstanceId));

                Criterion cri2 = Expression.eq("state", new Integer(ITaskInstance.COMPLETED));

                criteria.add(cri2);

                ProjectionList prolist = Projections.projectionList();
                prolist.add(Projections.rowCount());
                criteria.setProjection(prolist);

                return criteria.uniqueResult();
            }
        });
        return result;
    }

    /* (non-Javadoc)
     * @see org.fireflow.engine.persistence.IPersistenceService#getAliveTaskInstanceCountForActivity(java.lang.String, java.lang.String)
     */
    public Integer getAliveTaskInstanceCountForActivity(final String processInstanceId, final String activityId) {
        Integer result = (Integer) this.getHibernateTemplate().execute(new HibernateCallback() {

            public Object doInHibernate(Session arg0) throws HibernateException, SQLException {

                Criteria criteria = arg0.createCriteria(TaskInstance.class);
                criteria.add(Expression.eq("processInstanceId", processInstanceId.trim()));

                criteria.add(Expression.eq("activityId", activityId.trim()));

                Criterion cri1 = Expression.eq("state", new Integer(ITaskInstance.INITIALIZED));
                Criterion cri2 = Expression.eq("state", new Integer(ITaskInstance.RUNNING));
                Criterion cri_or = Expression.or(cri1, cri2);

                criteria.add(cri_or);

                ProjectionList prolist = Projections.projectionList();
                prolist.add(Projections.rowCount());
                criteria.setProjection(prolist);

                return criteria.uniqueResult();
            }
        });
        return result;
    }

    /**
     * 获得同一个Token的所有状态为Initialized的TaskInstance
     * @param processInstanceId
     * @param tokenId
     * @return
     */
    @SuppressWarnings("unchecked")
	public List<ITaskInstance> findInitializedTaskInstancesListForToken(final String processInstanceId, final String tokenId) {
        List<ITaskInstance> result = (List<ITaskInstance>) this.getHibernateTemplate().execute(new HibernateCallback() {

            public Object doInHibernate(Session arg0) throws HibernateException, SQLException {

                Criteria criteria = arg0.createCriteria(TaskInstance.class);
                criteria.add(Expression.eq("processInstanceId", processInstanceId));

                criteria.add(Expression.eq("tokenId", tokenId.trim()));

                criteria.add(Expression.eq("state", new Integer(0)));

                return (List<ITaskInstance>) criteria.list();
            }
        });
        return result;
    }

    /* (non-Javadoc)
     * @see org.fireflow.engine.persistence.IPersistenceService#findTaskInstancesForProcessInstance(java.lang.String, java.lang.String)
     */
    @SuppressWarnings("unchecked")
	public List<ITaskInstance> findTaskInstancesForProcessInstance(final java.lang.String processInstanceId,
            final String activityId) {
        List<ITaskInstance> result = (List<ITaskInstance>) this.getHibernateTemplate().execute(new HibernateCallback() {

            public Object doInHibernate(Session arg0) throws HibernateException, SQLException {

                Criteria criteria = arg0.createCriteria(TaskInstance.class);
                criteria.add(Expression.eq("processInstanceId", processInstanceId.trim()));
                if (activityId != null && !activityId.trim().equals("")) {
                    criteria.add(Expression.eq("activityId", activityId.trim()));
                }
                return (List<ITaskInstance>) criteria.list();
            }
        });
        return result;
    }

    /* (non-Javadoc)
     * @see org.fireflow.engine.persistence.IPersistenceService#findTaskInstancesForProcessInstanceByStepNumber(java.lang.String, java.lang.Integer)
     */
    @SuppressWarnings("unchecked")
	public List<ITaskInstance> findTaskInstancesForProcessInstanceByStepNumber(final String processInstanceId, final Integer stepNumber) {
        List<ITaskInstance> result = (List<ITaskInstance>) this.getHibernateTemplate().execute(new HibernateCallback() {

            public Object doInHibernate(Session arg0) throws HibernateException, SQLException {

                Criteria criteria = arg0.createCriteria(TaskInstance.class);
                criteria.add(Expression.eq("processInstanceId", processInstanceId.trim()));

                if (stepNumber != null) {
                    criteria.add(Expression.eq("stepNumber", stepNumber));
                }
                return (List<ITaskInstance>) criteria.list();
            }
        });
        return result;
    }
    /* (non-Javadoc)
     * @see org.fireflow.engine.persistence.IPersistenceService#lockTaskInstance(java.lang.String)
     */
    public void lockTaskInstance(String taskInstanceId){
    	this.getHibernateTemplate().get(TaskInstance.class, taskInstanceId,LockMode.UPGRADE);
    }
    
    /* (non-Javadoc)
     * @see org.fireflow.engine.persistence.IPersistenceService#findTokenById(java.lang.String)
     */
    public IToken findTokenById(String id) {
        return (IToken) this.getHibernateTemplate().get(Token.class, id);
    }

    /* (non-Javadoc)
     * @see org.fireflow.engine.persistence.IPersistenceService#deleteTokensForNodes(java.lang.String, java.util.List)
     */
    public void deleteTokensForNodes(final String processInstanceId, final List<String> nodeIdsList) {
        this.getHibernateTemplate().execute(new HibernateCallback() {

            public Object doInHibernate(Session arg0) throws HibernateException, SQLException {
                String hql = "delete from org.fireflow.kernel.impl.Token  where processInstanceId=:processInstanceId and nodeId in (:nodeId)";
                Query query = arg0.createQuery(hql);
                query.setString("processInstanceId", processInstanceId);
                query.setParameterList("nodeId", nodeIdsList);
                return query.executeUpdate();
            }
        });
    }

    /* (non-Javadoc)
     * @see org.fireflow.engine.persistence.IPersistenceService#deleteTokensForNode(java.lang.String, java.lang.String)
     */
    public void deleteTokensForNode(final String processInstanceId, final String nodeId) {
        this.getHibernateTemplate().execute(new HibernateCallback() {

            public Object doInHibernate(Session arg0) throws HibernateException, SQLException {
                String hql = "delete from org.fireflow.kernel.impl.Token  where processInstanceId=:processInstanceId and nodeId=:nodeId";
                Query query = arg0.createQuery(hql);
                query.setString("processInstanceId", processInstanceId);
                query.setString("nodeId", nodeId);
                return query.executeUpdate();
            }
        });
    }

    /* (non-Javadoc)
     * @see org.fireflow.engine.persistence.IPersistenceService#deleteToken(org.fireflow.kernel.IToken)
     */
    public void deleteToken(IToken token) {
        this.getHibernateTemplate().delete(token);
    }


    /* (non-Javadoc)
     * @see org.fireflow.engine.persistence.IPersistenceService#findTokensForProcessInstance(java.lang.String, java.lang.String)
     */
    @SuppressWarnings("unchecked")
	public List<IToken> findTokensForProcessInstance(final String processInstanceId, final String nodeId) {
        List<IToken> result = (List<IToken>) this.getHibernateTemplate().execute(new HibernateCallback() {

            public Object doInHibernate(Session arg0) throws HibernateException, SQLException {

                Criteria criteria = arg0.createCriteria(Token.class);

                criteria.add(Expression.eq("processInstanceId", processInstanceId));
                if (nodeId != null && !nodeId.trim().equals("")) {
                    criteria.add(Expression.eq("nodeId", nodeId));
                }

                return (List<IToken>) criteria.list();
            }
        });
        return result;
    }

    /* (non-Javadoc)
     * @see org.fireflow.engine.persistence.IPersistenceService#findWorkItemById(java.lang.String)
     */
    public IWorkItem findWorkItemById(String id) {
        return (IWorkItem) this.getHibernateTemplate().get(WorkItem.class, id);
    }

    /* (non-Javadoc)
     * @see org.fireflow.engine.persistence.IPersistenceService#findAliveTaskInstanceById(java.lang.String)
     */
    public ITaskInstance findAliveTaskInstanceById(final String id) {
        ITaskInstance result = (ITaskInstance) this.getHibernateTemplate().execute(new HibernateCallback() {

            public Object doInHibernate(Session arg0) throws HibernateException, SQLException {
                Criteria criteria = arg0.createCriteria(TaskInstance.class);


                Criterion cri1 = Expression.eq("state", new Integer(ITaskInstance.INITIALIZED));
                Criterion cri2 = Expression.eq("state", new Integer(ITaskInstance.RUNNING));
                Criterion cri_or = Expression.or(cri1, cri2);

                Criterion cri0 = Expression.eq("id", id);
                Criterion cri_and = Expression.and(cri0, cri_or);
                criteria.add(cri_and);

                return criteria.uniqueResult();
            }
        });
        return result;
    }

    /* (non-Javadoc)
     * @see org.fireflow.engine.persistence.IPersistenceService#findTaskInstanceById(java.lang.String)
     */
    public ITaskInstance findTaskInstanceById(String id) {
        return (ITaskInstance) this.getHibernateTemplate().get(TaskInstance.class, id);
    }


    /* (non-Javadoc)
     * @see org.fireflow.engine.persistence.IPersistenceService#abortTaskInstance(org.fireflow.engine.impl.TaskInstance)
     */
    public void abortTaskInstance(final TaskInstance taskInstance) {
        this.getHibernateTemplate().execute(new HibernateCallback() {

            public Object doInHibernate(Session arg0) throws HibernateException, SQLException {
                Date now = rtCtx.getCalendarService().getSysDate();
                //首先Cancel TaskInstance
                taskInstance.setState(ITaskInstance.CANCELED);
                taskInstance.setEndTime(now);
                taskInstance.setCanBeWithdrawn(Boolean.FALSE);
                arg0.update(taskInstance);


                String hql = "Update org.fireflow.engine.impl.WorkItem m set m.state=:state ,m.endTime=:endTime Where m.taskInstance.id=:taskInstanceId And (m.state=0 Or m.state=1)";
                Query query = arg0.createQuery(hql);
                query.setInteger("state", IWorkItem.CANCELED);
                query.setDate("endTime", now);
                query.setString("taskInstanceId", taskInstance.getId());

                query.executeUpdate();

                return null;
            }
        });
    }

    /* (non-Javadoc)
     * @see org.fireflow.engine.persistence.IPersistenceService#getAliveWorkItemCountForTaskInstance(java.lang.String)
     */
    public Integer getAliveWorkItemCountForTaskInstance(final String taskInstanceId) {
        Object result = (Object) this.getHibernateTemplate().execute(new HibernateCallback() {

            public Object doInHibernate(Session arg0) throws HibernateException, SQLException {
                String hql = "select count(*) From org.fireflow.engine.impl.WorkItem m Where m.taskInstance.id=:taskInstanceId And (m.state=0 Or m.state=1 Or m.state=3)";
                Query query = arg0.createQuery(hql);
                query.setString("taskInstanceId", taskInstanceId);

                return query.uniqueResult();
            }
        });
        if (result instanceof Integer)return (Integer)result;
        else{
        	return new Integer(((Long)result).intValue());
        }
    }

    /* (non-Javadoc)
     * @see org.fireflow.engine.persistence.IPersistenceService#findCompletedWorkItemsForTaskInstance(java.lang.String)
     */
    @SuppressWarnings("unchecked")
	public List<IWorkItem> findCompletedWorkItemsForTaskInstance(final String taskInstanceId) {
        List<IWorkItem> result = (List<IWorkItem>) this.getHibernateTemplate().execute(new HibernateCallback() {

            public Object doInHibernate(Session arg0) throws HibernateException, SQLException {
                String hql = "From org.fireflow.engine.impl.WorkItem m Where m.taskInstance.id=:taskInstanceId And m.state=:state";
                Query query = arg0.createQuery(hql);
                query.setString("taskInstanceId", taskInstanceId);
                query.setInteger("state", IWorkItem.COMPLETED);
                return (List<IWorkItem>)query.list();
            }
        });

        return result;
    }
    
    /* (non-Javadoc)
     * @see org.fireflow.engine.persistence.IPersistenceService#findWorkItemsForTaskInstance(java.lang.String)
     */
    @SuppressWarnings("unchecked")
	public List<IWorkItem> findWorkItemsForTaskInstance(final String taskInstanceId){
        List<IWorkItem> result = (List<IWorkItem>) this.getHibernateTemplate().execute(new HibernateCallback() {

			public Object doInHibernate(Session arg0) throws HibernateException, SQLException {
            	Criteria criteria = arg0.createCriteria(WorkItem.class);
            	criteria.createAlias("taskInstance", "taskInstance");
            	criteria.add(Expression.eq("taskInstance.id", taskInstanceId));
                
            	return (List<IWorkItem>) criteria.list();
            }
        });
        return result;    	
    }

    /* (non-Javadoc)
     * @see org.fireflow.engine.persistence.IPersistenceService#findWorkItemsForTask(java.lang.String)
     */
    @SuppressWarnings("unchecked")
	public List<IWorkItem> findWorkItemsForTask(final String taskid) {
        List<IWorkItem> result = (List<IWorkItem>) this.getHibernateTemplate().execute(new HibernateCallback() {

			public Object doInHibernate(Session arg0) throws HibernateException, SQLException {
                Criteria criteria = arg0.createCriteria(WorkItem.class);

                criteria.createAlias("taskInstance", "taskInstance");
                criteria.add(Expression.eq("taskInstance.taskId", taskid));
                return (List<IWorkItem>) criteria.list();
            }
        });
        return result;
    }


    /* (non-Javadoc)
     * @see org.fireflow.engine.persistence.IPersistenceService#findProcessInstancesByProcessId(java.lang.String)
     */
    @SuppressWarnings("unchecked")
	public List<IProcessInstance> findProcessInstancesByProcessId(final String processId) {
        List<IProcessInstance> result = (List<IProcessInstance>) this.getHibernateTemplate().execute(new HibernateCallback() {

            public Object doInHibernate(Session arg0) throws HibernateException, SQLException {
                Criteria criteria = arg0.createCriteria(ProcessInstance.class);

                criteria.add(Expression.eq("processId", processId));
                
                criteria.addOrder(Order.asc("createdTime"));
                
                return (List<IProcessInstance>)criteria.list();
            }
        });
        return result;
    }

    /* (non-Javadoc)
     * @see org.fireflow.engine.persistence.IPersistenceService#findProcessInstancesByProcessIdAndVersion(java.lang.String, java.lang.Integer)
     */
    @SuppressWarnings("unchecked")
	public List<IProcessInstance> findProcessInstancesByProcessIdAndVersion(final String processId, final Integer version) {
        List<IProcessInstance> result = (List<IProcessInstance>) this.getHibernateTemplate().execute(new HibernateCallback() {

            public Object doInHibernate(Session arg0) throws HibernateException, SQLException {
                Criteria criteria = arg0.createCriteria(ProcessInstance.class);

                criteria.add(Expression.eq("processId", processId));
                criteria.add(Expression.eq("version", version));
                criteria.addOrder(Order.asc("createdTime"));                
                return (List<IProcessInstance>) criteria.list();
            }
        });
        return result;
    }

    /* (non-Javadoc)
     * @see org.fireflow.engine.persistence.IPersistenceService#findProcessInstanceById(java.lang.String)
     */
    public IProcessInstance findProcessInstanceById(String id) {
        return (IProcessInstance) this.getHibernateTemplate().get(ProcessInstance.class, id);
    }

    /* (non-Javadoc)
     * @see org.fireflow.engine.persistence.IPersistenceService#findAliveProcessInstanceById(java.lang.String)
     */
    public IProcessInstance findAliveProcessInstanceById(final String id) {
        IProcessInstance result = (IProcessInstance) this.getHibernateTemplate().execute(new HibernateCallback() {

            public Object doInHibernate(Session arg0) throws HibernateException, SQLException {
                Criteria criteria = arg0.createCriteria(ProcessInstance.class);


                Criterion cri1 = Expression.eq("state", new Integer(IProcessInstance.INITIALIZED));
                Criterion cri2 = Expression.eq("state", new Integer(IProcessInstance.RUNNING));

                Criterion cri_or = Expression.or(cri1, cri2);
                

                Criterion cri0 = Expression.eq("id", id);
                Criterion cri_and = Expression.and(cri0, cri_or);
                criteria.add(cri_and);

                return criteria.uniqueResult();
            }
        });
        return result;
    }


    /* (non-Javadoc)
     * @see org.fireflow.engine.persistence.IPersistenceService#saveOrUpdateWorkflowDefinition(org.fireflow.engine.definition.WorkflowDefinition)
     */
    public void saveOrUpdateWorkflowDefinition(WorkflowDefinition workflowDef) {
        if (workflowDef.getId() == null || workflowDef.getId().equals("")) {
            Integer latestVersion = findTheLatestVersionNumberIgnoreState(workflowDef.getProcessId());
            if (latestVersion != null) {
                workflowDef.setVersion(new Integer(latestVersion.intValue() + 1));
            } else {
                workflowDef.setVersion(new Integer(1));
            }
        }
        this.getHibernateTemplate().saveOrUpdate(workflowDef);
    }

    /* (non-Javadoc)
     * @see org.fireflow.engine.persistence.IPersistenceService#findTheLatestVersionNumber(java.lang.String)
     */
    public Integer findTheLatestVersionNumber(final String processId) {
        //取得当前最大的发布状态为有效的version值
        Integer result = (Integer) this.getHibernateTemplate().execute(new HibernateCallback() {

            public Object doInHibernate(Session arg0) throws HibernateException, SQLException {
                Query q = arg0.createQuery("select max(m.version) from WorkflowDefinition m where m.processId=:processId and m.state=:state");
                q.setString("processId", processId);
                q.setBoolean("state", Boolean.TRUE);
                Object obj = q.uniqueResult();
                if (obj != null) {
                    Integer latestVersion = (Integer) obj;
                    return latestVersion;
                } else {
                    return null;
                }
            }
        });
        return result;
    }
    
    /* (non-Javadoc)
     * @see org.fireflow.engine.persistence.IPersistenceService#findTheLatestVersionNumberIgnoreState(java.lang.String)
     */
    public Integer findTheLatestVersionNumberIgnoreState(final String processId){
        Integer result = (Integer) this.getHibernateTemplate().execute(new HibernateCallback() {
            public Object doInHibernate(Session arg0) throws HibernateException, SQLException {
                Query q = arg0.createQuery("select max(m.version) from WorkflowDefinition m where m.processId=:processId ");
                q.setString("processId", processId);
                Object obj = q.uniqueResult();
                if (obj != null) {
                    Integer latestVersion = (Integer) obj;
                    return latestVersion;
                } else {
                    return null;
                }
            }
        });
        return result;    	
    }

    /* (non-Javadoc)
     * @see org.fireflow.engine.persistence.IPersistenceService#findWorkflowDefinitionById(java.lang.String)
     */
    public WorkflowDefinition findWorkflowDefinitionById(String id) {
        return (WorkflowDefinition) this.getHibernateTemplate().get(WorkflowDefinition.class, id);
    }

    /* (non-Javadoc)
     * @see org.fireflow.engine.persistence.IPersistenceService#findWorkflowDefinitionByProcessIdAndVersionNumber(java.lang.String, int)
     */
    public WorkflowDefinition findWorkflowDefinitionByProcessIdAndVersionNumber(final String processId, final int version) {
        WorkflowDefinition workflowDef = (WorkflowDefinition) this.getHibernateTemplate().execute(new HibernateCallback() {

            public Object doInHibernate(Session arg0) throws HibernateException, SQLException {
                Criteria c = arg0.createCriteria(WorkflowDefinition.class);
                c.add(Expression.eq("processId", processId));
                c.add(Expression.eq("version", version));
                return (WorkflowDefinition) c.uniqueResult();
            }
        });
        return workflowDef;
    }

    /* (non-Javadoc)
     * @see org.fireflow.engine.persistence.IPersistenceService#findTheLatestVersionOfWorkflowDefinitionByProcessId(java.lang.String)
     */
    public WorkflowDefinition findTheLatestVersionOfWorkflowDefinitionByProcessId(String processId) {
        Integer latestVersion = this.findTheLatestVersionNumber(processId);
        return this.findWorkflowDefinitionByProcessIdAndVersionNumber(processId, latestVersion);
    }

    /* (non-Javadoc)
     * @see org.fireflow.engine.persistence.IPersistenceService#findWorkflowDefinitionsByProcessId(java.lang.String)
     */
    @SuppressWarnings("unchecked")
	public List<WorkflowDefinition> findWorkflowDefinitionsByProcessId(final String processId) {
        List<WorkflowDefinition> result = (List<WorkflowDefinition>) this.getHibernateTemplate().execute(new HibernateCallback() {

            public Object doInHibernate(Session arg0) throws HibernateException, SQLException {
                Criteria c = arg0.createCriteria(WorkflowDefinition.class);
                c.add(Expression.eq("processId", processId));
                return (List<WorkflowDefinition>)c.list();
            }
        });

        return result;
    }

    /* (non-Javadoc)
     * @see org.fireflow.engine.persistence.IPersistenceService#findAllTheLatestVersionsOfWorkflowDefinition()
     */
    @SuppressWarnings("unchecked")
	public List<WorkflowDefinition> findAllTheLatestVersionsOfWorkflowDefinition() {
        List<WorkflowDefinition> result = (List<WorkflowDefinition>) this.getHibernateTemplate().execute(new HibernateCallback() {

            public Object doInHibernate(Session arg0) throws HibernateException, SQLException {
                String hql = "select distinct model.processId from WorkflowDefinition model ";
                Query query = arg0.createQuery(hql);
                List<String> processIdList = query.list();
                
                List<WorkflowDefinition> _result = new ArrayList<WorkflowDefinition>();
                for (int i = 0; i < processIdList.size(); i++) {
                    WorkflowDefinition wfDef = findTheLatestVersionOfWorkflowDefinitionByProcessId((String) processIdList.get(i));
                    _result.add(wfDef);
                }
                return _result;
            }
        });
        return result;
    }

    /* (non-Javadoc)
     * @see org.fireflow.engine.persistence.IPersistenceService#findTodoWorkItems(java.lang.String)
     */
    public List<IWorkItem> findTodoWorkItems(final String actorId) {
        return findTodoWorkItems(actorId, null);
    }

    /* (non-Javadoc)
     * @see org.fireflow.engine.persistence.IPersistenceService#findTodoWorkItems(java.lang.String, java.lang.String)
     */
    @SuppressWarnings("unchecked")
	public List<IWorkItem> findTodoWorkItems(final String actorId, final String processInstanceId) {

        List<IWorkItem> result = (List<IWorkItem>) this.getHibernateTemplate().execute(new HibernateCallback() {

            public Object doInHibernate(Session arg0) throws HibernateException, SQLException {
                Criteria criteria = arg0.createCriteria(WorkItem.class);

                Criterion cri1 = Expression.eq("state", new Integer(IWorkItem.INITIALIZED));
                Criterion cri2 = Expression.eq("state", new Integer(IWorkItem.RUNNING));
                Criterion cri_or = Expression.or(cri1, cri2);

                if (actorId != null && !actorId.trim().equals("")) {
                    Criterion cri0 = Expression.eq("actorId", actorId);
                    Criterion cri_and = Expression.and(cri0, cri_or);
                    criteria.add(cri_and);
                } else {
                    criteria.add(cri_or);
                }

                criteria.createAlias("taskInstance", "taskInstance");
                if (processInstanceId != null && !processInstanceId.trim().equals("")) {
                    criteria.add(Expression.eq("taskInstance.processInstanceId", processInstanceId));
                }

                return (List<IWorkItem>)criteria.list();
            }
        });
        return result;
    }

    /* (non-Javadoc)
     * @see org.fireflow.engine.persistence.IPersistenceService#findTodoWorkItems(java.lang.String, java.lang.String, java.lang.String)
     */
    @SuppressWarnings("unchecked")
	public List<IWorkItem> findTodoWorkItems(final String actorId, final String processId, final String taskId) {
        List<IWorkItem> result = (List<IWorkItem>) this.getHibernateTemplate().execute(new HibernateCallback() {

            public Object doInHibernate(Session arg0) throws HibernateException, SQLException {
                Criteria criteria = arg0.createCriteria(WorkItem.class);

                Criterion cri1 = Expression.eq("state", new Integer(IWorkItem.INITIALIZED));
                Criterion cri2 = Expression.eq("state", new Integer(IWorkItem.RUNNING));
                Criterion cri_or = Expression.or(cri1, cri2);

                if (actorId != null && !actorId.trim().equals("")) {
                    Criterion cri0 = Expression.eq("actorId", actorId);
                    Criterion cri_and = Expression.and(cri0, cri_or);
                    criteria.add(cri_and);
                } else {
                    criteria.add(cri_or);
                }

                criteria.createAlias("taskInstance", "taskInstance");
                if (processId != null && !processId.trim().equals("")) {
                    criteria.add(Expression.eq("taskInstance.processId", processId));
                }

                if (taskId != null && !taskId.trim().equals("")) {
                    criteria.add(Expression.eq("taskInstance.taskId", taskId));
                }
                return (List<IWorkItem>)criteria.list();


            }
        });
        return result;
    }

    /* (non-Javadoc)
     * @see org.fireflow.engine.persistence.IPersistenceService#findHaveDoneWorkItems(java.lang.String)
     */
    public List<IWorkItem> findHaveDoneWorkItems(final String actorId) {
        return findHaveDoneWorkItems(actorId, null);
    }

    /* (non-Javadoc)
     * @see org.fireflow.engine.persistence.IPersistenceService#findHaveDoneWorkItems(java.lang.String, java.lang.String)
     */
    @SuppressWarnings("unchecked")
	public List<IWorkItem> findHaveDoneWorkItems(final String actorId, final String processInstanceId) {

        List<IWorkItem> result = (List<IWorkItem>) this.getHibernateTemplate().execute(new HibernateCallback() {

            public Object doInHibernate(Session arg0) throws HibernateException, SQLException {
                Criteria criteria = arg0.createCriteria(WorkItem.class);

                Criterion cri1 = Expression.eq("state", new Integer(IWorkItem.COMPLETED));
                Criterion cri2 = Expression.eq("state", new Integer(IWorkItem.CANCELED));
                Criterion cri_or = Expression.or(cri1, cri2);

                if (actorId != null && !actorId.trim().equals("")) {
                    Criterion cri0 = Expression.eq("actorId", actorId);
                    Criterion cri_and = Expression.and(cri0, cri_or);
                    criteria.add(cri_and);
                } else {
                    criteria.add(cri_or);
                }

                criteria.createAlias("taskInstance", "taskInstance");
                if (processInstanceId != null && !processInstanceId.trim().equals("")) {
                    criteria.add(Expression.eq("taskInstance.processInstanceId", processInstanceId));
                }

                return (List<IWorkItem>)criteria.list();
            }
        });
        return result;
    }

    /* (non-Javadoc)
     * @see org.fireflow.engine.persistence.IPersistenceService#findHaveDoneWorkItems(java.lang.String, java.lang.String, java.lang.String)
     */
    @SuppressWarnings("unchecked")
	public List<IWorkItem> findHaveDoneWorkItems(final String actorId, final String processId, final String taskId) {
        List<IWorkItem> result = (List<IWorkItem>) this.getHibernateTemplate().execute(new HibernateCallback() {

            public Object doInHibernate(Session arg0) throws HibernateException, SQLException {
                Criteria criteria = arg0.createCriteria(WorkItem.class);


                Criterion cri1 = Expression.eq("state", new Integer(IWorkItem.COMPLETED));
                Criterion cri2 = Expression.eq("state", new Integer(IWorkItem.CANCELED));
                Criterion cri_or = Expression.or(cri1, cri2);

                if (actorId != null && !actorId.trim().equals("")) {
                    Criterion cri0 = Expression.eq("actorId", actorId);
                    Criterion cri_and = Expression.and(cri0, cri_or);
                    criteria.add(cri_and);
                } else {
                    criteria.add(cri_or);
                }

                criteria.createAlias("taskInstance", "taskInstance");
                if (processId != null && !processId.trim().equals("")) {
                    criteria.add(Expression.eq("taskInstance.processId", processId));
                }

                if (taskId != null && !taskId.trim().equals("")) {
                    criteria.add(Expression.eq("taskInstance.taskId", taskId));
                }
                return (List<IWorkItem>)criteria.list();


            }
        });
        return result;
    }

    /* (non-Javadoc)
     * @see org.fireflow.engine.persistence.IPersistenceService#deleteWorkItemsInInitializedState(java.lang.String)
     */
    public void deleteWorkItemsInInitializedState(final String taskInstanceId) {
        this.getHibernateTemplate().execute(new HibernateCallback() {

            public Object doInHibernate(Session arg0) throws HibernateException, SQLException {
                String hql = "delete from org.fireflow.engine.impl.WorkItem  where taskInstance.id=? and state=0";
                Query query = arg0.createQuery(hql);
                query.setString(0, taskInstanceId);
                return query.executeUpdate();
            }
        });
    }

    /* (non-Javadoc)
     * @see org.fireflow.engine.persistence.IPersistenceService#getAliveProcessInstanceCountForParentTaskInstance(java.lang.String)
     */
    public Integer getAliveProcessInstanceCountForParentTaskInstance(final String taskInstanceId) {
        Integer result = (Integer) this.getHibernateTemplate().execute(new HibernateCallback() {

            public Object doInHibernate(Session arg0) throws HibernateException, SQLException {
                Criteria criteria = arg0.createCriteria(ProcessInstance.class);
                criteria.add(Expression.eq("parentTaskInstanceId", taskInstanceId));

                Criterion cri1 = Expression.eq("state", new Integer(IProcessInstance.INITIALIZED));
                Criterion cri2 = Expression.eq("state", new Integer(IProcessInstance.RUNNING));
//              Criterion cri3 = Expression.eq("state", new Integer(IProcessInstance.SUSPENDED));
                Criterion cri_or = Expression.or(cri1, cri2);
//              Criterion cri_or = Expression.or(cri_tmp, cri3);

                criteria.add(cri_or);

                ProjectionList prolist = Projections.projectionList();
                prolist.add(Projections.rowCount());
                criteria.setProjection(prolist);

                return criteria.uniqueResult();
            }
        });
        return result;
    }

    /* (non-Javadoc)
     * @see org.fireflow.engine.persistence.IPersistenceService#suspendProcessInstance(org.fireflow.engine.impl.ProcessInstance)
     */
    public void suspendProcessInstance(final ProcessInstance processInstance) {
        this.getHibernateTemplate().execute(new HibernateCallback() {

            public Object doInHibernate(Session arg0) throws HibernateException, SQLException {
                processInstance.setSuspended(Boolean.TRUE);
                arg0.update(processInstance);

                String hql1 = "Update org.fireflow.engine.impl.TaskInstance m Set m.suspended=:suspended Where m.processInstanceId=:processInstanceId And (m.state=0 Or m.state=1)";
                Query query1 = arg0.createQuery(hql1);
                query1.setBoolean("suspended", Boolean.TRUE);
                query1.setString("processInstanceId", processInstance.getId());
                query1.executeUpdate();

                return null;
            }
        });
    }

    /* (non-Javadoc)
     * @see org.fireflow.engine.persistence.IPersistenceService#restoreProcessInstance(org.fireflow.engine.impl.ProcessInstance)
     */
    public void restoreProcessInstance(final ProcessInstance processInstance) {
        this.getHibernateTemplate().execute(new HibernateCallback() {

            public Object doInHibernate(Session arg0) throws HibernateException, SQLException {
                processInstance.setSuspended(Boolean.FALSE);
                arg0.update(processInstance);

                String hql1 = "Update org.fireflow.engine.impl.TaskInstance m Set m.suspended=:suspended Where m.processInstanceId=:processInstanceId And (m.state=0 Or m.state=1)";
                Query query1 = arg0.createQuery(hql1);
                query1.setBoolean("suspended", Boolean.FALSE);
                query1.setString("processInstanceId", processInstance.getId());
                query1.executeUpdate();

                return null;
            }
        });
    }

    /* (non-Javadoc)
     * @see org.fireflow.engine.persistence.IPersistenceService#abortProcessInstance(org.fireflow.engine.impl.ProcessInstance)
     */
    public void abortProcessInstance(final ProcessInstance processInstance) {
        this.getHibernateTemplate().execute(new HibernateCallback() {

            public Object doInHibernate(Session arg0) throws HibernateException, SQLException {
                Date now = rtCtx.getCalendarService().getSysDate();
                processInstance.setState(IProcessInstance.CANCELED);
                processInstance.setEndTime(now);
                arg0.update(processInstance);
//更新所有的任务实例状态为canceled
                String hql1 = "Update org.fireflow.engine.impl.TaskInstance as m set m.state=:state,m.endTime=:endTime,m.canBeWithdrawn=:canBewithdrawn Where m.processInstanceId=:processInstanceId And (m.state=0 Or m.state=1)";
                Query query1 = arg0.createQuery(hql1);
                query1.setInteger("state", ITaskInstance.CANCELED);
                query1.setDate("endTime", now);
                query1.setBoolean("canBewithdrawn", Boolean.FALSE);
                query1.setString("processInstanceId", processInstance.getId());
                query1.executeUpdate();
//更新所有工作项的状态为canceled

                String hql2 = "Update org.fireflow.engine.impl.WorkItem as m set m.state=:state,m.endTime=:endTime Where m.taskInstance in (From org.fireflow.engine.impl.TaskInstance n  Where n.processInstanceId=:processInstanceId)   And (m.state=0 Or m.state=1)";
                Query query2 = arg0.createQuery(hql2);
                query2.setInteger("state", IWorkItem.CANCELED);
                query2.setDate("endTime", now);
                query2.setString("processInstanceId", processInstance.getId());
                query2.executeUpdate();
//删除所有的token
                String hql3 = "Delete org.fireflow.kernel.impl.Token where processInstanceId=:processInstanceId";
                Query query3 = arg0.createQuery(hql3);
                query3.setString("processInstanceId", processInstance.getId());
                query3.executeUpdate();

                return null;
            }
        });
    }

    /* (non-Javadoc)
     * @see org.fireflow.engine.persistence.IPersistenceService#saveOrUpdateProcessInstanceTrace(org.fireflow.engine.impl.ProcessInstanceTrace)
     */
    public void saveOrUpdateProcessInstanceTrace(ProcessInstanceTrace processInstanceTrace) {
        this.getHibernateTemplate().saveOrUpdate(processInstanceTrace);
    }
    
    /* (non-Javadoc)
     * @see org.fireflow.engine.persistence.IPersistenceService#findProcessInstanceTraces(java.lang.String)
     */
    @SuppressWarnings("unchecked")
	public List<ProcessInstanceTrace> findProcessInstanceTraces(final String processInstanceId){
		List<ProcessInstanceTrace> l = (List<ProcessInstanceTrace>)this.getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session arg0)
					throws HibernateException, SQLException {
				String hql = "From org.fireflow.engine.impl.ProcessInstanceTrace as m Where m.processInstanceId=:processInstanceId Order by m.stepNumber,m.minorNumber";
				Query q = arg0.createQuery(hql);
				q.setString("processInstanceId", processInstanceId);

				return q.list();
			}
		});

		return l;
    }
    
    public List<ProcessInstanceVar> findProcessInstanceVariable(final String processInstanceId){
		List<ProcessInstanceVar> l = (List<ProcessInstanceVar>)this.getHibernateTemplate().execute(new HibernateCallback() {
			public Object doInHibernate(Session arg0)
					throws HibernateException, SQLException {
				Criteria c = arg0.createCriteria(ProcessInstanceVar.class);
			    c.add(Expression.eq("varPrimaryKey.processInstanceId", processInstanceId));

				return c.list();
			}
		});

		return l;    	
    }
    
    public ProcessInstanceVar findProcessInstanceVariable(String processInstanceId,String name){
    	ProcessInstanceVarPk pk = new ProcessInstanceVarPk();
    	pk.setProcessInstanceId(processInstanceId);
    	pk.setName(name);
    	
    	return (ProcessInstanceVar)this.getHibernateTemplate().get(ProcessInstanceVar.class, pk);
    }
    
    public void updateProcessInstanceVariable(ProcessInstanceVar var){
    	ProcessInstanceVar oldVar = (ProcessInstanceVar)this.getHibernateTemplate().get(ProcessInstanceVar.class, var.getVarPrimaryKey());
    	if (oldVar!=null){
    		oldVar.setValue(var.getValue());
    	}
    	else{
    		saveProcessInstanceVariable(var);
    	}
//    	this.getHibernateTemplate().merge(var);
    }
    
    public void saveProcessInstanceVariable(ProcessInstanceVar var){
    	this.getHibernateTemplate().save(var);
    }

	public List<IWorkItem> findHaveDoneWorkItems(String actorId, String publishUser, int pageSize, int pageNumber)
			throws RuntimeException
	{
		// TODO Auto-generated method stub
		return null;
	}

	public List<IProcessInstance> findProcessInstanceListByCreatorId(String creatorId, String publishUser,
			int pageSize, int pageNumber) throws RuntimeException
	{
		// TODO Auto-generated method stub
		return null;
	}

	public List<IProcessInstance> findProcessInstanceListByPublishUser(String publishUser, int pageSize, int pageNumber)
			throws RuntimeException
	{
		// TODO Auto-generated method stub
		return null;
	}

	public List<IWorkItem> findTodoWorkItems(String actorId, String publishUser, int pageSize, int pageNumber)
			throws RuntimeException
	{
		// TODO Auto-generated method stub
		return null;
	}

	public Integer getHaveDoneWorkItemsCount(String actorId, String publishUser) throws RuntimeException
	{
		// TODO Auto-generated method stub
		return null;
	}

	public Integer getProcessInstanceCountByCreatorId(String creatorId, String publishUser) throws RuntimeException
	{
		// TODO Auto-generated method stub
		return null;
	}

	public Integer getProcessInstanceCountByPublishUser(String publishUser) throws RuntimeException
	{
		// TODO Auto-generated method stub
		return null;
	}

	public Integer getTodoWorkItemsCount(String actorId, String publishUser) throws RuntimeException
	{
		// TODO Auto-generated method stub
		return null;
	}

}
