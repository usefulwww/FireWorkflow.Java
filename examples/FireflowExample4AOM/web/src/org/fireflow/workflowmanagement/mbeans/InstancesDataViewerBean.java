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
import org.operamasks.faces.annotation.Action;
import org.operamasks.faces.annotation.Bind;
import org.operamasks.faces.annotation.ManagedBean;
import org.operamasks.faces.annotation.ManagedBeanScope;
import org.operamasks.faces.annotation.ManagedProperty;
import org.operamasks.faces.component.grid.impl.UIDataGrid;
import org.operamasks.faces.component.layout.impl.UIWindow;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;

@ManagedBean(scope=ManagedBeanScope.SESSION)
public class InstancesDataViewerBean extends BasicManagedBean {
	String workflowName4q = null;
	
	protected List bizDataList = null;
	protected String selectedObjectId = null;
	
	@ManagedProperty(value="#{CommonWorkflowDAO}")
	transient CommonWorkflowDAO  commonWorkflowDAO = null;
	
	protected Object currentObject = null;
	
	@Bind( id = "taskgrid", attribute = "value")
	private List<ITaskInstance> taskInstanceList;
	
	@Bind( id = "workitemgrid", attribute = "value")
	List<IWorkItem> workItemsList = null;
	
	@Bind( id = "variablegrid", attribute = "value")
	List<Object[]> variableList = new ArrayList();
		
	@Bind(id = "grid", attribute = "value")
	private List data ;
	
	@Bind(id = "grid")
	private UIDataGrid grid;
	
	@Bind(id = "taskgrid")
	private UIDataGrid taskgrid;
	
	@Bind(id = "variablegrid")
	private UIDataGrid variablegrid;
	
	@Bind(id = "workitemgrid")
	private UIDataGrid workitemgrid;
	
	@Bind
	private UIWindow dialog;
	
	protected void onSelectObject(){
		Object[] os = grid.getSelectedValues();
		if (os != null && os.length > 0) 
			this.currentObject = os[0];
	}

	protected String executeBizOperQuery(){
		Criterion exp1 = Expression.like("name", "%"+this.workflowName4q+"%");
		Criterion exp2 = Expression.like("displayName", "%"+this.workflowName4q+"%");
		final Criterion exp3 = Expression.or(exp1, exp2);
		
		
		this.bizDataList = commonWorkflowDAO.findProcessInstanceByCriteria(exp3);

		return null;
	}
	
	protected String fireBizDataSelected() {
		
		Object[] os = grid.getSelectedValues();
		if (os != null && os.length > 0) 
			this.currentObject = os[0];
		if(this.currentObject == null)
			return null;
		IProcessInstance processInstance = (IProcessInstance)this.currentObject;
		this.selectedObjectId = processInstance.getId();
		
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
		
		//this.outcome = "/org/fireflow/workflowmanagement/instances_data_viewer/InstanceDetailsViewer.xhtml";
		return null;
	}	
	
	/**
	 * 打开对话框
	 */
	@Action
	public void grid_ondblclick() {
		fireBizDataSelected();
		dialog.show();
		taskgrid.reload();
		variablegrid.reload();
	}
	
	@Action
	public void taskgrid_ondblclick() {
		Object[] os = taskgrid.getSelectedValues();
		if (os != null && os.length > 0) {
			IExampleTaskInstance taskInstance = (IExampleTaskInstance)os[0];
			this.workItemsList = taskInstance.getWorkItems();
		}
			
		workitemgrid.reload();
	}
	
	/**
	 * 暂停
	 * @return
	 */
	@Action(id="abort")
	public String abortProcessInstance(){
		onSelectObject();
		
		this.transactionTemplate.execute(new TransactionCallback(){

			public Object doInTransaction(TransactionStatus arg0) {
				IProcessInstance processInstance = (IProcessInstance)currentObject;
				if(processInstance == null || processInstance.getState() >= 5)
					return null;
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
		grid.reload();
		return SELF_VIEW;
	}
	
	/**
	 * 挂起
	 * @return
	 */
	@Action(id="suspend")
	public String suspendProcessInstance(){
		onSelectObject();
		this.transactionTemplate.execute(new TransactionCallback(){

			public Object doInTransaction(TransactionStatus arg0) {
				IProcessInstance processInstance = (IProcessInstance)currentObject;
				if(processInstance == null || processInstance.getState() >= 5 || processInstance.isSuspended())
					return null;
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
		grid.reload();
		return SELF_VIEW;
	}
	
	/**
	 * 恢复
	 * @return
	 */
	@Action(id="restore")
	public String restoreProcessInstance(){
		onSelectObject();
		this.transactionTemplate.execute(new TransactionCallback(){

			public Object doInTransaction(TransactionStatus arg0) {
				IProcessInstance processInstance = (IProcessInstance)currentObject;
				if(processInstance == null || processInstance.getState() >= 5 || !processInstance.isSuspended())
					return null;
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
		grid.reload();
		return SELF_VIEW;
	}

	public List getData() {
		//if(data == null || data.size()==0){
			Criterion c = Expression.sql("1=1");
			data = commonWorkflowDAO.findProcessInstanceByCriteria(c);
		//}
		return data;
	}

	public List<ITaskInstance> getTaskInstanceList() {
		fireBizDataSelected();
		return taskInstanceList;
	}

	public List<Object[]> getVariableList() {
		return variableList;
	}

	public List<IWorkItem> getWorkItemsList() {
		return workItemsList;
	}

}
