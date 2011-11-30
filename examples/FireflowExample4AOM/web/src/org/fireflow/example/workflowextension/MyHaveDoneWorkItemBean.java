package org.fireflow.example.workflowextension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.fireflow.BasicManagedBean;
import org.fireflow.engine.EngineException;
import org.fireflow.engine.IWorkItem;
import org.fireflow.engine.IWorkflowSession;
import org.fireflow.engine.IWorkflowSessionCallback;
import org.fireflow.engine.RuntimeContext;
import org.fireflow.engine.persistence.IPersistenceService;
import org.fireflow.example.goods_deliver_process.workflowextension.GoodsDeliverTaskInstance;
import org.fireflow.example.loan_process.workflowextension.LoanTaskInstance;
import org.fireflow.kernel.KernelException;
import org.fireflow.security.persistence.User;
import org.operamasks.faces.annotation.Bind;
import org.operamasks.faces.annotation.ManagedBean;
import org.operamasks.faces.annotation.ManagedBeanScope;
import org.operamasks.faces.component.grid.impl.UIDataGrid;
import org.springframework.security.context.SecurityContextHolder;

@ManagedBean(scope = ManagedBeanScope.REQUEST)
@SuppressWarnings( { "unused", "serial" })
public class MyHaveDoneWorkItemBean extends BasicManagedBean {

	@SuppressWarnings("unchecked")
	@Bind(id = "grid", attribute = "value")
	private List data = null;//(List) this.doQueryMyHaveDoneWorkItems()

	@Bind(id = "grid")
	private UIDataGrid grid;

	private List doQueryMyHaveDoneWorkItems() {
		IWorkflowSession wflsession = workflowRuntimeContext
				.getWorkflowSession();
		User currentUser = (User) SecurityContextHolder.getContext()
				.getAuthentication().getPrincipal();
		final String actorId = currentUser.getId();
		List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
		try {
			List<IWorkItem> ws = (List<IWorkItem>) wflsession
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
			for (IWorkItem w : ws) {
				Map<String, Object> map = new HashMap<String, Object>();

				map.put("id", w.getId());
				map.put("state", w.getState() == 7 ? "已处理" : "已取消");
				
				if(w.getTaskInstance() instanceof GoodsDeliverTaskInstance){
					
					GoodsDeliverTaskInstance task = (GoodsDeliverTaskInstance) w
					.getTaskInstance();
					map.put("displayName", task.getDisplayName());
					map.put("goodsName", task.getGoodsName());
					map.put("sn", task.getSn());
					map.put("quantity", task.getQuantity());
					map.put("customerName", task.getCustomerName());
					map.put("endTime", w.getEndTime());
					map.put("actorId",w.getActorId() );
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
		} catch (EngineException e) {
			e.printStackTrace();
		} catch (KernelException e) {
			e.printStackTrace();
		}
		return datas;
	}

	public List getData() {
		if(data == null)
			data = this.doQueryMyHaveDoneWorkItems();
		return data;
	}

}