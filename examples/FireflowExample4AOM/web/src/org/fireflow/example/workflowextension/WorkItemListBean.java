package org.fireflow.example.workflowextension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;

import org.fireflow.BasicManagedBean;
import org.fireflow.engine.EngineException;
import org.fireflow.engine.IWorkItem;
import org.fireflow.engine.IWorkflowSession;
import org.fireflow.example.goods_deliver_process.workflowextension.GoodsDeliverTaskInstance;
import org.fireflow.example.loan_process.workflowextension.LoanTaskInstance;
import org.fireflow.kernel.KernelException;
import org.fireflow.model.FormTask;
import org.fireflow.model.Task;
import org.fireflow.security.persistence.User;
import org.fireflow.security.util.SecurityUtilities;
import org.operamasks.faces.annotation.Action;
import org.operamasks.faces.annotation.Bind;
import org.operamasks.faces.annotation.ManagedBean;
import org.operamasks.faces.annotation.ManagedBeanScope;
import org.operamasks.faces.component.grid.impl.UIDataGrid;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;

@ManagedBean(scope = ManagedBeanScope.REQUEST)
@SuppressWarnings( { "unused", "serial" })
public class WorkItemListBean extends BasicManagedBean {

	private String selectWorkItemId;
	
	@SuppressWarnings("unchecked")
	@Bind(id = "grid", attribute = "value")
	private List data = null;

	@Bind(id = "grid")
	private UIDataGrid grid;

	/**
	 * 签收
	 */
	@Action(id = "claim")
	public void claim() {
		Object[] os = grid.getSelectedValues();
		if (os != null && os.length > 0) {
			Map<String, Object> map = (Map<String, Object>) os[0];
			final String workItemId = (String) map.get("id");
			if (workItemId == null || workItemId.equals(""))
				return;
			this.transactionTemplate
					.execute(new TransactionCallbackWithoutResult() {
						@Override
						protected void doInTransactionWithoutResult(
								TransactionStatus transactionStatus) {
							IWorkflowSession wflsession = workflowRuntimeContext
									.getWorkflowSession();
							IWorkItem wi = wflsession
									.findWorkItemById(workItemId);

							try {
								if (wi != null) {
									wi.claim();
								}
							} catch (EngineException e) {
								e.printStackTrace();
							} catch (KernelException e) {
								e.printStackTrace();
							}
						}
					});
		}
		grid.reload();
	}

	/**
	 * 处理
	 */
	@Action(id = "open")
	public String open() {
		Object[] os = grid.getSelectedValues();
		if (os != null && os.length > 0) {
			FacesContext facesContext = FacesContext.getCurrentInstance();

			try {
				Map<String, Object> map = (Map<String, Object>) os[0];
				final String workItemId = (String) map.get("id");
				selectWorkItemId = workItemId;
				IWorkflowSession wflsession = workflowRuntimeContext
						.getWorkflowSession();
				IWorkItem wi = wflsession.findWorkItemById(workItemId);
				Task task = wi.getTaskInstance().getTask();
				if (task instanceof FormTask) {
					facesContext.getExternalContext().getRequestMap().put(
							"CURRENT_WORKITEM", wi);
					if (wi != null && wi.getState() == IWorkItem.RUNNING) {
						// 如果是运行状态，返回编辑界面
						String formUri = ((FormTask) task).getEditForm()
								.getUri();
						formUri = formUri.replace("faces","xhtml");
						return "/"+formUri;
					} else {
						// 否则返回只读界面（example未实现view Form）
						// String formUri = ((FormTask)
						// task).getViewForm().getUri();
						// return formUri;

						// 返回待办任务
						return SELF_VIEW;
					}
				} else {
					// 非FormTask没有界面，直接返回当前的操作界面本身
					//return this.outcome;
				}
			} catch (EngineException e) {
				e.printStackTrace();
				return SELF_VIEW;
			}
		}
		return null;
	}


	/**
	 * 结束当前的workitem
	 * 
	 * @return
	 */
	public String completeWorkItem() {
		final String workItemId = selectWorkItemId;
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
		return "/org/fireflow/example/workflowextension/WorkItemList.xhtml";
	}
	
	private List doQueryWorkItemList(){
		// 查询待办任务
		IWorkflowSession wflsession = workflowRuntimeContext
				.getWorkflowSession();
		User currentUser = (User) SecurityUtilities
				.getCurrentUser();

		List<IWorkItem> ws = wflsession
				.findMyTodoWorkItems(currentUser.getId());
		List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
		for (IWorkItem w : ws) {
			Map<String, Object> map = new HashMap<String, Object>();

			map.put("id", w.getId());
			map.put("state", w.getState() == 0 ? "待签收" : "待处理");
			if(w.getTaskInstance() instanceof GoodsDeliverTaskInstance){
				GoodsDeliverTaskInstance task = (GoodsDeliverTaskInstance) w
				.getTaskInstance();
				map.put("displayName", task.getDisplayName());
				map.put("goodsName", task.getGoodsName());
				map.put("sn", task.getSn());
				map.put("quantity", task.getQuantity());
				map.put("customerName", task.getCustomerName());
			}
			if(w.getTaskInstance() instanceof LoanTaskInstance){
				LoanTaskInstance task = (LoanTaskInstance)w.getTaskInstance();
				map.put("displayName", task.getDisplayName());
				map.put("goodsName", task.getName());
				map.put("sn", task.getSn());
				map.put("quantity", task.getLoanValue());
				map.put("customerName", task.getApplicantName());
				map.put("endTime", w.getEndTime());
				map.put("actorId",w.getActorId() );
			}
			datas.add(map);
		}
		return datas;
	}
	
	public String getSelectWorkItemId() {
		return selectWorkItemId;
	}

	public void setSelectWorkItemId(String selectWorkItemId) {
		this.selectWorkItemId = selectWorkItemId;
	}

	public List getData() {
		if(data == null)
			data = this.doQueryWorkItemList();
		return data;
	}

}