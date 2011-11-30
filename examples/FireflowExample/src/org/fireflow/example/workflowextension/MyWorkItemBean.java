package org.fireflow.example.workflowextension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;

import org.fireflow.BasicManagedBean;
import org.fireflow.engine.EngineException;
import org.fireflow.engine.IWorkItem;
import org.fireflow.engine.IWorkflowSession;
import org.fireflow.engine.IWorkflowSessionCallback;
import org.fireflow.engine.RuntimeContext;
import org.fireflow.engine.persistence.IPersistenceService;
import org.fireflow.kernel.KernelException;
import org.fireflow.model.FormTask;
import org.fireflow.model.Task;
import org.fireflow.security.persistence.User;
import org.fireflow.security.util.SecurityUtilities;
import org.fireflow.util.Utilities;
import org.fireflow.workflowmanagement.persistence.CommonWorkflowDAO;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;

/**
 * 将流程操作都集中在该bean中。
 * 流程操作如：签收工单、结束工单等。
 * @author app
 *
 */
public class MyWorkItemBean extends BasicManagedBean {

	List myTodoWorkitems = null;
	List myHaveDoneWorkItems = null;
	String selectedWorkItemId = null;

	
	//更多查询条件
	String condition_sn = null;
	String condition_applicant = null;
	String condition_customer_name = null;
	
	CommonWorkflowDAO commonWorkflowDAO = null;
	
	/**
	 * 查询待办任务
	 * 
	 * @return
	 */
	public String doQueryMyToDoWorkItems() {
		this.transactionTemplate.execute(new TransactionCallback() {

			public Object doInTransaction(TransactionStatus arg0) {
				IWorkflowSession wflsession = workflowRuntimeContext
						.getWorkflowSession();
				User currentUser = (User) SecurityUtilities.getCurrentUser();

				myTodoWorkitems = wflsession.findMyTodoWorkItems(currentUser
						.getId());

				return null;
			}

		});

		return this.outcome;
	}
	
	/**
	 * 通过更多业务属性作为条件查询待办任务
	 * 
	 * 
	 */
	public String doQueryMyToDoWorkItemsByConditions() {
		if (!Utilities.isEmpty(this.condition_applicant) && 
				!Utilities.isEmpty(this.condition_customer_name)){
			
		}
		this.transactionTemplate.execute(new TransactionCallback() {

			public Object doInTransaction(TransactionStatus arg0) {

				User currentUser = (User) SecurityUtilities.getCurrentUser();
				Map conditions = new HashMap();
				conditions.put("sn", condition_sn);
				conditions.put("actorId", currentUser.getLoginid());
				conditions.put("applicant", condition_applicant);
				conditions.put("customer_name", condition_customer_name);
				
				myTodoWorkitems = commonWorkflowDAO.findWorkItemsByConditions(conditions);

				return null;
			}

		});

		return this.outcome;
	}
	
	
	/**
	 * 查询已办任务
	 * 
	 * @return
	 */
	public String doQueryMyHaveDoneWorkItems() {
		IWorkflowSession wflsession = workflowRuntimeContext
				.getWorkflowSession();
		User currentUser = (User) SecurityContextHolder.getContext()
				.getAuthentication().getPrincipal();
		final String actorId = currentUser.getId();
		try {
			this.myHaveDoneWorkItems = (List) wflsession
					.execute(new IWorkflowSessionCallback() {

						public Object doInWorkflowSession(RuntimeContext arg0)
								throws EngineException, KernelException {
							// TODO Auto-generated method stub
							IPersistenceService persistenceService = arg0
									.getPersistenceService();
							return persistenceService
									.findHaveDoneWorkItems(actorId);
						}

					});
		} catch (EngineException e) {
			e.printStackTrace();
		} catch (KernelException e) {
			e.printStackTrace();
		}
		return outcome;
	}

