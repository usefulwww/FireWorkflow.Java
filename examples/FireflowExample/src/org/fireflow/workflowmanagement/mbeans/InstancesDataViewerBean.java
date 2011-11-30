package org.fireflow.workflowmanagement.mbeans;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.fireflow.BasicManagedBean;
import org.fireflow.engine.EngineException;
import org.fireflow.engine.IProcessInstance;
import org.fireflow.engine.IRuntimeContextAware;
import org.fireflow.engine.ITaskInstance;
import org.fireflow.engine.IWorkItem;
import org.fireflow.engine.persistence.IPersistenceService;
import org.fireflow.example.workflowextension.IExampleTaskInstance;
import org.fireflow.workflowmanagement.persistence.CommonWorkflowDAO;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Expression;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;

public class InstancesDataViewerBean extends BasicManagedBean {
	String workflowName4q = null;
	
	transient CommonWorkflowDAO  commonWorkflowDAO = null;
	
	List<ITaskInstance> taskInstanceList = null;
	List<IWorkItem> workItemsList = null;
	
	List<Object[]> variableList = new ArrayList();
	
	public String getWorkflowName4q() {
		return workflowName4q;
	}

	public void setWorkflowName4q(String workflowName4q) {
		this.workflowName4q = workflowName4q;
	}
	
	

	public CommonWorkflowDAO getCommonWorkflowDAO() {
		return commonWorkflowDAO;
	}

	public void setCommonWorkflowDAO(CommonWorkflowDAO commonWorkflowDAO) {
		this.commonWorkflowDAO = commonWorkflowDAO;
	}

	
	public List<ITaskInstance> getTaskInstanceList() {
		return taskInstanceList;
	}

	public void setTaskInstanceList(List<ITaskInstance> taskInstanceList) {
		this.taskInstanceList = taskInstanceList;
	}

	public List<IWorkItem> getWorkItemsList() {
		return workItemsList;
	}

	public void setWorkItemsList(List<IWorkItem> workItemsList) {
		this.workItemsList = workItemsList;
	}

	

	public List<Object[]> getVariableList() {
		return variableList;
	}

	public void setVariableList(List<Object[]> variableList) {
		this.variableList = variableList;
	}

	protected String executeBizOperQuery(){
		Criterion exp1 = Expression.like("name", "%"+this.workflowName4q+"%");
		Criterion exp2 = Expression.like("displayName", "%"+this.workflowName4q+"%");
		final Criterion exp3 = Expression.or(exp1, exp2);
		
		
		this.bizDataList = commonWorkflowDAO.findProcessInstanceByCriteria(exp3);

		return null;
	}
	
	protected String fireBizDataSelected() {
		super.fireBizDataSelected();
		
		IProcessInstance processInstance = (IProcessInstance)this.currentObject;
		
		Map varMap = processInstance.getProcessInstanceVariables();
		variableList.clear();
		Iterator itr = varMap.keySet().iterator();
		while(itr.hasNext()){
			String name = (String)itr.next();
			Object value = varMap.get(name);
			Object[] var = new Object[]{name,value};
			variableList.add(var);
		}
		
		IPersistenceService persistenceService = this.workflowRuntimeContext.getPersistenceService();
		
		this.taskInstanceList = persistenceService.findTaskInstancesForProcessInstance(processInstance.getId(), null);
		
		for (int i=0;i<taskInstanceList.size();i++){
			ITaskInstance taskInstance = taskInstanceList.get(i);
			List<IWorkItem> wis = persistenceService.findWorkItemsForTaskInstance(taskInstance.getId());
			((IExampleTaskInstance)taskInstance).setWorkItems(wis);
		}
		
		this.outcome = "SHOW_DETAIL";
		return null;
	}	
	
	public String abortProcessInstance(){
		onSelectBizData();
		this.transactionTemplate.execute(new TransactionCallback(){

			public Object doInTransaction(TransactionStatus arg0) {
				IProcessInstance processInstance = (IProcessInstance)currentObject;
				try {
					((IRuntimeContextAware)processInstance).setRuntimeContext(workflowRuntimeContext);
					processInstance.abort();
				} catch (EngineException e) {
					arg0.setRollbackOnly();
					e.printStackTrace();
				}
				
				return null;
			}
			
		});
		return this.SELF_VIEW;
	}
	
	public String suspendProcessInstance(){
		onSelectBizData();
		this.transactionTemplate.execute(new TransactionCallback(){

			public Object doInTransaction(TransactionStatus arg0) {
				IProcessInstance processInstance = (IProcessInstance)currentObject;
				try {
					
					((IRuntimeContextAware)processInstance).setRuntimeContext(workflowRuntimeContext);
					processInstance.suspend();
				} catch (EngineException e) {
					arg0.setRollbackOnly();
					e.printStackTrace();
				}
				
				return null;
			}
			
		});
		return this.SELF_VIEW;
	}
	
	public String restoreProcessInstance(){
		onSelectBizData();
		this.transactionTemplate.execute(new TransactionCallback(){

			public Object doInTransaction(TransactionStatus arg0) {
				IProcessInstance processInstance = (IProcessInstance)currentObject;
				try {
					((IRuntimeContextAware)processInstance).setRuntimeContext(workflowRuntimeContext);
					processInstance.restore();
				} catch (EngineException e) {
					arg0.setRollbackOnly();
					e.printStackTrace();
				}
				
				return null;
			}
			
		});
		return this.SELF_VIEW;
	}	
}
