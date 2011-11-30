package org.fireflow.example.goods_deliver_process.workflowextension;

import org.fireflow.engine.EngineException;
import org.fireflow.engine.IProcessInstance;
import org.fireflow.engine.ITaskInstance;
import org.fireflow.engine.IWorkflowSession;
import org.fireflow.engine.RuntimeContext;
import org.fireflow.engine.taskinstance.ITaskInstanceCreator;
import org.fireflow.model.Task;
import org.fireflow.model.net.Activity;

public class GoodsDeliverTaskInstanceCreator implements ITaskInstanceCreator {

	public ITaskInstance createTaskInstance(IWorkflowSession currentSession,
			RuntimeContext runtimeContxt, IProcessInstance processInstance,
			Task task, Activity activity) throws EngineException {
		GoodsDeliverTaskInstance taskInst = new GoodsDeliverTaskInstance();
		
		String sn = (String)processInstance.getProcessInstanceVariable("sn");
		taskInst.setSn(sn);
		
		String customerName = (String)processInstance.getProcessInstanceVariable("customerName");
		taskInst.setCustomerName(customerName);
		
		String goodsName = (String)processInstance.getProcessInstanceVariable("goodsName");
		taskInst.setGoodsName(goodsName);
		
		Long quantity = (Long)processInstance.getProcessInstanceVariable("quantity");
		taskInst.setQuantity(quantity);
		
		return taskInst;
	}

}