	/**
	 * 签收工单
	 * 
	 * @return
	 */
	public String claimWorkItem() {
		final String workItemId = this.selectedWorkItemId;
		if (workItemId == null || workItemId.equals(""))
			return this.outcome;
		this.transactionTemplate
				.execute(new TransactionCallbackWithoutResult() {
					@Override
					protected void doInTransactionWithoutResult(
							TransactionStatus transactionStatus) {
						IWorkflowSession wflsession = workflowRuntimeContext
								.getWorkflowSession();
						IWorkItem wi = wflsession.findWorkItemById(workItemId);
						
						try {
							if (wi!=null){
								wi.claim();
							}
						} catch (EngineException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (KernelException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
		doQueryMyToDoWorkItems();
		return this.outcome;
	}

	/**
	 * 打开工单对应的业务操作界面
	 * 
	 * @return
	 */
	public String openForm() {
		FacesContext facesContext = FacesContext.getCurrentInstance();

		try {
			final String workItemId = this.selectedWorkItemId;
			IWorkflowSession wflsession = workflowRuntimeContext
					.getWorkflowSession();
			IWorkItem wi = wflsession.findWorkItemById(workItemId);
			Task task = wi.getTaskInstance().getTask();
			if (task instanceof FormTask) {
				facesContext.getExternalContext().getRequestMap().put(
						"CURRENT_WORKITEM", wi);
				if (wi != null && wi.getState() == IWorkItem.RUNNING) {
					// 如果是运行状态，返回编辑界面
					String formUri = ((FormTask) task).getEditForm().getUri();

					return formUri;
				} else {
					// 否则返回只读界面（example未实现view Form）
					//String formUri = ((FormTask) task).getViewForm().getUri();
					//return formUri;
					
					//返回待办任务
					return SELF_VIEW;
				}
			} else {
				// 非FormTask没有界面，直接返回当前的操作界面本身
				this.doQueryMyToDoWorkItems();
				return this.outcome;
			}
		} catch (EngineException e) {
			e.printStackTrace();
			this.doQueryMyToDoWorkItems();
			return this.outcome;
		}
	}

	/**
	 * 结束当前的workitem
	 * 
	 * @return
	 */
	public String completeWorkItem() {
		final String workItemId = selectedWorkItemId;
		this.transactionTemplate
				.execute(new TransactionCallbackWithoutResult() {
					@Override
					protected void doInTransactionWithoutResult(
							TransactionStatus arg0) {
						if (workItemId != null) {
							try {
								IWorkflowSession wflSession = workflowRuntimeContext
										.getWorkflowSession();
								IWorkItem wi = wflSession
										.findWorkItemById(workItemId);
								wi.complete();
							} catch (EngineException e) {
								e.printStackTrace();
								arg0.setRollbackOnly();
							} catch (KernelException e) {
								e.printStackTrace();
								arg0.setRollbackOnly();
							}
						}
					}
				});
		this.doQueryMyToDoWorkItems();
		return "MyWorkItemView";
	}

	public List getMyTodoWorkitems() {

		return myTodoWorkitems;
	}

	public void setMyTodoWorkitems(List arg) {
		this.myTodoWorkitems = arg;
	}

	public List getMyHaveDoneWorkitems() {
		return this.myHaveDoneWorkItems;
	}

	public void setMyHaveDoneWorkitems(List arg) {
		this.myHaveDoneWorkItems = arg;
	}

	public String getSelectedWorkItemId() {
		return selectedWorkItemId;
	}

	public void setSelectedWorkItemId(String selectedWorkItemId) {
		this.selectedWorkItemId = selectedWorkItemId;
	}

	public String getCondition_sn() {
		return condition_sn;
	}

	public void setCondition_sn(String condition_sn) {
		this.condition_sn = condition_sn;
	}

	public String getCondition_applicant() {
		return condition_applicant;
	}

	public void setCondition_applicant(String condition_applicant) {
		this.condition_applicant = condition_applicant;
	}

	public String getCondition_customer_name() {
		return condition_customer_name;
	}

	public void setCondition_customer_name(String condition_customer_name) {
		this.condition_customer_name = condition_customer_name;
	}

	public CommonWorkflowDAO getCommonWorkflowDAO() {
		return commonWorkflowDAO;
	}

	public void setCommonWorkflowDAO(CommonWorkflowDAO commonWorkflowDAO) {
		this.commonWorkflowDAO = commonWorkflowDAO;
	}
	
}
